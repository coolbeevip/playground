package coolbeevip.playgroud.statemachine.saga.actors.event.base;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
public abstract class BaseEvent {
  private String globalTxId;

  @Override
  public String toString() {
    return "BaseEvent{" +
        "globalTxId='" + globalTxId + '\'' +
        '}';
  }
}
