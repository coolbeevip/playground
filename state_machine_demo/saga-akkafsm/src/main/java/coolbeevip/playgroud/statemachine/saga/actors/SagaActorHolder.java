package coolbeevip.playgroud.statemachine.saga.actors;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import coolbeevip.playgroud.statemachine.saga.integration.akka.SpringAkkaExtension;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SagaActorHolder {

  private Map<String, ActorRef> sagaActors = new HashMap<>();
  private Map<String,SagaActorState> currentStates = new HashMap<>();

  @Autowired
  private ActorSystem actorSystem;

  @Autowired
  private SpringAkkaExtension springAkkaExtension;

  public ActorRef put(String globalTxId){
    ActorRef sagaActor;
    if(sagaActors.containsKey(globalTxId)){
      sagaActor = sagaActors.get(globalTxId);
    }else{
      sagaActor = actorSystem.actorOf(springAkkaExtension.props(SpringAkkaExtension.classNameToSpringName(SagaActor.class)));
      sagaActors.put(globalTxId,sagaActor);
    }
    return sagaActor;
  }

  public ActorRef get(String globalTxId){
    return sagaActors.get(globalTxId);
  }

  public void updateSagaCurrentState(String globalTxId, SagaActorState state){
    currentStates.put(globalTxId,state);
  }

  public SagaActorState getSagaCurrentState(String globalTxId){
    return currentStates.get(globalTxId);
  }
}
