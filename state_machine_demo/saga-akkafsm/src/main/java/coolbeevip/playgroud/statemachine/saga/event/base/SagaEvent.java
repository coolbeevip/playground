package coolbeevip.playgroud.statemachine.saga.event.base;

import lombok.Getter;

@Getter
public class SagaEvent extends BaseEvent {

  public SagaEvent(String globalTxId) {
    super(globalTxId);
  }
}
