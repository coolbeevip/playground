package coolbeevip.playgroud.statemachine.nespresso;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.testkit.javadsl.TestKit;
import coolbeevip.playgroud.statemachine.nespresso.actors.CitizConditions;
import coolbeevip.playgroud.statemachine.nespresso.actors.CitizStateMachine;
import coolbeevip.playgroud.statemachine.nespresso.message.PushCapsule;
import coolbeevip.playgroud.statemachine.nespresso.model.CitizState;
import coolbeevip.playgroud.statemachine.nespresso.message.PressPowerOFF;
import coolbeevip.playgroud.statemachine.nespresso.message.PressPowerON;
import coolbeevip.playgroud.statemachine.nespresso.message.PressTallCupButton;
import coolbeevip.playgroud.statemachine.nespresso.message.PressVentiCupButton;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.scalatest.junit.JUnitSuite;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;

@Slf4j
public class CitizStateMachineTest extends JUnitSuite {

  static ActorSystem system;

  @BeforeClass
  public static void setup() {
    system = ActorSystem.create();
  }

  @AfterClass
  public static void down() {
    TestKit.shutdownActorSystem(system);
    system = null;
  }

  @Test
  @SneakyThrows
  public void normalTest() {
    new TestKit(system) {
      {
        final ActorRef citiz = system.actorOf(Props.create(CitizStateMachine.class));
        final ActorRef probe = getRef();
        citiz.tell(new PressPowerON(), probe);
        citiz.tell(new PushCapsule(), probe);
        citiz.tell(new PressTallCupButton(), probe); // Coffee productions is completed after 5 second
        expectNoMessage();
        await().atMost(5, SECONDS).until(() -> CitizConditions.status.get(citiz.path().name()).equalsIgnoreCase(CitizState.ON.name()));

        citiz.tell(new PressPowerOFF(), probe);
        expectNoMessage();
        Assert.assertEquals(CitizConditions.status.get(citiz.path().name()), CitizState.OFF.name());
        system.stop(citiz);
      }
    };
  }

  /**
   * 开机状态 10 秒钟后自动关机
   */
  @Test
  @SneakyThrows
  public void autoPowerOffWhenOnTest() {
    new TestKit(system) {
      {
        final ActorRef citiz = system.actorOf(Props.create(CitizStateMachine.class));
        final ActorRef probe = getRef();
        citiz.tell(new PressPowerON(), probe);
        expectNoMessage();
        await().atMost(10, SECONDS).until(() -> CitizConditions.status.get(citiz.path().name()).equalsIgnoreCase(CitizState.OFF.name()));
        system.stop(citiz);
      }
    };
  }

  /**
   * 就绪状态 10 秒钟后自动关机
   */
  @Test
  @SneakyThrows
  public void autoPowerOffWhenReadyTest() {
    new TestKit(system) {
      {
        final ActorRef citiz = system.actorOf(Props.create(CitizStateMachine.class));
        final ActorRef probe = getRef();
        citiz.tell(new PressPowerON(), probe);
        citiz.tell(new PushCapsule(), probe);
        expectNoMessage();
        Assert.assertEquals(CitizConditions.status.get(citiz.path().name()), CitizState.READY.name());
        await().atMost(10, SECONDS).until(() -> CitizConditions.status.get(citiz.path().name()).equalsIgnoreCase(CitizState.OFF.name()));
        system.stop(citiz);
      }
    };
  }

  /**
   * 制作咖啡
   * */
  @Test
  @SneakyThrows
  public void makingCoffee() {
    new TestKit(system) {
      {
        final ActorRef citiz = system.actorOf(Props.create(CitizStateMachine.class));
        final ActorRef probe = getRef();
        citiz.tell(new PressPowerON(), probe);

        // tall cup
        citiz.tell(new PushCapsule(), probe);
        citiz.tell(new PressTallCupButton(), probe); // Coffee productions is completed after 5 second
        expectNoMessage();
        await().atMost(5, SECONDS).until(() -> CitizConditions.status.get(citiz.path().name()).equalsIgnoreCase(CitizState.ON.name()));

        // venti cup
        citiz.tell(new PushCapsule(), probe);
        citiz.tell(new PressVentiCupButton(), probe); // Coffee productions is completed after 10 second
        expectNoMessage();
        await().atMost(10, SECONDS).until(() -> CitizConditions.status.get(citiz.path().name()).equalsIgnoreCase(CitizState.ON.name()));
        system.stop(citiz);
      }
    };
  }

  /**
   * 制作时关机，重新开机后自动进入就绪状态
   * */
  @Test
  @SneakyThrows
  public void powerOnAfterPowerOffWhenWorking () {
    new TestKit(system) {
      {
        final ActorRef citiz = system.actorOf(Props.create(CitizStateMachine.class));
        final ActorRef probe = getRef();
        citiz.tell(new PressPowerON(), probe);
        citiz.tell(new PushCapsule(), probe);
        citiz.tell(new PressTallCupButton(), probe);
        citiz.tell(new PressPowerOFF(), probe); // Don't wait for the completion
        expectNoMessage();
        Assert.assertEquals(CitizConditions.status.get(citiz.path().name()), CitizState.OFF.name());

        // Restart
        citiz.tell(new PressPowerON(), probe);
        citiz.tell(new PressTallCupButton(), probe);
        expectNoMessage();
        await().atMost(5, SECONDS).until(() -> CitizConditions.status.get(citiz.path().name()).equalsIgnoreCase(CitizState.ON.name()));
        system.stop(citiz);
      }
    };
  }

  /**
   * 非就绪状态 + 按制作咖啡按钮
   * */

  @Test
  @SneakyThrows
  public void pressMakeButtonWhenNonReady () {
    new TestKit(system) {
      {
        final ActorRef citiz = system.actorOf(Props.create(CitizStateMachine.class));
        final ActorRef probe = getRef();
        citiz.tell(new PressPowerON(), probe);
        citiz.tell(new PressTallCupButton(), probe);
        expectNoMessage();
        Assert.assertEquals(CitizConditions.status.get(citiz.path().name()), CitizState.ON.name());
        system.stop(citiz);
      }
    };
  }
}