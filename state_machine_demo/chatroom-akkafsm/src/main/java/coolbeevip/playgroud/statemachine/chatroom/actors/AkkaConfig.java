package coolbeevip.playgroud.statemachine.chatroom.actors;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import lombok.Builder;

@Builder
public class AkkaConfig {
  public Config parseString() {
    return ConfigFactory.parseString("akka {chat_history_file = chat_history_log.txt}");
  }
}
