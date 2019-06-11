package coolbeevip.playgroud.statemachine.saga.actors.model;

import coolbeevip.playgroud.statemachine.saga.actors.event.SagaDomainEvent;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
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
  @Default
  private Map<String,TxEntity> txEntityMap = new HashMap<>();
}
