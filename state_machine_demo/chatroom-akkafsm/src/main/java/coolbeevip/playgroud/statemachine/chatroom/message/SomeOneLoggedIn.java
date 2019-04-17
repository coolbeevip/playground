package coolbeevip.playgroud.statemachine.chatroom.message;


public class SomeOneLoggedIn {
    private String personName;

    public SomeOneLoggedIn(String personName) {
        this.personName = personName;
    }

    public String getPersonName() {
        return personName;
    }
}
