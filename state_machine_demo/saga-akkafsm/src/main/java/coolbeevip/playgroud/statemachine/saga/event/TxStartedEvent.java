package coolbeevip.playgroud.statemachine.saga.event;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class TxStartedEvent {
  private String globalTxId;
  private String parentTxId;
  private String localTxId;
}
