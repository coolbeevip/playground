package coolbeevip.playgroud.statemachine.saga.actors.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Getter;
import lombok.Setter;

@Builder
@Setter
@Getter
public class SagaData implements Serializable {
  @Default
  private long beginTime = System.currentTimeMillis();
  private long endTime;
  private String globalTxId;
  private long expirationTime;
  private boolean terminated;
  @Default
  private AtomicLong compensationRunningCounter = new AtomicLong();
  @Default
  private Map<String,TxEntity> txEntityMap = new HashMap<>();

  public long getTimeout(){
    return expirationTime-System.currentTimeMillis();
  }
}
