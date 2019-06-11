package coolbeevip.playgroud.statemachine.saga.actors.event;

import coolbeevip.playgroud.statemachine.saga.actors.event.base.SagaEvent;
import lombok.Builder;
import lombok.Getter;

@Getter
public class SagaTimeoutEvent extends SagaEvent {

  @Builder
  public SagaTimeoutEvent(String globalTxId) {
    super(globalTxId);
  }
}
