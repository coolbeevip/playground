package coolbeevip.playgroud.statemachine.chatroom.message;


public class SomeOneLoggedOut {
    private String personName;

    public SomeOneLoggedOut(String personName) {
        this.personName = personName;
    }

    public String getPersonName() {
        return personName;
    }
}
