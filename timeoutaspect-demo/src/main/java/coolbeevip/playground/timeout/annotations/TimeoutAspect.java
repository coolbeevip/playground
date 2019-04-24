package coolbeevip.playground.timeout.annotations;


import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class TimeoutAspect {

  @Autowired
  TimeoutCommand timeoutCommand;

  @Around("execution(@coolbeevip.playground.timeout.annotations.Timeout * *(..)) && @annotation(timeout)")
  Object advise(ProceedingJoinPoint joinPoint, Timeout timeout) throws Throwable {
    try {
      log.info("TimeoutAspect AOP begin");
      return timeoutCommand.doTimeout(joinPoint, timeout);
    } catch (Throwable ex){
      log.info("TimeoutAspect AOP exception");
      throw ex;
    }finally {
      log.info("TimeoutAspect AOP end");
    }
  }
}
