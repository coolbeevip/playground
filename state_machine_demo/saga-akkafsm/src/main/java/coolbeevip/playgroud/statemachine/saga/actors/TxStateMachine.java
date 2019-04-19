package coolbeevip.playgroud.statemachine.saga.actors;

import akka.actor.AbstractFSM;
import coolbeevip.playgroud.statemachine.saga.event.TxAbortedEvent;
import coolbeevip.playgroud.statemachine.saga.event.TxEndedEvent;
import coolbeevip.playgroud.statemachine.saga.event.TxStartedEvent;
import coolbeevip.playgroud.statemachine.saga.model.SagaData;
import coolbeevip.playgroud.statemachine.saga.model.SagaState;
import coolbeevip.playgroud.statemachine.saga.model.StateMachineData;
import coolbeevip.playgroud.statemachine.saga.model.TxData;
import coolbeevip.playgroud.statemachine.saga.model.TxState;
import coolbeevip.playgroud.statemachine.saga.model.Uninitialized;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TxStateMachine extends AbstractFSM<TxState, StateMachineData> {

  {
    startWith(TxState.Idle, Uninitialized.Uninitialized);

    // Idle + TxStartedEvent
    when(TxState.Idle,
        matchEvent(TxStartedEvent.class, StateMachineData.class,
            (event, data) -> {
              log.info("  TS globalTxId={} parentTxId={} localTxId={}", event.getGlobalTxId(),
                  event.getParentTxId(), event.getLocalTxId());
              return goTo(TxState.Active).using(TxData.builder().globalTxId(event.getParentTxId())
                  .parentTxId(event.getParentTxId()).localTxId(event.getLocalTxId()).build());
            }
        )
    );

    // Active + TxEndedEvent
    when(TxState.Active,
        matchEvent(TxEndedEvent.class, TxData.class,
            (event, data) -> {
              log.info("  TE globalTxId={} parentTxId={} localTxId={}", event.getGlobalTxId(),
                  event.getParentTxId(), event.getLocalTxId());
              data.setEndTime(System.currentTimeMillis());
              return goTo(TxState.Committed).using(data);
            }
        )
    );

    // Active + TxAbortedEvent
    when(TxState.Active,
        matchEvent(TxAbortedEvent.class, TxData.class,
            (event, data) -> {
              log.info("  TA globalTxId={} parentTxId={} localTxId={}", event.getGlobalTxId(),
                  event.getParentTxId(), event.getLocalTxId());
              return goTo(TxState.Failed).using(data);
            }
        )
    );

    when(
        TxState.Committed,
        matchAnyEvent(
            (msg, data) -> {
              return goTo(TxState.Idle)
                  .using(data);
            }));

    whenUnhandled(
      matchAnyEvent((event, data) -> {
        log.warn("");
        return stay();
      })
    );

    onTransition(
        matchState(TxState.Active, TxState.Committed, (from, to) -> {
          //log.info("{} {} -> {}",getSelf().path().name(),from.name(),to.name());
        }).state(TxState.Active, TxState.Active, (from, to) -> {
          //log.info("{} {} -> {}",getSelf().path().name(),from.name(),to.name());
        })
    );

    initialize();
  }
}
