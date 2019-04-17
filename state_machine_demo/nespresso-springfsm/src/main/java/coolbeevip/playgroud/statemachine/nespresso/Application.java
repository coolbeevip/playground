package coolbeevip.playgroud.statemachine.nespresso;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.EnableWithStateMachine;

@SpringBootApplication
@EnableWithStateMachine
public class Application {
  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }
}
