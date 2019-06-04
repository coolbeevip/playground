package coolbeevip.playgroud.statemachine.saga.actors;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.persistence.fsm.AbstractPersistentFSM;
import coolbeevip.playgroud.statemachine.saga.event.SagaAbortedEvent;
import coolbeevip.playgroud.statemachine.saga.event.SagaDomainEvent;
import coolbeevip.playgroud.statemachine.saga.event.SagaDomainEvent.DomainEvent;
import coolbeevip.playgroud.statemachine.saga.event.SagaEndedEvent;
import coolbeevip.playgroud.statemachine.saga.event.SagaStartedEvent;
import coolbeevip.playgroud.statemachine.saga.event.SagaTimeoutEvent;
import coolbeevip.playgroud.statemachine.saga.event.TxAbortedEvent;
import coolbeevip.playgroud.statemachine.saga.event.TxComponsitedEvent;
import coolbeevip.playgroud.statemachine.saga.event.TxEndedEvent;
import coolbeevip.playgroud.statemachine.saga.event.TxStartedEvent;
import coolbeevip.playgroud.statemachine.saga.event.base.TxEvent;
import coolbeevip.playgroud.statemachine.saga.model.SagaData;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import scala.concurrent.duration.Duration;

@Slf4j
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SagaActor extends
    AbstractPersistentFSM<SagaActorState, SagaData, SagaDomainEvent.DomainEvent> {

  public static Props props(String persistenceId) {
    return Props.create(SagaActor.class, persistenceId);
  }


  @Autowired
  private ActorSystem actorSystem;

  @Autowired
  SagaActorHolder sagaActorHolder;

  private final String persistenceId;

  public SagaActor(String persistenceId) {
    this.persistenceId = persistenceId;

    startWith(SagaActorState.IDEL, SagaData.builder().build());

    when(SagaActorState.IDEL,
        matchEvent(SagaStartedEvent.class,
            (event, data) -> {
              data.setGlobalTxId(event.getGlobalTxId());
              data.setBeginTime(System.currentTimeMillis());
              return goTo(SagaActorState.READY).replying(data);
            }
        )
    );

    when(SagaActorState.READY,
        matchEvent(TxStartedEvent.class, SagaData.class,
            (event, data) -> {
              return goTo(SagaActorState.PARTIALLY_ACTIVE).andThen(exec(_data -> {
                tellTxActor(event, getSender(), _data);
              }));
            }
        ).event(SagaEndedEvent.class,
            (event, data) -> {
              return goTo(SagaActorState.SUSPENDED);
            }
        ).event(SagaAbortedEvent.class,
            (event, data) -> {
              return goTo(SagaActorState.SUSPENDED);
            }
        )
    );

    when(SagaActorState.PARTIALLY_ACTIVE,
        matchEvent(TxEndedEvent.class, SagaData.class,
            (event, data) -> {
              return goTo(SagaActorState.PARTIALLY_COMMITTED).andThen(exec(_data -> {
                tellTxActor(event, getSender(), _data);
              }));
            }
        ).event(SagaTimeoutEvent.class,
            (event, data) -> {
              return goTo(SagaActorState.SUSPENDED);
            }
        ).event(TxAbortedEvent.class,
            (event, data) -> {
              return goTo(SagaActorState.FAILED).andThen(exec(_data -> {
                tellTxActor(event, getSender(), _data);
              }));
            }
        )
    );

    when(SagaActorState.PARTIALLY_COMMITTED,
        matchEvent(TxStartedEvent.class,
            (event, data) -> {
              return goTo(SagaActorState.PARTIALLY_ACTIVE).andThen(exec(_data -> {
                tellTxActor(event, getSender(), _data);
              }));
            }
        ).event(SagaTimeoutEvent.class,
            (event, data) -> {
              return goTo(SagaActorState.SUSPENDED);
            }
        ).event(SagaEndedEvent.class,
            (event, data) -> {
              data.setEndTime(System.currentTimeMillis());
              return goTo(SagaActorState.COMMITTED)
                  .forMax(Duration.create(1, TimeUnit.MICROSECONDS)).replying(data);
            }
        ).event(TxAbortedEvent.class,
            (event, data) -> {
              return goTo(SagaActorState.FAILED).andThen(exec(_data -> {
                tellTxActor(event, getSender(), _data);
              }));
            }
        )
    );

    when(SagaActorState.FAILED,
        matchEvent(SagaTimeoutEvent.class, SagaData.class,
            (event, data) -> {
              return goTo(SagaActorState.SUSPENDED);
            }
        ).event(TxComponsitedEvent.class, SagaData.class,
            (event, data) -> {
              //log.info("{} -> {}",SagaActorState.FAILED,SagaActorState.FAILED);
              //return stay().using(data);
              //当不存在committed的tx时，状态变为COMPENSATED
              return goTo(SagaActorState.COMPENSATED).andThen(exec(_data -> {
                tellTxActor(event, getSender(), _data);
              }));
            }
        )
    );

    when(SagaActorState.COMMITTED,
        matchAnyEvent(
            (event, data) -> stop()
        )
    );

    when(SagaActorState.SUSPENDED,
        matchAnyEvent(
            (event, data) -> stop()
        )
    );

    when(SagaActorState.COMPENSATED,
        matchAnyEvent(
            (event, data) -> stop()
        )
    );

    whenUnhandled(
        matchAnyEvent((event, data) -> {
          log.error("unmatch event {}", event);
          return stay();
        })
    );

    onTransition(
        matchState(null, null, (from, to) -> {
          log.info("transition {} {} -> {}", getSelf(), from, to);
          SagaData data = stateData();
          data.updateCurrentState(data.getGlobalTxId(),to);
        })
    );
  }

  @Override
  public Class domainEventClass() {
    return SagaDomainEvent.DomainEvent.class;
  }


  @Override
  public String persistenceId() {
    return persistenceId;
  }

  @Override
  public SagaData applyEvent(DomainEvent domainEvent, SagaData currentData) {
    return currentData;
  }

  private void tellTxActor(TxEvent event, ActorRef txActor, SagaData data) {
    if(!data.getTxActors().contains(txActor)){
      data.addTxActor(txActor);
    }
    txActor.tell(event, getSelf());
  }

//  private void addTxStartedEvent(ActorRef tx, TxStartedEvent event, SagaData sagaData) {
//    sagaData.addTxStartedEvent(tx);
//    tx.tell(event, getSelf());
//    //sagaData.getTx().stream().filter(tx -> !tx.equals(saga)).forEach(tx -> tx.tell(TxStartedEvent.builder().build(), getSelf()));
//  }
//
//  private void addTxEndedEvent(ActorRef tx, TxEndedEvent event, SagaData sagaData) {
//    sagaData.addTxEndedEvent(tx);
//    tx.tell(event, getSelf());
//    //sagaData.getTx().stream().filter(tx -> !tx.equals(tx)).forEach(tx -> tx.tell(TxEndedEvent.builder().build(), getSelf()));
//  }
//
//  private void addTxAbortedEvent(ActorRef tx, TxAbortedEvent event, SagaData sagaData) {
//    sagaData.addTxAbortedEvent(tx);
//    tx.tell(event, getSelf());
//    //sagaData.getTx().stream().filter(tx -> !tx.equals(tx)).forEach(tx -> tx.tell(TxAbortedEvent.builder().build(), getSelf()));
//  }
}
