package coolbeevip.playgroud.statemachine.saga.actors;

import akka.actor.AbstractFSM;
import akka.actor.ActorRef;
import coolbeevip.playgroud.statemachine.saga.event.SagaEndedEvent;
import coolbeevip.playgroud.statemachine.saga.event.SagaStartedEvent;
import coolbeevip.playgroud.statemachine.saga.event.TxAbortedEvent;
import coolbeevip.playgroud.statemachine.saga.event.TxEndedEvent;
import coolbeevip.playgroud.statemachine.saga.event.TxStartedEvent;
import coolbeevip.playgroud.statemachine.saga.model.SagaData;
import coolbeevip.playgroud.statemachine.saga.model.StateMachineData;
import coolbeevip.playgroud.statemachine.saga.model.SagaState;
import coolbeevip.playgroud.statemachine.saga.model.Uninitialized;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SagaStateMachine extends AbstractFSM<SagaState, StateMachineData> {

  {
    startWith(SagaState.Idle, Uninitialized.Uninitialized);

    // Idle + SagaStartedEvent = Active
    when(SagaState.Idle,
        matchEvent(SagaStartedEvent.class, StateMachineData.class,
            (event, data) -> {
              log.info("SagaStartedEvent");
              return goTo(SagaState.Active)
                  .using(SagaData.builder().globalTxId(event.getGlobalTxId()).build());
            }
        )
    );

    // Active + TxStartedEvent = Active
    when(SagaState.Active,
        matchEvent(TxStartedEvent.class, SagaData.class,
            (event, data) -> {
              addTxStartedEvent(getSender(), event, data);
              return stay().using(data);
              //return goTo(SagaState.Active).using(data);
            }
        )
    );

    // Active + TxEndedEvent = PartiallyCommitted
    when(SagaState.Active,
        matchEvent(TxEndedEvent.class, SagaData.class,
            (event, data) -> {
              addTxEndedEvent(getSender(), event, data);
              return goTo(SagaState.PartiallyCommitted).using(data);
            }
        )
    );

    // Active + TxAbortedEvent = Active
    when(SagaState.Active,
        matchEvent(TxAbortedEvent.class, SagaData.class,
            (event, data) -> {
              addTxAbortedEvent(getSender(), event, data);
              return stay().using(data);
              //return goTo(SagaState.Active).using(data);
            }
        )
    );

    // PartiallyCommitted + SagaEndedEvent = Committed
    when(SagaState.PartiallyCommitted,
        matchEvent(SagaEndedEvent.class, SagaData.class,
            (event, data) -> {
              log.info("SagaEndedEvent");
              data.setEndTime(System.currentTimeMillis());
              return goTo(SagaState.Committed).using(data);
            }
        )
    );

    // PartiallyCommitted + TxStartedEvent = Active
    when(SagaState.PartiallyCommitted,
        matchEvent(TxStartedEvent.class, SagaData.class,
            (event, data) -> {
              log.info("TxStartedEvent");
              return goTo(SagaState.Active).using(data);
            }
        )
    );

    // Committed
    when(
        SagaState.Committed,
        matchAnyEvent(
            (msg, data) -> {
              return goTo(SagaState.Idle)
                  .using(data);
            }));

    onTransition(
        matchState(SagaState.PartiallyCommitted, SagaState.Committed, (from, to) -> {
          log.info("{} {} -> {}",getSelf().path().name(),from.name(),to.name());
        })
    );

    initialize();
  }

  private void addTxStartedEvent(ActorRef tx, TxStartedEvent event, SagaData sagaData) {
    sagaData.addTxStartedEvent(tx);
    tx.tell(event, getSelf());
    //sagaData.getTx().stream().filter(tx -> !tx.equals(saga)).forEach(tx -> tx.tell(TxStartedEvent.builder().build(), getSelf()));
  }

  private void addTxEndedEvent(ActorRef tx, TxEndedEvent event, SagaData sagaData) {
    sagaData.addTxEndedEvent(tx);
    tx.tell(event, getSelf());
    //sagaData.getTx().stream().filter(tx -> !tx.equals(tx)).forEach(tx -> tx.tell(TxEndedEvent.builder().build(), getSelf()));
  }

  private void addTxAbortedEvent(ActorRef tx, TxAbortedEvent event, SagaData sagaData) {
    sagaData.addTxAbortedEvent(tx);
    tx.tell(event, getSelf());
    //sagaData.getTx().stream().filter(tx -> !tx.equals(tx)).forEach(tx -> tx.tell(TxAbortedEvent.builder().build(), getSelf()));
  }
}
