package coolbeevip.playgroud.statemachine.saga.actors.event.consumer;

import akka.actor.ActorNotFound;
import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.actor.ActorSystem;
import akka.util.Timeout;
import com.google.common.base.Optional;
import com.google.common.eventbus.Subscribe;
import coolbeevip.playgroud.statemachine.saga.actors.SagaActor;
import coolbeevip.playgroud.statemachine.saga.actors.event.base.BaseEvent;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.omg.CORBA.TIMEOUT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;

@Component
@Slf4j
public class SagaEventConsumer {

  public static final Timeout TIMEOUT = new Timeout(100, TimeUnit.MILLISECONDS);

  @Autowired
  ActorSystem system;

  @Subscribe
  public void subscribeSagaEvent(BaseEvent event) throws Exception {
    ActorRef saga;
    String actorPath = "/user/"+event.getGlobalTxId();

    Optional<ActorRef> optional = this.getActorRefFromPath(actorPath);
    if(!optional.isPresent()){
      saga = system.actorOf(SagaActor.props(event.getGlobalTxId()), event.getGlobalTxId());
    }else{
      saga = optional.get();
    }

    saga.tell(event,ActorRef.noSender());
    log.info("receive {} ", event.toString());
  }

  public Optional<ActorRef> getActorRefFromPath(String path) throws Exception {
    try {
      ActorSelection selection = system.actorSelection(path);
      Future<ActorRef> future = selection.resolveOne(TIMEOUT);
      ActorRef ref = Await.result(future, TIMEOUT.duration());
      return Optional.of(ref);
    } catch (ActorNotFound e) {
      return Optional.absent();
    }
  }
}
