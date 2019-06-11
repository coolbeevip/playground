package coolbeevip.playgroud.statemachine.saga.actors.model;

import coolbeevip.playgroud.statemachine.saga.actors.TxState;
import java.io.Serializable;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Getter;
import lombok.Setter;

@Builder
@Setter
@Getter
public class TxEntity implements Serializable {
  @Default
  private long beginTime = System.currentTimeMillis();
  private long endTime;
  private String parentTxId;
  private String localTxId;
  private TxState state;
}
