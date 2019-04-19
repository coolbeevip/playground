package coolbeevip.playgroud.statemachine.saga.event;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class SagaEndedEvent {
  private String globalTxId;
}
