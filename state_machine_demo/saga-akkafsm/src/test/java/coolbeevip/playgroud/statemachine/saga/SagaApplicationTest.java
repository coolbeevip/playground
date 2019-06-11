package coolbeevip.playgroud.statemachine.saga;

import static org.awaitility.Awaitility.await;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import com.google.common.eventbus.EventBus;
import coolbeevip.playgroud.statemachine.saga.actors.event.SagaEndedEvent;
import coolbeevip.playgroud.statemachine.saga.actors.event.SagaStartedEvent;
import coolbeevip.playgroud.statemachine.saga.actors.event.TxAbortedEvent;
import coolbeevip.playgroud.statemachine.saga.actors.event.TxComponsitedEvent;
import coolbeevip.playgroud.statemachine.saga.actors.event.TxEndedEvent;
import coolbeevip.playgroud.statemachine.saga.actors.event.TxStartedEvent;
import lombok.extern.slf4j.Slf4j;
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


  private ActorRef testActorRef;

//  @Test
//  public void sagaSuccessfulScenarioTest(){
//    new TestKit(actorSystem) {{
//      testActorRef = getRef();
//      final String globalTxId = UUID.randomUUID().toString();
//      getSuccessfulScenarioSagaEvents(globalTxId).stream().forEach( event -> {
//        sagaEventBus.post(event);
//      });
//      CurrentState currentState = expectMsgClass(akka.persistence.fsm.PersistentFSM.CurrentState.class);
//      await().atMost(3, SECONDS).until(() -> sagaActorHolder.getSagaCurrentState(globalTxId) == SagaActorState.COMMITTED);
//    }};
//  }
//
//  @Test
//  public void sagaTxAbortedEventScenarioTest(){
//    new TestKit(actorSystem) {{
//      final String globalTxId = UUID.randomUUID().toString();
//      getTxAbortedEventScenarioSagaEvents(globalTxId).stream().forEach( event -> {
//        sagaEventBus.post(event);
//      });
//      await().atMost(3, SECONDS).until(() -> sagaActorHolder.getSagaCurrentState(globalTxId) == SagaActorState.COMPENSATED);
//    }};
//  }
//
//  private List<SagaEvent> getSuccessfulScenarioSagaEvents(String globalTxId){
//    final String localTxId_1 = UUID.randomUUID().toString();
//    final String localTxId_2 = UUID.randomUUID().toString();
//    List<SagaEvent> sagaEvents = new ArrayList<>();
//    sagaEvents.add(SagaStartedEvent.builder().globalTxId(globalTxId).build());
//    sagaEvents.add(TxStartedEvent.builder().globalTxId(globalTxId).parentTxId(globalTxId).localTxId(localTxId_1).build());
//    sagaEvents.add(TxEndedEvent.builder().globalTxId(globalTxId).parentTxId(globalTxId).localTxId(localTxId_1).build());
//    sagaEvents.add(TxStartedEvent.builder().globalTxId(globalTxId).parentTxId(globalTxId).localTxId(localTxId_2).build());
//    sagaEvents.add(TxEndedEvent.builder().globalTxId(globalTxId).parentTxId(globalTxId).localTxId(localTxId_2).build());
//    sagaEvents.add(SagaEndedEvent.builder().globalTxId(globalTxId).build());
//    return sagaEvents;
//  }
//
//  private List<SagaEvent> getTxAbortedEventScenarioSagaEvents(String globalTxId){
//    final String localTxId_1 = UUID.randomUUID().toString();
//    final String localTxId_2 = UUID.randomUUID().toString();
//    List<SagaEvent> sagaEvents = new ArrayList<>();
//    sagaEvents.add(SagaStartedEvent.builder().globalTxId(globalTxId).build());
//    sagaEvents.add(TxStartedEvent.builder().globalTxId(globalTxId).parentTxId(globalTxId).localTxId(localTxId_1).build());
//    sagaEvents.add(TxEndedEvent.builder().globalTxId(globalTxId).parentTxId(globalTxId).localTxId(localTxId_1).build());
//    sagaEvents.add(TxStartedEvent.builder().globalTxId(globalTxId).parentTxId(globalTxId).localTxId(localTxId_2).build());
//    sagaEvents.add(TxAbortedEvent.builder().globalTxId(globalTxId).parentTxId(globalTxId).localTxId(localTxId_2).build());
//    sagaEvents.add(TxComponsitedEvent.builder().globalTxId(globalTxId).build());
//    return sagaEvents;
//  }
}
