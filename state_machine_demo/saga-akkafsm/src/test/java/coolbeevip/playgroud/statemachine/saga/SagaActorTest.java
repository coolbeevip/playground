package coolbeevip.playgroud.statemachine.saga;

import static org.junit.Assert.assertEquals;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Terminated;
import akka.persistence.fsm.PersistentFSM;
import akka.persistence.fsm.PersistentFSM.CurrentState;
import akka.testkit.javadsl.TestKit;
import coolbeevip.playgroud.statemachine.saga.actors.SagaActor;
import coolbeevip.playgroud.statemachine.saga.actors.SagaActorState;
import coolbeevip.playgroud.statemachine.saga.actors.TxActor;
import coolbeevip.playgroud.statemachine.saga.actors.TxActorState;
import coolbeevip.playgroud.statemachine.saga.event.SagaEndedEvent;
import coolbeevip.playgroud.statemachine.saga.event.SagaStartedEvent;
import coolbeevip.playgroud.statemachine.saga.event.SagaTimeoutEvent;
import coolbeevip.playgroud.statemachine.saga.event.TxAbortedEvent;
import coolbeevip.playgroud.statemachine.saga.event.TxComponsitedEvent;
import coolbeevip.playgroud.statemachine.saga.event.TxEndedEvent;
import coolbeevip.playgroud.statemachine.saga.event.TxStartedEvent;
import coolbeevip.playgroud.statemachine.saga.model.SagaData;
import java.util.UUID;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

@Slf4j
public class SagaActorTest {
  static ActorSystem system;

  @BeforeClass
  public static void setup() {
    system = ActorSystem.create("SagaActorTest");
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

      transition = expectMsgClass(PersistentFSM.Transition.class);
      assertSagaTransition(transition, saga, SagaActorState.PARTIALLY_COMMITTED, SagaActorState.COMMITTED);

      Terminated terminated = expectMsgClass(Terminated.class);
      assertEquals(terminated.getActor(), saga);

      system.stop(tx_1);
      system.stop(tx_2);
      system.stop(saga);
    }};
  }

  @Test
  @SneakyThrows
  public void sagaTxAbortedScenarioTest(){
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
      saga.tell(TxAbortedEvent.builder().globalTxId(globalTxId).parentTxId(globalTxId).localTxId(localTxId_2).build(),tx_2);

      saga.tell(TxComponsitedEvent.builder().globalTxId(globalTxId).build(),tx_1);

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
      assertSagaTransition(transition, saga, SagaActorState.PARTIALLY_ACTIVE, SagaActorState.FAILED);

      transition = expectMsgClass(PersistentFSM.Transition.class);
      assertSagaTransition(transition, saga, SagaActorState.FAILED, SagaActorState.COMPENSATED);

      Terminated terminated = expectMsgClass(Terminated.class);
      assertEquals(terminated.getActor(), saga);

      system.stop(tx_1);
      system.stop(tx_2);
      system.stop(saga);
    }};
  }

  @Test
  @SneakyThrows
  public void sagaTxEndedAndSagaTimeoutToSuspendedScenarioTest(){
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

      saga.tell(SagaTimeoutEvent.builder().globalTxId(globalTxId).build(),tx_1);

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

      transition = expectMsgClass(PersistentFSM.Transition.class);
      assertSagaTransition(transition, saga, SagaActorState.PARTIALLY_COMMITTED, SagaActorState.SUSPENDED);

      Terminated terminated = expectMsgClass(Terminated.class);
      assertEquals(terminated.getActor(), saga);

      system.stop(tx_1);
      system.stop(tx_2);
      system.stop(saga);
    }};
  }

  @Test
  @SneakyThrows
  public void sagaTxAbortedAndSagaTimeoutToSuspendedScenarioTest(){
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
      saga.tell(TxAbortedEvent.builder().globalTxId(globalTxId).parentTxId(globalTxId).localTxId(localTxId_2).build(),tx_2);

      saga.tell(SagaTimeoutEvent.builder().globalTxId(globalTxId).build(),tx_1);

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
      assertSagaTransition(transition, saga, SagaActorState.PARTIALLY_ACTIVE, SagaActorState.FAILED);

      transition = expectMsgClass(PersistentFSM.Transition.class);
      assertSagaTransition(transition, saga, SagaActorState.FAILED, SagaActorState.SUSPENDED);

      Terminated terminated = expectMsgClass(Terminated.class);
      assertEquals(terminated.getActor(), saga);

      system.stop(tx_1);
      system.stop(tx_2);
      system.stop(saga);
    }};
  }

  private static void assertSagaTransition(PersistentFSM.Transition transition, ActorRef actorRef, SagaActorState from, SagaActorState to) {
    assertEquals(transition.fsmRef(), actorRef);
    assertEquals(transition.from(), from);
    assertEquals(transition.to(), to);
  }

}
