package coolbeevip.playgroud.statemachine.saga.actors.event;

import coolbeevip.playgroud.statemachine.saga.actors.event.base.TxEvent;
import lombok.Builder;
import lombok.Getter;

@Getter
public class TxStartedEvent extends TxEvent {

  @Builder
  public TxStartedEvent(String globalTxId, String parentTxId, String localTxId) {
    super(globalTxId, parentTxId, localTxId);
  }
}
