package coolbeevip.playgroud.statemachine.saga.model;

import akka.actor.ActorRef;
import akka.persistence.fsm.PersistentFSM;
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
public class SagaData implements StateMachineData, Serializable {
  @Default
  private long beginTime = System.currentTimeMillis();
  private long endTime;
  private String globalTxId;
  @Default
  private Map<String,ActorRef> txActors = new HashMap<>();
  @Default
  private Set<TxData> txData = new HashSet<>();
  @Default
  private Map<String, PersistentFSM.FSMState> currentStates = new HashMap<>();

  public void updateCurrentState(String globalTxId, PersistentFSM.FSMState state){
    currentStates.put(globalTxId,state);
  }
}
