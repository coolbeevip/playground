package coolbeevip.playgroud.statemachine.saga.actors.event;

import coolbeevip.playgroud.statemachine.saga.actors.event.base.SagaEvent;
import lombok.Builder;
import lombok.Getter;

@Getter
public class SagaEndedEvent extends SagaEvent {

  @Builder
  public SagaEndedEvent(String globalTxId) {
    super(globalTxId);
  }
}
