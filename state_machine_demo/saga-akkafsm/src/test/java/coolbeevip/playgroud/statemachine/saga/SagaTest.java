package coolbeevip.playgroud.statemachine.saga;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.testkit.javadsl.TestKit;
import coolbeevip.playgroud.statemachine.saga.actors.SagaStateMachine;
import coolbeevip.playgroud.statemachine.saga.actors.TxStateMachine;
import coolbeevip.playgroud.statemachine.saga.event.SagaEndedEvent;
import coolbeevip.playgroud.statemachine.saga.event.SagaStartedEvent;
import coolbeevip.playgroud.statemachine.saga.event.TxEndedEvent;
import coolbeevip.playgroud.statemachine.saga.event.TxStartedEvent;
import java.util.UUID;
import lombok.SneakyThrows;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

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

  @Test
  @SneakyThrows
  public void normalSagaTest(){
    new TestKit(system) {{
      final String globalTxId = UUID.randomUUID().toString();
      final ActorRef saga =  system.actorOf(Props.create(SagaStateMachine.class),"saga");
      final ActorRef probe = getRef();
      saga.tell(SagaStartedEvent.builder().globalTxId(globalTxId).build(),probe);
      saga.tell(SagaEndedEvent.builder().build(),probe);
      expectNoMessage();
      system.stop(saga);
    }};
  }

  @Test
  @SneakyThrows
  public void normalTxTest(){
    new TestKit(system) {{
      final String globalTxId = UUID.randomUUID().toString();
      final String localTxId = UUID.randomUUID().toString();
      final ActorRef tx =  system.actorOf(Props.create(TxStateMachine.class),"tx");
      final ActorRef probe = getRef();
      tx.tell(TxStartedEvent.builder().parentTxId(globalTxId).localTxId(localTxId).build(),probe);
      tx.tell(TxEndedEvent.builder().localTxId(localTxId).build(),probe);
      expectNoMessage();
      system.stop(tx);
    }};
  }

  @Test
  @SneakyThrows
  public void normalTest(){
    new TestKit(system) {{
      final String globalTxId = UUID.randomUUID().toString();
      final String localTxId = UUID.randomUUID().toString();
      final ActorRef saga =  system.actorOf(Props.create(SagaStateMachine.class),"saga");
      final ActorRef tx =  system.actorOf(Props.create(TxStateMachine.class),"tx1");
      saga.tell(SagaStartedEvent.builder().globalTxId(globalTxId).build(),tx);
      saga.tell(TxStartedEvent.builder().localTxId(localTxId).parentTxId(globalTxId).globalTxId(globalTxId).build(),tx);
      saga.tell(TxEndedEvent.builder().localTxId(localTxId).build(),tx);
      saga.tell(SagaEndedEvent.builder().build(),tx);
      Thread.sleep(1000*60);
      system.stop(saga);
    }};
  }
}
