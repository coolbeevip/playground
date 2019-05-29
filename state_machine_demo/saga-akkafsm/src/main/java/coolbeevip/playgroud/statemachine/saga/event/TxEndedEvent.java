package coolbeevip.playgroud.statemachine.saga.event;

import coolbeevip.playgroud.statemachine.saga.event.base.SagaEvent;
import coolbeevip.playgroud.statemachine.saga.event.base.TxEvent;
import lombok.Builder;
import lombok.Getter;


@Getter
public class TxEndedEvent extends TxEvent {

  @Builder
  public TxEndedEvent(String globalTxId, String parentTxId, String localTxId) {
    super(globalTxId, parentTxId, localTxId);
  }
}
