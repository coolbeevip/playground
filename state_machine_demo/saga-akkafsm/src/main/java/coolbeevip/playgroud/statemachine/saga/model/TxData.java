package coolbeevip.playgroud.statemachine.saga.model;

import java.io.Serializable;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Getter;
import lombok.Setter;

@Builder
@Setter
@Getter
public class TxData implements StateMachineData, Serializable {
  @Default
  private long beginTime = System.currentTimeMillis();
  private long endTime;
  private String parentTxId;
  private String localTxId;
  private String globalTxId;
}
