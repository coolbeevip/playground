package coolbeevip.playgroud.statemachine.saga.event;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class TxAbortedEvent {
  private String localTxId;
}
