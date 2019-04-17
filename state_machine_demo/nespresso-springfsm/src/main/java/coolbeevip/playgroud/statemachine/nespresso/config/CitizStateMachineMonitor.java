package coolbeevip.playgroud.statemachine.nespresso.config;

import coolbeevip.playgroud.statemachine.nespresso.message.CitizEvent;
import coolbeevip.playgroud.statemachine.nespresso.model.CitizState;
import lombok.extern.slf4j.Slf4j;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.monitor.AbstractStateMachineMonitor;
import org.springframework.statemachine.transition.Transition;

@Slf4j
public class CitizStateMachineMonitor extends AbstractStateMachineMonitor<CitizState, CitizEvent> {

  @Override
  public void transition(StateMachine<CitizState, CitizEvent> stateMachine, Transition<CitizState, CitizEvent> transition, long duration) {
    log.info("machine transition {}",stateMachine);
  }

  @Override
  public void action(StateMachine<CitizState, CitizEvent> stateMachine, Action<CitizState, CitizEvent> action, long duration) {
    log.info("machine action {}",stateMachine);
  }

}
