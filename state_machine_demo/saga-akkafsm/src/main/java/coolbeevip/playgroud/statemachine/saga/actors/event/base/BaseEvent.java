package coolbeevip.playgroud.statemachine.saga.actors.event.base;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public abstract class BaseEvent {
  private String globalTxId;
}
