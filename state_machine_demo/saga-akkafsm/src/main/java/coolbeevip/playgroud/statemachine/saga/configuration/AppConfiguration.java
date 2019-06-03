package coolbeevip.playgroud.statemachine.saga.configuration;

import akka.actor.ActorSystem;
import com.google.common.eventbus.EventBus;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import coolbeevip.playgroud.statemachine.saga.integration.akka.SpringAkkaExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfiguration {

  @Value("${saga.eventBus.poolSize:100}")
  private int sageEventBusPoolSize;

  @Autowired
  private ApplicationContext applicationContext;

  @Autowired
  private SpringAkkaExtension springAkkaExtension;

  @Bean
  public ActorSystem actorSystem() {
    ActorSystem system = ActorSystem.create("akka-saga", akkaConfiguration());
    springAkkaExtension.initialize(applicationContext);
    return system;
  }

  @Bean
  public Config akkaConfiguration() {
    return ConfigFactory.load();
  }

  @Bean(name = "sagaEventBus")
  public EventBus sagaEventBus() {
    return new EventBus();
  }

}
