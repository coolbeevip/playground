package coolbeevip.playgroud.statemachine.chatroom.service;

import java.io.Serializable;
import java.util.List;


public class ChatStateJson implements Serializable {
    private String chatBody;
    private List<ActorStatusJson> actorStatuses;
    private String chatStatus;

    public ChatStateJson(String chatBody, List<ActorStatusJson> statuses, String chatStatus) {
        this.chatBody = chatBody;
        this.actorStatuses = statuses;
        this.chatStatus = chatStatus;
    }

    public String getChatBody() {
        return chatBody;
    }

    public List<ActorStatusJson> getActorStatuses() {
        return actorStatuses;
    }

    public String getChatStatus() {
        return chatStatus;
    }
}
