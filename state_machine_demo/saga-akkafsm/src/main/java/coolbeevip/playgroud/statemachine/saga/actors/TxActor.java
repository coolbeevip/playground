package coolbeevip.playgroud.statemachine.saga.actors;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.persistence.fsm.AbstractPersistentFSM;
import coolbeevip.playgroud.statemachine.saga.event.SagaDomainEvent;
import coolbeevip.playgroud.statemachine.saga.event.SagaDomainEvent.DomainEvent;
import coolbeevip.playgroud.statemachine.saga.event.TxAbortedEvent;
import coolbeevip.playgroud.statemachine.saga.event.TxComponsitedEvent;
import coolbeevip.playgroud.statemachine.saga.event.TxEndedEvent;
import coolbeevip.playgroud.statemachine.saga.event.TxStartedEvent;
import coolbeevip.playgroud.statemachine.saga.event.UpdateSagaDataEvent;
import coolbeevip.playgroud.statemachine.saga.model.TxData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class TxActor extends AbstractPersistentFSM<TxActorState, TxData, DomainEvent> {

  public static Props props(String persistenceId) {
    return Props.create(TxActor.class, persistenceId);
  }

  private final String persistenceId;

  public TxActor(String persistenceId) {
    this.persistenceId = persistenceId;
    startWith(TxActorState.IDEL, TxData.builder().build());

    when(TxActorState.IDEL,
        matchEvent(TxStartedEvent.class,
            (event, data) -> {
              log.info("++++");
              data.setGlobalTxId(event.getGlobalTxId());
              data.setParentTxId(event.getParentTxId());
              data.setBeginTime(System.currentTimeMillis());
              getSender().tell(UpdateSagaDataEvent.builder().globalTxId(event.getGlobalTxId()).data(data).build(),getSelf());
              return goTo(TxActorState.ACTIVE).replying(data);
            }
        )
    );

    when(TxActorState.ACTIVE,
        matchEvent(TxEndedEvent.class,
            (event, data) -> {
              data.setEndTime(System.currentTimeMillis());
              //getSender().tell(UpdateSagaDataEvent.builder().globalTxId(event.getGlobalTxId()).data(data).build(),getSelf());
              return goTo(TxActorState.COMMITTED).replying(data);
            }
        ).event(TxAbortedEvent.class, TxData.class,
            (event, data) -> {
              data.setEndTime(System.currentTimeMillis());
              //getSender().tell(UpdateSagaDataEvent.builder().globalTxId(event.getGlobalTxId()).data(data).build(),getSelf());
              return goTo(TxActorState.FAILED).replying(data);
            }
        )
    );

    when(TxActorState.COMMITTED,
        matchEvent(TxComponsitedEvent.class,
            (event, data) -> {
              data.setEndTime(System.currentTimeMillis());
              //getSender().tell(UpdateSagaDataEvent.builder().globalTxId(event.getGlobalTxId()).data(data).build(),getSelf());
              return goTo(TxActorState.COMPENSATED).replying(data);
            }
        )
    );

    whenUnhandled(
        matchAnyEvent((event, data) -> {
          log.error("unMatch Event {}", event);
          return stay();
        })
    );

    onTransition(
        matchState(null, null, (from, to) -> {
          log.info("transition {} {} -> {}", getSelf(), from.name(), to.name());
        })
    );
  }

  @Override
  public Class<DomainEvent> domainEventClass() {
    return SagaDomainEvent.DomainEvent.class;
  }

  @Override
  public TxData applyEvent(DomainEvent domainEvent, TxData currentData) {
    return currentData;
  }

  @Override
  public String persistenceId() {
    return persistenceId;
  }
}
