package coolbeevip.playgroud.statemachine.chatroom.service;

import org.apache.commons.collections4.queue.CircularFifoQueue;

import java.util.*;


public class ActorsConditions {
    public static Map<String, String> status = new HashMap<String, String>();
    public static Queue<String> chatBody = new CircularFifoQueue<String>(8);
    public static String chatStatus = "";

    public static ChatStateJson getJson(){
        String chatBodyStr = "";
        for(String line: chatBody){
            if(chatBodyStr.length()>0){
                chatBodyStr += "\n";
            }
            chatBodyStr+=line;
        }

        List<ActorStatusJson> statusJsonList = new ArrayList<>();
        for(Map.Entry<String, String> entry : status.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            ActorStatusJson actorStatusJson = new ActorStatusJson();
            actorStatusJson.setName(key);
            actorStatusJson.setStatus(value);
            statusJsonList.add(actorStatusJson);
        }

        return new ChatStateJson(chatBodyStr, statusJsonList, chatStatus);
    }
}
