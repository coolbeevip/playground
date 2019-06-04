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
  private Set<ActorRef> txActors = new HashSet<>();
  @Default
  private Set<TxData> txData = new HashSet<>();
  @Default
  private Map<String, PersistentFSM.FSMState> currentStates = new HashMap<>();

  public void addTxActor(ActorRef tx){
    txActors.add(tx);
  }

  public void updateCurrentState(String globalTxId, PersistentFSM.FSMState state){
    currentStates.put(globalTxId,state);
  }
}
