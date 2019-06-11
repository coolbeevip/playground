package coolbeevip.playgroud.statemachine.saga.event.consumer;

import akka.actor.ActorRef;
import com.google.common.eventbus.Subscribe;
import coolbeevip.playgroud.statemachine.saga.event.SagaStartedEvent;
import coolbeevip.playgroud.statemachine.saga.event.TxStartedEvent;
import coolbeevip.playgroud.statemachine.saga.event.base.BaseEvent;
import coolbeevip.playgroud.statemachine.saga.event.base.SagaEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SagaEventConsumer {

  @Subscribe
  public void subscribeSagaEvent(BaseEvent event) {
    log.info("receive {} {}",event.getClass().getSimpleName(),event.getGlobalTxId());
    final ActorRef sagaActor;
//    if(event instanceof SagaStartedEvent){
//      sagaActor = sagaActorHolder.put(event.getGlobalTxId());
//      sagaActor.tell(event,ActorRef.noSender());
//    } else if(event instanceof TxStartedEvent){
////      final ActorRef txActor = actorSystem.actorOf(springAkkaExtension.props(SpringAkkaExtension.classNameToSpringName(
////          TxActor.class)));
//      sagaActor = sagaActorHolder.get(event.getGlobalTxId());
//      sagaActor.tell(event,ActorRef.noSender());
//    } else {
//      sagaActor = sagaActorHolder.get(event.getGlobalTxId());
//      sagaActor.tell(event,sagaActor);
//    }
//
//    log.info("saga current state is {}",sagaActorHolder.getSagaCurrentState(event.getGlobalTxId()));
  }
}
