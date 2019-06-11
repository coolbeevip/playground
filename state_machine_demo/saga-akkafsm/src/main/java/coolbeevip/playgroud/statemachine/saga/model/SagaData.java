package coolbeevip.playgroud.statemachine.saga.model;

import akka.actor.ActorRef;
import akka.persistence.fsm.PersistentFSM;
import coolbeevip.playgroud.statemachine.saga.actors.SagaActorState;
import coolbeevip.playgroud.statemachine.saga.event.SagaDomainEvent;
import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
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
