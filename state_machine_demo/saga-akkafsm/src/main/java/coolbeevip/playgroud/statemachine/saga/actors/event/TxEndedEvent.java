package coolbeevip.playgroud.statemachine.saga.actors.event;

import coolbeevip.playgroud.statemachine.saga.actors.event.base.TxEvent;
import lombok.Builder;
import lombok.Getter;


@Getter
public class TxEndedEvent extends TxEvent {

  @Builder
  public TxEndedEvent(String globalTxId, String parentTxId, String localTxId) {
    super(globalTxId, parentTxId, localTxId);
  }
}
