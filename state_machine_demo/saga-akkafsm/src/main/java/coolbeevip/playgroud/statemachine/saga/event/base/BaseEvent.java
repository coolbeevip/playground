package coolbeevip.playgroud.statemachine.saga.event.base;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public abstract class BaseEvent {
  private String globalTxId;
}
