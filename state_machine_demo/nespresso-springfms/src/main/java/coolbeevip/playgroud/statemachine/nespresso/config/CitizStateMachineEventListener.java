package coolbeevip.playgroud.statemachine.nespresso.config;

import coolbeevip.playgroud.statemachine.nespresso.message.CitizEvent;
import coolbeevip.playgroud.statemachine.nespresso.model.CitizState;
import lombok.extern.slf4j.Slf4j;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.statemachine.transition.Transition;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CitizStateMachineEventListener extends
    StateMachineListenerAdapter<CitizState, CitizEvent> {
  @Override
  public void transition(Transition<CitizState, CitizEvent> transition) {
    log.info("Citiz State to {}",transition.getTarget().getId().name());
  }
}
