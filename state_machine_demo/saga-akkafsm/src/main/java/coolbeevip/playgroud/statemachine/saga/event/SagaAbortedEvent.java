package coolbeevip.playgroud.statemachine.saga.event;

import coolbeevip.playgroud.statemachine.saga.event.base.SagaEvent;
import lombok.Builder;
import lombok.Getter;

@Getter
public class SagaAbortedEvent extends SagaEvent {

  @Builder
  public SagaAbortedEvent(String globalTxId) {
    super(globalTxId);
  }
}
