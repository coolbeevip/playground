package coolbeevip.playgroud.statemachine.nespresso.config;

import coolbeevip.playgroud.statemachine.nespresso.message.CitizEvent;
import coolbeevip.playgroud.statemachine.nespresso.model.CitizData;
import coolbeevip.playgroud.statemachine.nespresso.model.CitizState;
import lombok.extern.slf4j.Slf4j;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.statemachine.transition.Transition;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CitizStateMachineEventListener extends
    StateMachineListenerAdapter<CitizState, CitizEvent> {

  @Override
  public void transition(Transition<CitizState, CitizEvent> transition) {
    log.info("transition state to {}", transition.getTarget().getId().name());
  }

  @Override
  public void stateContext(StateContext<CitizState, CitizEvent> stateContext) {

  }

  @Override
  public void stateMachineStarted(StateMachine<CitizState, CitizEvent> stateMachine) {
    stateMachine.getExtendedState().getVariables().put("data", CitizData.builder().build());
  }
}
