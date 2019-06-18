package coolbeevip.playgroud.statemachine.saga;

import akka.actor.ActorSystem;
import com.google.common.eventbus.EventBus;
import coolbeevip.playgroud.statemachine.saga.actors.event.SagaEndedEvent;
import coolbeevip.playgroud.statemachine.saga.actors.event.SagaStartedEvent;
import coolbeevip.playgroud.statemachine.saga.actors.event.TxEndedEvent;
import coolbeevip.playgroud.statemachine.saga.actors.event.TxStartedEvent;
import coolbeevip.playgroud.statemachine.saga.actors.event.base.BaseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {SagaApplication.class})
@Slf4j
public class SagaApplicationTest {

  @Autowired
  ActorSystem system;

  @Autowired
  @Qualifier("sagaEventBus")
  EventBus sagaEventBus;

  @Test
  @SneakyThrows
  public void sagaSuccessfulScenarioTest(){
    final String globalTxId = UUID.randomUUID().toString();
    getSuccessfulScenarioSagaEvents(globalTxId).stream().forEach( event -> {
      sagaEventBus.post(event);
    });

    Thread.sleep(36000);
  }

  private List<BaseEvent> getSuccessfulScenarioSagaEvents(String globalTxId){
    final String localTxId_1 = UUID.randomUUID().toString();
    final String localTxId_2 = UUID.randomUUID().toString();
    final String localTxId_3 = UUID.randomUUID().toString();
    List<BaseEvent> sagaEvents = new ArrayList<>();
    sagaEvents.add(SagaStartedEvent.builder().globalTxId(globalTxId).build());
    sagaEvents.add(TxStartedEvent.builder().globalTxId(globalTxId).parentTxId(globalTxId).localTxId(localTxId_1).build());
    sagaEvents.add(TxEndedEvent.builder().globalTxId(globalTxId).parentTxId(globalTxId).localTxId(localTxId_1).build());
    sagaEvents.add(TxStartedEvent.builder().globalTxId(globalTxId).parentTxId(globalTxId).localTxId(localTxId_2).build());
    sagaEvents.add(TxEndedEvent.builder().globalTxId(globalTxId).parentTxId(globalTxId).localTxId(localTxId_2).build());
    sagaEvents.add(TxStartedEvent.builder().globalTxId(globalTxId).parentTxId(globalTxId).localTxId(localTxId_3).build());
    sagaEvents.add(TxEndedEvent.builder().globalTxId(globalTxId).parentTxId(globalTxId).localTxId(localTxId_3).build());
    sagaEvents.add(SagaEndedEvent.builder().globalTxId(globalTxId).build());
    return sagaEvents;
  }

}
