package coolbeevip.playgroud.statemachine.saga.event.consumer;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import com.google.common.eventbus.Subscribe;
import coolbeevip.playgroud.statemachine.saga.actors.SagaActor;
import coolbeevip.playgroud.statemachine.saga.actors.SagaActorHolder;
import coolbeevip.playgroud.statemachine.saga.actors.TxActor;
import coolbeevip.playgroud.statemachine.saga.event.SagaStartedEvent;
import coolbeevip.playgroud.statemachine.saga.event.TxStartedEvent;
import coolbeevip.playgroud.statemachine.saga.event.base.SagaEvent;
import coolbeevip.playgroud.statemachine.saga.integration.akka.SpringAkkaExtension;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SagaEventConsumer {

  @Autowired
  private ActorSystem actorSystem;

  @Autowired
  private SpringAkkaExtension springAkkaExtension;

  @Autowired
  private SagaActorHolder sagaActorHolder;

  @Subscribe
  public void subscribeSagaEvent(SagaEvent event) {
    log.info("receive {} {}",event.getClass().getSimpleName(),event.getGlobalTxId());
    final ActorRef sagaActor;
    if(event instanceof SagaStartedEvent){
      sagaActor = sagaActorHolder.put(event.getGlobalTxId());
      sagaActor.tell(event,ActorRef.noSender());
    } else if(event instanceof TxStartedEvent){
//      final ActorRef txActor = actorSystem.actorOf(springAkkaExtension.props(SpringAkkaExtension.classNameToSpringName(
//          TxActor.class)));
      sagaActor = sagaActorHolder.get(event.getGlobalTxId());
      sagaActor.tell(event,ActorRef.noSender());
    } else {
      sagaActor = sagaActorHolder.get(event.getGlobalTxId());
      sagaActor.tell(event,sagaActor);
    }

    log.info("saga current state is {}",sagaActor);
  }
}
