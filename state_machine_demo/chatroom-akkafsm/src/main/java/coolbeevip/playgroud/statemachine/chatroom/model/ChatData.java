package coolbeevip.playgroud.statemachine.chatroom.model;

import akka.actor.ActorRef;

import java.util.HashSet;
import java.util.Set;


public final class ChatData implements FsmData {
    private Set<ActorRef> persons = new HashSet<>();

    public ChatData(ActorRef person){
        addPerson(person);
    }

    public void addPerson(ActorRef person){
        persons.add(person);
    }

    public Set<ActorRef> getPersons() {
        return persons;
    }


    public void removePerson(ActorRef loggedOutPerson) {
        persons.remove(loggedOutPerson);
    }
}
