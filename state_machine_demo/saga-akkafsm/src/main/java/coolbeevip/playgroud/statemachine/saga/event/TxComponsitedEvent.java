package coolbeevip.playgroud.statemachine.saga.event;

import coolbeevip.playgroud.statemachine.saga.event.base.TxEvent;
import lombok.Builder;
import lombok.Getter;

@Getter
public class TxComponsitedEvent extends TxEvent {

  @Builder
  public TxComponsitedEvent(String globalTxId, String parentTxId, String localTxId) {
    super(globalTxId, parentTxId, localTxId);
  }
}
