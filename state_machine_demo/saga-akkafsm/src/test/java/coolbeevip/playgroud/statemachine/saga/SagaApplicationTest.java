package coolbeevip.playgroud.statemachine.saga;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;

import akka.actor.ActorSystem;
import akka.testkit.javadsl.TestKit;
import com.google.common.eventbus.EventBus;
import coolbeevip.playgroud.statemachine.saga.actors.SagaActorHolder;
import coolbeevip.playgroud.statemachine.saga.actors.SagaActorState;
import coolbeevip.playgroud.statemachine.saga.event.SagaEndedEvent;
import coolbeevip.playgroud.statemachine.saga.event.TxEndedEvent;
import coolbeevip.playgroud.statemachine.saga.event.TxStartedEvent;
import coolbeevip.playgroud.statemachine.saga.event.base.SagaEvent;
import coolbeevip.playgroud.statemachine.saga.event.SagaStartedEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {SagaApplication.class})
@Slf4j
public class SagaApplicationTest {

  @Autowired
  @Qualifier("sagaEventBus")
  private EventBus sagaEventBus;

  @Autowired
  private ActorSystem actorSystem;

  @Autowired
  SagaActorHolder sagaActorHolder;

  @Test
  @SneakyThrows
  public void testSaga(){
    log.info("### Actor System is ready for work ###\n\n");
    new TestKit(actorSystem) {{
      final String globalTxId = UUID.randomUUID().toString();
      getSuccessfulScenarioSagaEvents(globalTxId).stream().forEach( event -> {
        sagaEventBus.post(event);
      });
      //expectNoMessage();
      await().atMost(3, SECONDS).until(() -> sagaActorHolder.getSagaCurrentState(globalTxId) == SagaActorState.COMMITTED);
      ;
    }};
  }

  private List<SagaEvent> getSuccessfulScenarioSagaEvents(String globalTxId){
    final String localTxId_1 = UUID.randomUUID().toString();
    final String localTxId_2 = UUID.randomUUID().toString();
    List<SagaEvent> sagaEvents = new ArrayList<>();
    sagaEvents.add(SagaStartedEvent.builder().globalTxId(globalTxId).build());
    sagaEvents.add(TxStartedEvent.builder().globalTxId(globalTxId).parentTxId(globalTxId).localTxId(localTxId_1).build());
    sagaEvents.add(TxEndedEvent.builder().globalTxId(globalTxId).parentTxId(globalTxId).localTxId(localTxId_1).build());
    sagaEvents.add(TxStartedEvent.builder().globalTxId(globalTxId).parentTxId(globalTxId).localTxId(localTxId_2).build());
    sagaEvents.add(TxEndedEvent.builder().globalTxId(globalTxId).parentTxId(globalTxId).localTxId(localTxId_2).build());
    sagaEvents.add(SagaEndedEvent.builder().globalTxId(globalTxId).build());
    return sagaEvents;
  }
}
