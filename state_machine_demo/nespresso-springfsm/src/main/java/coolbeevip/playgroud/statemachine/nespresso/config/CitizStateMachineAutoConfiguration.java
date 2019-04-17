package coolbeevip.playgroud.statemachine.nespresso.config;

import coolbeevip.playgroud.statemachine.nespresso.message.CitizEvent;
import coolbeevip.playgroud.statemachine.nespresso.model.CitizData;
import coolbeevip.playgroud.statemachine.nespresso.model.CitizState;
import java.util.EnumSet;
import javax.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.config.EnableStateMachine;
import org.springframework.statemachine.config.EnumStateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.guard.Guard;
import org.springframework.statemachine.monitor.StateMachineMonitor;

/**
 * Citiz 胶囊咖啡机
 */

@Slf4j
@Configuration
public class CitizStateMachineAutoConfiguration {

  public static long TALL_CUP_WORKING_TIME = 1000 * 5L;
  public static long AUTOMATIC_SHUTDOWN_TIMEOUT = 1000 * 10L;

  @Autowired
  StateMachine<CitizState, CitizEvent> stateMachine;

  @Autowired
  CitizStateMachineEventListener citizStateMachineEventListener;

  @PostConstruct
  public void init(){
    stateMachine.addStateListener(citizStateMachineEventListener);
  }

  @Configuration
  @EnableStateMachine(contextEvents=false)
  public static class CitizStateMachine extends
      EnumStateMachineConfigurerAdapter<CitizState, CitizEvent> {

    @Override
    public void configure(StateMachineStateConfigurer<CitizState, CitizEvent> states)
        throws Exception {
      states
          .withStates()
          .initial(CitizState.OFF)
          .states(EnumSet.allOf(CitizState.class));
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<CitizState, CitizEvent> transitions)
        throws Exception {
      transitions
          //关机状态 + 按下开机按钮
          .withExternal()
          .source(CitizState.OFF).target(CitizState.ON).event(CitizEvent.PressPowerON)
          .action(checkCapsuleAction())
          .and()

          //关机状态 + 压入咖啡胶囊
          .withExternal()
          .source(CitizState.OFF).target(CitizState.READY).event(CitizEvent.PushCapsule)
          .and()

          //开机状态 + 按下关机按钮
          .withExternal()
          .source(CitizState.ON).target(CitizState.OFF).event(CitizEvent.PressPowerOFF)
          .and()

          //开机状态 + 10 秒无响应自动进入关机状态
          .withExternal()
          .source(CitizState.ON).target(CitizState.OFF).timerOnce(AUTOMATIC_SHUTDOWN_TIMEOUT)
          .and()

          //开机状态 + 压入咖啡胶囊
          .withExternal()
          .source(CitizState.ON).target(CitizState.READY).event(CitizEvent.PushCapsule)
          .action(pushCapsuleAction())
          .and()

          //就绪状态 + 按下制作小杯按钮（工作5秒钟）
          .withExternal()
          .source(CitizState.READY).target(CitizState.WORKING).event(CitizEvent.PressTallCupButton)
          .and()

          //就绪状态 + 按下制作大杯按钮（工作10秒钟）
          .withExternal()
          .source(CitizState.READY).target(CitizState.WORKING)
          .event(CitizEvent.PressVentiCupButton)
          .and()

          //就绪状态 + 10 秒无响应自动进入关机状态
          .withExternal()
          .source(CitizState.READY).target(CitizState.OFF).timerOnce(AUTOMATIC_SHUTDOWN_TIMEOUT)
          .and()

          //工作状态 + 5 秒钟完成一杯
          .withExternal()
          .source(CitizState.WORKING).target(CitizState.ON).timerOnce(TALL_CUP_WORKING_TIME) // TODO 如何动态设置超时，小杯5秒，大杯10秒自动结束WORKING状态
          .action(popCapsuleAction())
          .and()

          //工作状态 + 按下关机按钮
          .withExternal()
          .source(CitizState.WORKING).target(CitizState.OFF).event(CitizEvent.PressPowerOFF);
    }

    /**
     * ON -> READY
     * 放入胶囊动作，设置数据对象胶囊位=true
     * */
    @Bean
    public Action<CitizState, CitizEvent> pushCapsuleAction() {
      return new Action<CitizState, CitizEvent>() {
        @Override
        public void execute(StateContext<CitizState, CitizEvent> context) {
          CitizData citizData = (CitizData)context.getExtendedState().getVariables().get("data");
          citizData.setCapsule(true);
        }
      };
    }

    /**
     * WORKING->ON
     * 弹出胶囊动作，设置数据对象胶囊位=false
     * */
    @Bean
    public Action<CitizState, CitizEvent> popCapsuleAction() {
      return new Action<CitizState, CitizEvent>() {
        @Override
        public void execute(StateContext<CitizState, CitizEvent> context) {
          CitizData citizData = (CitizData)context.getExtendedState().getVariables().get("data");
          citizData.setCapsule(false);
        }
      };
    }

    /**
     * OFF->ON
     * 开机后检查胶囊状态，有胶囊重新发送压入胶囊事件
     * TODO 是否有直接进行状态切换的方法
     * */
    @Bean
    public Action<CitizState, CitizEvent> checkCapsuleAction() {
      return new Action<CitizState, CitizEvent>() {
        @Override
        public void execute(StateContext<CitizState, CitizEvent> context) {
          CitizData citizData = (CitizData)context.getExtendedState().getVariables().get("data");
          if(citizData.isCapsule()){
            context.getStateMachine().sendEvent(CitizEvent.PushCapsule);
          }
        }
      };
    }
  }

  @Bean
  public StateMachineMonitor<CitizState, CitizEvent> citizStateMachineMonitor() {
    return new CitizStateMachineMonitor();
  }

}
