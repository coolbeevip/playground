package coolbeevip.playgroud.statemachine.saga.event.base;

import lombok.Getter;

@Getter
public class TxEvent extends SagaEvent {
  private String parentTxId;
  private String localTxId;

  public TxEvent(String globalTxId, String parentTxId, String localTxId) {
    super(globalTxId);
    this.parentTxId = parentTxId;
    this.localTxId = localTxId;
  }
}
