package coolbeevip.playgroud.statemachine.saga.model;

import akka.actor.ActorRef;
import coolbeevip.playgroud.statemachine.saga.event.TxStartedEvent;
import java.util.HashSet;
import java.util.Set;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Getter;
import lombok.Setter;

@Builder
@Setter
@Getter
public class SagaData implements StateMachineData {
  @Default
  private long beginTime = System.currentTimeMillis();
  private long endTime;
  private String globalTxId;
  @Default
  private Set<ActorRef> txSet = new HashSet<>();
  @Default
  private Set<TxData> txData = new HashSet<>();

  public void addTxStartedEvent(ActorRef tx){
    if(!txSet.contains(tx)){
      txSet.add(tx);
    }
  }

  public void addTxEndedEvent(ActorRef tx){
    if(!txSet.contains(tx)){
      txSet.add(tx);
    }
  }

  public void addTxAbortedEvent(ActorRef tx){
    if(!txSet.contains(tx)){
      txSet.add(tx);
    }
  }

  public Set<ActorRef> getTx() {
    return txSet;
  }
}
