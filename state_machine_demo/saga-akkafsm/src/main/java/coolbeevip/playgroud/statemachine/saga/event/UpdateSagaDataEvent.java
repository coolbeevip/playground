package coolbeevip.playgroud.statemachine.saga.event;

import coolbeevip.playgroud.statemachine.saga.event.base.SagaEvent;
import coolbeevip.playgroud.statemachine.saga.model.TxData;
import lombok.Builder;
import lombok.Getter;

@Getter
public class UpdateSagaDataEvent extends SagaEvent {

  private TxData data;

  @Builder
  public UpdateSagaDataEvent(String globalTxId, TxData data) {
    super(globalTxId);
    this.data = data;
  }
}
