package coolbeevip.playgroud.statemachine.saga.actors.event.base;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
public abstract class BaseEvent implements Serializable {
  private String globalTxId;

  @Override
  public String toString() {
    return "BaseEvent{" +
        "globalTxId='" + globalTxId + '\'' +
        '}';
  }
}
