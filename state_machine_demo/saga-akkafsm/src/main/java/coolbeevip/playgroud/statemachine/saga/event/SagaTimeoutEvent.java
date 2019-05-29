package coolbeevip.playgroud.statemachine.saga.event;

import coolbeevip.playgroud.statemachine.saga.event.base.SagaEvent;
import lombok.Builder;
import lombok.Getter;

@Getter
public class SagaTimeoutEvent extends SagaEvent {

  @Builder
  public SagaTimeoutEvent(String globalTxId) {
    super(globalTxId);
  }
}
