package coolbeevip.playgroud.statemachine.chatroom.message;


public class MessageFromPerson {
    private final String message;
    private final String senderName;

    public MessageFromPerson(String message) {
        this.message = message;
        this.senderName = "";
    }
    public MessageFromPerson(String message, String senderName) {
        this.message = message;
        this.senderName = senderName;
    }

    public String getMessage() {
        return message;
    }

    public String getSenderName() {
        return senderName;
    }
}
