package coolbeevip.playground.timeout.annotations;

import coolbeevip.playground.timeout.exception.TimeoutAbortedException;
import coolbeevip.playground.timeout.exception.TimeoutAbortedFailureException;
import java.lang.reflect.Method;
import java.nio.channels.ClosedByInterruptException;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import javax.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TimeoutCommand {

  private final transient Set<TimeoutTrace> timeoutTraces = new ConcurrentSkipListSet<TimeoutTrace>();

  @Value("${omega.timeout.command.delay-millis:100}")
  private int omegaTimeoutCommandDelayMillis;

  @PostConstruct
  public void init() {
    this.interrupter.scheduleWithFixedDelay(
        new Runnable() {
          @Override
          public void run() {
            try{
              TimeoutCommand.this.interrupt();
            }catch (Exception e){
              log.error("",e);
            }
          }
        },
        0, omegaTimeoutCommandDelayMillis, TimeUnit.MICROSECONDS
    );
  }

  /**
   * 包装方法调用
   */
  public Object doTimeout(final ProceedingJoinPoint point, Timeout timeoutConfig) throws Throwable {
    log.debug(point + " -> " + timeoutConfig);
    final TimeoutTrace timeoutTrace = new TimeoutTrace(point, timeoutConfig);
    this.timeoutTraces.add(timeoutTrace);
    Object output;
    try {
      output = point.proceed();
      if(timeoutTrace.getInterruptFailureException()!=null){
        throw new TimeoutAbortedFailureException(timeoutTrace.interruptFailureException);
      }
    } catch (InterruptedException e) {
      throw new TimeoutAbortedException(e);
    } catch (IllegalMonitorStateException e) {
      throw new TimeoutAbortedException(e);
    } catch (ClosedByInterruptException e) {
      throw new TimeoutAbortedException(e);
    } catch (Throwable e) {
      throw e;
    } finally {
      this.timeoutTraces.remove(timeoutTrace);
    }
    return output;
  }

  /**
   * 方法超时终止线程定义
   */
  private final transient ScheduledExecutorService interrupter =
      Executors.newSingleThreadScheduledExecutor(
          threadFactory()
      );

  private ThreadFactory threadFactory() {
    CustomizableThreadFactory tf = new CustomizableThreadFactory("timeoutcmd-");
    tf.setThreadPriority(Thread.MAX_PRIORITY);
    tf.setDaemon(true);
    tf.setThreadGroupName("timeoutcmd-group");
    return tf;
  }

  /**
   * 如果当前线程已经超时并且已经中断则移除 executor
   */
  private void interrupt() {
    synchronized (this.interrupter) {
      for (TimeoutTrace timeoutTrace : this.timeoutTraces) {
        if(timeoutTrace.interruptFailureException == null){
          if (timeoutTrace.expired()) {
            if (timeoutTrace.interrupted()) {
              this.timeoutTraces.remove(timeoutTrace);
            }
          }
        }
      }
    }
  }

  /**
   * 超时跟踪类 跟踪每个方法的超时时间，并负责中断线程
   */
  private static final class TimeoutTrace implements
      Comparable<TimeoutTrace> {

    private final transient Thread thread = Thread.currentThread();
    private final transient long startTime = System.currentTimeMillis();
    private final transient long expireTime;
    private Exception interruptFailureException = null;
    private final transient ProceedingJoinPoint joinPoint;

    public TimeoutTrace(final ProceedingJoinPoint pnt, Timeout timeoutConfig) {
      this.joinPoint = pnt;
      this.expireTime = this.startTime + timeoutConfig.unit().toMillis(timeoutConfig.value());
    }

    @Override
    public int compareTo(final TimeoutTrace obj) {
      int compare;
      if (this.expireTime > obj.expireTime) {
        compare = 1;
      } else if (this.expireTime < obj.expireTime) {
        compare = -1;
      } else {
        compare = 0;
      }
      return compare;
    }

    public Exception getInterruptFailureException() {
      return interruptFailureException;
    }

    /**
     * 超时判断
     *
     * @return 已经超时返回 TRUE
     */
    public boolean expired() {
      return this.expireTime < System.currentTimeMillis();
    }

    /**
     * 中断线程
     *
     * @return 如果线程已经中断则返回 TRUE
     */
    public boolean interrupted() {
      boolean interrupted;
      if (this.thread.isAlive()) {
        // 如果当前线程是活动状态，则发送线程中断信号
        try{
          this.thread.interrupt();
        }catch (Exception e){
          this.interruptFailureException = e;
          log.info("Failed to interrupt the thread "+this.thread.getName(),e);
          throw e;
        }
        final Method method = MethodSignature.class.cast(this.joinPoint.getSignature()).getMethod();
        log.debug("{}: interrupted on {}ms timeout (over {}ms)",
            new Object[]{method, System.currentTimeMillis() - this.startTime,
                this.expireTime - this.startTime}
        );
        interrupted = false;
      } else {
        interrupted = true;
      }
      return interrupted;
    }
  }

}
