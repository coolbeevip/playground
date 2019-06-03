package coolbeevip.playgroud.statemachine.saga.model;

import akka.actor.ActorRef;
import java.io.Serializable;
import java.util.HashSet;
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

  public void addTxActor(ActorRef tx){
    if(!txActors.contains(tx)){
      txActors.add(tx);
    }
  }
}
