package coolbeevip.playgroud.statemachine.saga.event;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class TxStartedEvent {
  private String localTxId;
  private String parentTxId;
  private String globalTxId;
}
