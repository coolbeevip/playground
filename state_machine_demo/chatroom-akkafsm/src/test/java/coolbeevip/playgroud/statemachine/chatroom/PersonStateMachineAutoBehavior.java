package coolbeevip.playgroud.statemachine.chatroom;

import akka.actor.Props;
import coolbeevip.playgroud.statemachine.chatroom.actors.PersonStateMachine;
import coolbeevip.playgroud.statemachine.chatroom.message.Logout;
import coolbeevip.playgroud.statemachine.chatroom.message.MessageFromPerson;

public class PersonStateMachineAutoBehavior extends PersonStateMachine {

    static public Props props() {
        return Props.create(PersonStateMachineAutoBehavior.class, PersonStateMachineAutoBehavior::new);
    }

    @Override
    protected void someOneLoggedInAutoBehavior(String personName){
        getSender().tell(new MessageFromPerson("hi, " + personName), getSelf());
    }

    @Override
    protected void messageFromPersonAutoBehavior(String senderNamej) {
        getSender().tell(new MessageFromPerson("hi, " + senderNamej), getSelf());
    }

    @Override
    protected void someOneLoggedOutAutoBehavior() {
        getSender().tell(new Logout(), getSelf());
    }
}
