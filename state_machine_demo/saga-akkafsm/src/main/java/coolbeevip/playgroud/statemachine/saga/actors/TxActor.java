package coolbeevip.playgroud.statemachine.saga.actors;

import akka.actor.AbstractFSM;
import coolbeevip.playgroud.statemachine.saga.event.TxAbortedEvent;
import coolbeevip.playgroud.statemachine.saga.event.TxComponsitedEvent;
import coolbeevip.playgroud.statemachine.saga.event.TxEndedEvent;
import coolbeevip.playgroud.statemachine.saga.event.TxStartedEvent;
import coolbeevip.playgroud.statemachine.saga.model.StateMachineData;
import coolbeevip.playgroud.statemachine.saga.model.TxData;
import coolbeevip.playgroud.statemachine.saga.model.Uninitialized;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class TxActor extends AbstractFSM<TxActorState, StateMachineData> {

  {
    startWith(TxActorState.ACTIVE, Uninitialized.Uninitialized);


    // ACTIVE + TxEndedEvent = COMMITTED
    when(TxActorState.ACTIVE,
        matchEvent(TxEndedEvent.class, TxData.class,
            (event, data) -> {
              log.info("  TE globalTxId={} parentTxId={} localTxId={}", event.getGlobalTxId(),
                  event.getParentTxId(), event.getLocalTxId());
              data.setEndTime(System.currentTimeMillis());
              return goTo(TxActorState.COMMITTED).using(data);
            }
        )
    );

    // ACTIVE + TxAbortedEvent = FAILED
    when(TxActorState.ACTIVE,
        matchEvent(TxAbortedEvent.class, TxData.class,
            (event, data) -> {
              log.info("  TA globalTxId={} parentTxId={} localTxId={}", event.getGlobalTxId(),
                  event.getParentTxId(), event.getLocalTxId());
              return goTo(TxActorState.FAILED).using(data);
            }
        )
    );

    // COMMITTED + TxComponsitedEvent = COMPENSATED
    when(TxActorState.COMMITTED,
        matchEvent(TxComponsitedEvent.class, TxData.class,
            (event, data) -> {
              log.info("  TA globalTxId={} parentTxId={} localTxId={}", event.getGlobalTxId(),
                  event.getParentTxId(), event.getLocalTxId());
              return goTo(TxActorState.COMPENSATED).using(data);
            }
        )
    );

    whenUnhandled(
      matchAnyEvent((event, data) -> {
        log.error("unMatch Event {}",event);
        return stay();
      })
    );

    onTransition(
        matchState(TxActorState.ACTIVE, TxActorState.COMMITTED, (from, to) -> {
          log.info("{} {} -> {}",getSelf().path().name(),from.name(),to.name());
        }).state(TxActorState.ACTIVE, TxActorState.FAILED, (from, to) -> {
          log.info("{} {} -> {}",getSelf().path().name(),from.name(),to.name());
        }).state(TxActorState.COMMITTED, TxActorState.COMPENSATED, (from, to) -> {
          log.info("{} {} -> {}",getSelf().path().name(),from.name(),to.name());
        })
    );

    initialize();
  }
}
