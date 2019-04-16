package coolbeevip.playgroud.statemachine.chatroom;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.testkit.javadsl.TestKit;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import coolbeevip.playgroud.statemachine.chatroom.actors.AkkaConfig;
import coolbeevip.playgroud.statemachine.chatroom.actors.ChatStateMachine;
import coolbeevip.playgroud.statemachine.chatroom.message.Login;
import coolbeevip.playgroud.statemachine.chatroom.message.Logout;
import coolbeevip.playgroud.statemachine.chatroom.message.MessageFromPerson;
import coolbeevip.playgroud.statemachine.chatroom.model.ChatCondition;
import coolbeevip.playgroud.statemachine.chatroom.model.PersonCondition;
import coolbeevip.playgroud.statemachine.chatroom.service.ActorsConditions;
import org.junit.Assert;
import org.junit.Test;
import org.junit.AfterClass;
import org.junit.BeforeClass;

public class ChatRoomTest {

  static ActorSystem system;

  @BeforeClass
  public static void setup() {
    Config config = AkkaConfig.builder().build().parseString();
    system = ActorSystem.create("ChatTest", ConfigFactory.load(config));
  }

  @AfterClass
  public static void tearDown() {
    TestKit.shutdownActorSystem(system);
    system = null;
  }

  @Test
  public void singleUserLoginTest(){
    new TestKit(system) {{
      final ActorRef chat =  system.actorOf(Props.create(ChatStateMachine.class));
      ActorRef tom = system.actorOf(PersonStateMachineAutoBehavior.props(), "Tom");
      chat.tell(new Login(), tom);
      expectNoMessage();
      Assert.assertEquals(ActorsConditions.chatStatus, ChatCondition.AwaitingRecipient.name());
      Assert.assertEquals(ActorsConditions.status.get(tom.path().name()), PersonCondition.Connected.name());
      chat.tell(new MessageFromPerson("Hello everyone"), tom);
      Assert.assertEquals(ActorsConditions.status.get(tom.path().name()), PersonCondition.Connected.name());
      chat.tell(new Logout(),tom);
      expectNoMessage();
      system.stop(chat);
    }};
  }

  @Test
  public void multipleUserLoginTest(){
    new TestKit(system) {{
      final ActorRef chat =  system.actorOf(Props.create(ChatStateMachine.class));
      ActorRef tom = system.actorOf(PersonStateMachineAutoBehavior.props(), "Tom");
      chat.tell(new Login(), tom);
      ActorRef jerry = system.actorOf(PersonStateMachineAutoBehavior.props(), "Jerry");
      chat.tell(new Login(), jerry);
      expectNoMessage();
      Assert.assertEquals(ActorsConditions.chatStatus, ChatCondition.Online.name());
      Assert.assertEquals(ActorsConditions.status.get(tom.path().name()), PersonCondition.Talked.name());
      Assert.assertEquals(ActorsConditions.status.get(jerry.path().name()), PersonCondition.Talked.name());
      chat.tell(new Logout(),tom);
      chat.tell(new Logout(),jerry);
      expectNoMessage();
      system.stop(chat);
    }};
  }

  @Test
  public void multipleUserLoginAndSayTest(){
    new TestKit(system) {{
      final ActorRef chat =  system.actorOf(Props.create(ChatStateMachine.class));
      ActorRef tom = system.actorOf(PersonStateMachineAutoBehavior.props(), "Tom");
      chat.tell(new Login(), tom);
      ActorRef jerry = system.actorOf(PersonStateMachineAutoBehavior.props(), "Jerry");
      chat.tell(new Login(), jerry);
      expectNoMessage();
      tom.tell(new MessageFromPerson("Hello everyone"), tom);
      expectNoMessage();
      Assert.assertEquals(ActorsConditions.chatStatus, ChatCondition.Online.name());
//      Assert.assertEquals(ActorsConditions.status.get(tom.path().name()), PersonCondition.Talked.name());
//      Assert.assertEquals(ActorsConditions.status.get(jerry.path().name()), PersonCondition.Talked.name());
      chat.tell(new Logout(),tom);
      chat.tell(new Logout(),jerry);
      expectNoMessage();
      system.stop(chat);
    }};
  }

  @Test
  public void testAutoBehavior() throws InterruptedException {
    new TestKit(system) {{
      final ActorRef chat =  system.actorOf(Props.create(ChatStateMachine.class));

      ActorRef tom = system.actorOf(PersonStateMachineAutoBehavior.props(), "Tom");
      chat.tell(new Login(), tom);
      chat.tell(new MessageFromPerson("Hello everyone"), tom);

      ActorRef jerry = system.actorOf(PersonStateMachineAutoBehavior.props(), "Jerry");
      chat.tell(new Login(), jerry);

//      ActorRef spike = system.actorOf(PersonStateMachineAutoBehavior.props(), "Spike");
//      chat.tell(new Login(), spike);

      //Thread.sleep(3000);
      expectNoMessage();
      chat.tell(new Logout(), tom);
      expectNoMessage();
      //Thread.sleep(1000);

      system.stop(chat);
    }};
  }
}
