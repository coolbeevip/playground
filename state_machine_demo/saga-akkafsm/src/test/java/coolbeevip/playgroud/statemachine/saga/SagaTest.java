package coolbeevip.playgroud.statemachine.saga;

import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Terminated;
import akka.persistence.fsm.PersistentFSM;
import akka.persistence.fsm.PersistentFSM.CurrentState;
import akka.testkit.TestProbe;
import akka.testkit.javadsl.TestKit;
import coolbeevip.playgroud.statemachine.saga.actors.SagaActor;
import coolbeevip.playgroud.statemachine.saga.actors.SagaActorState;
import coolbeevip.playgroud.statemachine.saga.actors.TxActor;
import coolbeevip.playgroud.statemachine.saga.actors.TxActorState;
import coolbeevip.playgroud.statemachine.saga.event.SagaEndedEvent;
import coolbeevip.playgroud.statemachine.saga.event.SagaStartedEvent;
import coolbeevip.playgroud.statemachine.saga.event.TxEndedEvent;
import coolbeevip.playgroud.statemachine.saga.event.TxStartedEvent;
import coolbeevip.playgroud.statemachine.saga.event.UpdateSagaDataEvent;
import coolbeevip.playgroud.statemachine.saga.model.SagaData;
import coolbeevip.playgroud.statemachine.saga.model.TxData;
import java.util.UUID;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

@Slf4j
public class SagaTest {
  static ActorSystem system;

  @BeforeClass
  public static void setup() {
    system = ActorSystem.create("SagaTest");
  }

  @AfterClass
  public static void tearDown() {
    TestKit.shutdownActorSystem(system);
    system = null;
  }

  public String genPersistenceId(){
    return UUID.randomUUID().toString();
  }

  @Test
  @SneakyThrows
  public void sagaSuccessfulScenarioTest(){
    new TestKit(system) {{
      final String globalTxId = UUID.randomUUID().toString();
      final String localTxId_1 = UUID.randomUUID().toString();
      final String localTxId_2 = UUID.randomUUID().toString();

      ActorRef saga = system.actorOf(SagaActor.props(genPersistenceId()),"saga");
      watch(saga);
      saga.tell(new PersistentFSM.SubscribeTransitionCallBack(getRef()), getRef());

      saga.tell(SagaStartedEvent.builder().globalTxId(globalTxId).build(),getRef());
      ActorRef tx_1 = system.actorOf(TxActor.props(genPersistenceId()),"tx1");
      saga.tell(TxStartedEvent.builder().globalTxId(globalTxId).parentTxId(globalTxId).localTxId(localTxId_1).build(),tx_1);
      saga.tell(TxEndedEvent.builder().globalTxId(globalTxId).parentTxId(globalTxId).localTxId(localTxId_1).build(),tx_1);
      ActorRef tx_2 = system.actorOf(TxActor.props(genPersistenceId()),"tx2");
      saga.tell(TxStartedEvent.builder().globalTxId(globalTxId).parentTxId(globalTxId).localTxId(localTxId_2).build(),tx_2);
      saga.tell(TxEndedEvent.builder().globalTxId(globalTxId).parentTxId(globalTxId).localTxId(localTxId_2).build(),tx_2);
      saga.tell(SagaEndedEvent.builder().globalTxId(globalTxId).build(),getRef());

      //expect
      CurrentState currentState = expectMsgClass(PersistentFSM.CurrentState.class);
      assertEquals(SagaActorState.IDEL,currentState.state());

      SagaData sagaData = expectMsgClass(SagaData.class);
      assertEquals(sagaData.getGlobalTxId(),globalTxId);

      PersistentFSM.Transition transition = expectMsgClass(PersistentFSM.Transition.class);
      assertSagaTransition(transition, saga, SagaActorState.IDEL, SagaActorState.READY);

      transition = expectMsgClass(PersistentFSM.Transition.class);
      assertSagaTransition(transition, saga, SagaActorState.READY, SagaActorState.PARTIALLY_ACTIVE);

      transition = expectMsgClass(PersistentFSM.Transition.class);
      assertSagaTransition(transition, saga, SagaActorState.PARTIALLY_ACTIVE, SagaActorState.PARTIALLY_COMMITTED);

      transition = expectMsgClass(PersistentFSM.Transition.class);
      assertSagaTransition(transition, saga, SagaActorState.PARTIALLY_COMMITTED, SagaActorState.PARTIALLY_ACTIVE);

      transition = expectMsgClass(PersistentFSM.Transition.class);
      assertSagaTransition(transition, saga, SagaActorState.PARTIALLY_ACTIVE, SagaActorState.PARTIALLY_COMMITTED);

      sagaData = expectMsgClass(SagaData.class);
      assertEquals(sagaData.getGlobalTxId(),globalTxId);
      assertNotNull(sagaData.getBeginTime());
      assertNotNull(sagaData.getEndTime());
      assertThat(sagaData.getEndTime(),greaterThan(sagaData.getBeginTime()));

      transition = expectMsgClass(PersistentFSM.Transition.class);
      assertSagaTransition(transition, saga, SagaActorState.PARTIALLY_COMMITTED, SagaActorState.COMMITTED);

      Terminated terminated = expectMsgClass(Terminated.class);
      assertEquals(terminated.getActor(), saga);

      system.stop(saga);
    }};
  }

  @Test
  @SneakyThrows
  public void txSuccessfulScenarioTest(){
    new TestKit(system) {{
      final String globalTxId = UUID.randomUUID().toString();
      final String localTxId = UUID.randomUUID().toString();

      String persistenceId = UUID.randomUUID().toString();
      ActorRef tx = system.actorOf(TxActor.props(persistenceId));

      watch(tx);
      tx.tell(new PersistentFSM.SubscribeTransitionCallBack(getRef()), getRef());

      tx.tell(TxStartedEvent.builder().globalTxId(globalTxId).parentTxId(globalTxId).localTxId(localTxId).build(),getRef());
      tx.tell(TxEndedEvent.builder().globalTxId(globalTxId).parentTxId(globalTxId).localTxId(localTxId).build(),getRef());

      CurrentState currentState = expectMsgClass(PersistentFSM.CurrentState.class);
      assertEquals(TxActorState.IDEL,currentState.state());

      TxData txData = expectMsgClass(TxData.class);
      assertEquals(txData.getGlobalTxId(),globalTxId);

      PersistentFSM.Transition transition = expectMsgClass(PersistentFSM.Transition.class);
      assertTxTransition(transition, tx, TxActorState.IDEL, TxActorState.ACTIVE);

      txData = expectMsgClass(TxData.class);
      assertEquals(txData.getGlobalTxId(),globalTxId);
      assertNotNull(txData.getBeginTime());
      assertNotNull(txData.getEndTime());
      assertThat(txData.getEndTime(),greaterThan(txData.getBeginTime()));

      transition = expectMsgClass(PersistentFSM.Transition.class);
      assertTxTransition(transition, tx, TxActorState.ACTIVE, TxActorState.COMMITTED);

      system.stop(tx);
    }};
  }


  private static void assertSagaTransition(PersistentFSM.Transition transition, ActorRef actorRef, SagaActorState from, SagaActorState to) {
    assertEquals(transition.fsmRef(), actorRef);
    assertEquals(transition.from(), from);
    assertEquals(transition.to(), to);
  }

  private static void assertTxTransition(PersistentFSM.Transition transition, ActorRef actorRef, TxActorState from, TxActorState to) {
    assertEquals(transition.fsmRef(), actorRef);
    assertEquals(transition.from(), from);
    assertEquals(transition.to(), to);
  }

}
