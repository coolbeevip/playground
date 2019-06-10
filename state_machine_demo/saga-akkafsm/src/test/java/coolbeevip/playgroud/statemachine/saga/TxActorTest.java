package coolbeevip.playgroud.statemachine.saga;

import static org.junit.Assert.assertEquals;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.persistence.fsm.PersistentFSM;
import akka.persistence.fsm.PersistentFSM.CurrentState;
import akka.testkit.javadsl.TestKit;
import coolbeevip.playgroud.statemachine.saga.actors.TxActor;
import coolbeevip.playgroud.statemachine.saga.actors.TxActorState;
import coolbeevip.playgroud.statemachine.saga.event.TxAbortedEvent;
import coolbeevip.playgroud.statemachine.saga.event.TxComponsitedEvent;
import coolbeevip.playgroud.statemachine.saga.event.TxEndedEvent;
import coolbeevip.playgroud.statemachine.saga.event.TxStartedEvent;
import java.util.UUID;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

@Slf4j
public class TxActorTest {
  static ActorSystem system;

  @BeforeClass
  public static void setup() {
    system = ActorSystem.create("TxActorTest");
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
  public void txSuccessfulScenarioTest(){
    new TestKit(system) {{
      final String globalTxId = UUID.randomUUID().toString();
      final String localTxId = UUID.randomUUID().toString();

      ActorRef tx = system.actorOf(TxActor.props(genPersistenceId()));

      watch(tx);
      tx.tell(new PersistentFSM.SubscribeTransitionCallBack(getRef()), getRef());

      tx.tell(TxStartedEvent.builder().globalTxId(globalTxId).parentTxId(globalTxId).localTxId(localTxId).build(),getRef());
      tx.tell(TxEndedEvent.builder().globalTxId(globalTxId).parentTxId(globalTxId).localTxId(localTxId).build(),getRef());

      CurrentState currentState = expectMsgClass(CurrentState.class);
      assertEquals(TxActorState.IDEL,currentState.state());

      PersistentFSM.Transition transition = expectMsgClass(PersistentFSM.Transition.class);
      assertTxTransition(transition, tx, TxActorState.IDEL, TxActorState.ACTIVE);

      transition = expectMsgClass(PersistentFSM.Transition.class);
      assertTxTransition(transition, tx, TxActorState.ACTIVE, TxActorState.COMMITTED);

      system.stop(tx);
    }};
  }

  @Test
  @SneakyThrows
  public void txFailedScenarioTest(){
    new TestKit(system) {{
      final String globalTxId = UUID.randomUUID().toString();
      final String localTxId = UUID.randomUUID().toString();

      ActorRef tx = system.actorOf(TxActor.props(genPersistenceId()));

      watch(tx);
      tx.tell(new PersistentFSM.SubscribeTransitionCallBack(getRef()), getRef());

      tx.tell(TxStartedEvent.builder().globalTxId(globalTxId).parentTxId(globalTxId).localTxId(localTxId).build(),getRef());
      tx.tell(TxAbortedEvent.builder().globalTxId(globalTxId).parentTxId(globalTxId).localTxId(localTxId).build(),getRef());

      CurrentState currentState = expectMsgClass(CurrentState.class);
      assertEquals(TxActorState.IDEL,currentState.state());

      PersistentFSM.Transition transition = expectMsgClass(PersistentFSM.Transition.class);
      assertTxTransition(transition, tx, TxActorState.IDEL, TxActorState.ACTIVE);

      transition = expectMsgClass(PersistentFSM.Transition.class);
      assertTxTransition(transition, tx, TxActorState.ACTIVE, TxActorState.FAILED);

      system.stop(tx);
    }};
  }

  @Test
  @SneakyThrows
  public void txCompensatedScenarioTest(){
    new TestKit(system) {{
      final String globalTxId = UUID.randomUUID().toString();
      final String localTxId = UUID.randomUUID().toString();

      ActorRef tx = system.actorOf(TxActor.props(genPersistenceId()));

      watch(tx);
      tx.tell(new PersistentFSM.SubscribeTransitionCallBack(getRef()), getRef());

      tx.tell(TxStartedEvent.builder().globalTxId(globalTxId).parentTxId(globalTxId).localTxId(localTxId).build(),getRef());
      tx.tell(TxEndedEvent.builder().globalTxId(globalTxId).parentTxId(globalTxId).localTxId(localTxId).build(),getRef());
      tx.tell(TxComponsitedEvent.builder().globalTxId(globalTxId).parentTxId(globalTxId).localTxId(localTxId).build(),getRef());

      CurrentState currentState = expectMsgClass(CurrentState.class);
      assertEquals(TxActorState.IDEL,currentState.state());

      PersistentFSM.Transition transition = expectMsgClass(PersistentFSM.Transition.class);
      assertTxTransition(transition, tx, TxActorState.IDEL, TxActorState.ACTIVE);

      transition = expectMsgClass(PersistentFSM.Transition.class);
      assertTxTransition(transition, tx, TxActorState.ACTIVE, TxActorState.COMMITTED);

      transition = expectMsgClass(PersistentFSM.Transition.class);
      assertTxTransition(transition, tx, TxActorState.COMMITTED, TxActorState.COMPENSATED);
      system.stop(tx);
    }};
  }

  private static void assertTxTransition(PersistentFSM.Transition transition, ActorRef actorRef, TxActorState from, TxActorState to) {
    assertEquals(transition.fsmRef(), actorRef);
    assertEquals(transition.from(), from);
    assertEquals(transition.to(), to);
  }

}
