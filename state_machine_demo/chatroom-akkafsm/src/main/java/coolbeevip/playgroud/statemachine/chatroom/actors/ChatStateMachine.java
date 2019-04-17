package coolbeevip.playgroud.statemachine.chatroom.actors;

import akka.actor.*;
import com.typesafe.config.ConfigValue;
import coolbeevip.playgroud.statemachine.chatroom.message.LoggedIn;
import coolbeevip.playgroud.statemachine.chatroom.message.Login;
import coolbeevip.playgroud.statemachine.chatroom.model.ChatCondition;
import coolbeevip.playgroud.statemachine.chatroom.model.ChatData;
import coolbeevip.playgroud.statemachine.chatroom.model.FsmData;
import coolbeevip.playgroud.statemachine.chatroom.model.Uninitialized;
import coolbeevip.playgroud.statemachine.chatroom.service.ActorsConditions;
import coolbeevip.playgroud.statemachine.chatroom.util.MyUtils;
import coolbeevip.playgroud.statemachine.chatroom.message.Logout;
import coolbeevip.playgroud.statemachine.chatroom.message.MessageDelivered;
import coolbeevip.playgroud.statemachine.chatroom.message.MessageFromChat;
import coolbeevip.playgroud.statemachine.chatroom.message.MessageFromPerson;
import coolbeevip.playgroud.statemachine.chatroom.message.SomeOneLoggedIn;
import coolbeevip.playgroud.statemachine.chatroom.message.SomeOneLoggedOut;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ChatStateMachine extends AbstractFSM<ChatCondition, FsmData> {

  {
    startWith(ChatCondition.Available, Uninitialized.Uninitialized);

    when(ChatCondition.Available,
            matchEvent(Login.class, FsmData.class,
                    (loginObj, data) -> {
                      ChatData chatData = new ChatData(getSender());
                      loginPerson(getSender(), chatData);
                      return goTo(ChatCondition.AwaitingRecipient).using(chatData);
                    }
            )
    );

    when(ChatCondition.AwaitingRecipient,
            matchEvent(Login.class, ChatData.class,
                    (loginObj, data) -> {
                      loginPerson(getSender(), data);
                      return goTo(ChatCondition.Online).using(data);
                    }
            ).event(MessageFromPerson.class, FsmData.class,
                    (messageFromPersonObj, data) -> {
                      matchMessageFromPersonNoRecipient(getSender(), messageFromPersonObj.getMessage());
                      return stay();
                    }
            ).event(Logout.class, ChatData.class,
                    (logoutObj, dataObj) -> {
                      logoutPerson(getSender(), getContext(), dataObj);
                      return goTo(ChatCondition.Available).using(dataObj);
                    }
            )
    );

    when(ChatCondition.Online,
            matchEvent(MessageFromPerson.class, ChatData.class,
                    (messageFromPersonObj, data) -> {
                      matchMessageFromPerson(getSender(), messageFromPersonObj.getMessage(), data);
                      return stay();
                    }
            ).event(Login.class, ChatData.class,
                    (loginObj, data) -> {
                      loginPerson(getSender(), data);
                      return stay().using(data);
                    }
            ).event(MessageFromPerson.class, ChatData.class,
                    (messageFromPersonObj, data) -> {
                      matchMessageFromPerson(getSender(), messageFromPersonObj.getMessage(), data);
                      return stay();
                    }
            ).event(Logout.class, ChatData.class,
                    (logoutObj, dataObj) -> {
                      logoutPerson(getSender(), getContext(), dataObj);
                      State newState = null;
                      if (dataObj.getPersons().size() > 1) {
                        newState = stay();
                      } else {
                        newState = goTo(ChatCondition.AwaitingRecipient);
                      }
                      return newState.using(dataObj);
                    }
            )
    );

    onTransition(
            matchState(null, null, (from, to) -> {
              ActorsConditions.chatStatus = to.name();
            })
    );

    initialize();
  }

  @Override
  public void preStart() {
    logMessage("-Start Chat-");
  }

  @Override
  public void postStop() {
    logMessage("-Finish Chat-");
  }

  private void loginPerson(ActorRef loggedInPerson, ChatData chatData) {
    String personName = loggedInPerson.path().name();
    chatData.addPerson(loggedInPerson);
    loggedInPerson.tell(new LoggedIn(), getSelf());

    chatData.getPersons().stream().filter(person -> !person.equals(loggedInPerson)).forEach(person -> person.tell(new SomeOneLoggedIn(personName), getSelf()));
    memAndLogMessage(personName + " connected to chat");
  }

  private void logoutPerson(ActorRef loggedOutPerson, AbstractActor.ActorContext context, ChatData chatData) {
    String personName = loggedOutPerson.path().name();
    chatData.removePerson(loggedOutPerson);
    context.stop(loggedOutPerson);

    chatData.getPersons().forEach(person -> person.tell(new SomeOneLoggedOut(personName), getSelf()));
    memAndLogMessage(personName + " disconnected from chat");
  }

  private void matchMessageFromPersonNoRecipient(ActorRef messagePerson, String message) {
    memAndLogMessage(messagePerson.path().name() + ": " + message);
    messagePerson.tell(new MessageFromChat("no recipient"), getSelf());
    memAndLogMessage("no recipient");
  }

  private void matchMessageFromPerson(ActorRef messagePerson, String Message, ChatData chatData) {
    String senderName = messagePerson.path().name();
    chatData.getPersons().stream().filter(person -> !person.equals(messagePerson)).forEach(person -> person.tell(new MessageFromPerson(Message, senderName), getSelf()));
    messagePerson.tell(new MessageDelivered(), getSelf());
    memAndLogMessage(senderName + ": " + Message);
  }

  private void memAndLogMessage(String message) {
    logMessage(message);
    ActorsConditions.chatBody.add(message);
  }

  private void logMessage(String message) {
    ConfigValue fileNameObj = getContext().getSystem().settings().config().getObject("akka").get("chat_history_file");
    String fileName = (String) fileNameObj.unwrapped();
    MyUtils.memAndLogChatMessage(message, fileName, log);
  }
}
