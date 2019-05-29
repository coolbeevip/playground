package coolbeevip.playgroud.statemachine.saga.actors;

import akka.actor.AbstractFSM;
import coolbeevip.playgroud.statemachine.saga.event.SagaAbortedEvent;
import coolbeevip.playgroud.statemachine.saga.event.SagaEndedEvent;
import coolbeevip.playgroud.statemachine.saga.event.SagaStartedEvent;
import coolbeevip.playgroud.statemachine.saga.event.SagaTimeoutEvent;
import coolbeevip.playgroud.statemachine.saga.event.TxAbortedEvent;
import coolbeevip.playgroud.statemachine.saga.event.TxComponsitedEvent;
import coolbeevip.playgroud.statemachine.saga.event.TxEndedEvent;
import coolbeevip.playgroud.statemachine.saga.event.TxStartedEvent;
import coolbeevip.playgroud.statemachine.saga.model.SagaData;
import coolbeevip.playgroud.statemachine.saga.model.StateMachineData;
import coolbeevip.playgroud.statemachine.saga.model.Uninitialized;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SagaActor extends AbstractFSM<SagaActorState, StateMachineData> {

  @Autowired
  SagaActorHolder sagaActorHolder;

  {
    startWith(SagaActorState.IDEL, Uninitialized.Uninitialized);

    // IDEL + SagaStartedEvent = READY
    when(SagaActorState.IDEL,
        matchEvent(SagaStartedEvent.class, Uninitialized.class,
            (event, uninitialized) -> {
              log.info("{} -> {}",SagaActorState.IDEL,SagaActorState.READY);
              return goTo(SagaActorState.READY)
                  .using(SagaData.builder().globalTxId(event.getGlobalTxId()).build());
            }
        )
    );

    // READY + TxStartedEvent = PARTIALLY_ACTIVE
    when(SagaActorState.READY,
        matchEvent(TxStartedEvent.class, StateMachineData.class,
            (event, data) -> {
              log.info("{} -> {}",SagaActorState.READY,SagaActorState.PARTIALLY_ACTIVE);
              return goTo(SagaActorState.PARTIALLY_ACTIVE)
                  .using(SagaData.builder().globalTxId(event.getGlobalTxId()).build());
            }
        )
    );

    // READY + SagaEndedEvent = SUSPENDED
    when(SagaActorState.READY,
        matchEvent(SagaEndedEvent.class, StateMachineData.class,
            (event, data) -> {
              log.info("{} -> {}",SagaActorState.READY,SagaActorState.SUSPENDED);
              return goTo(SagaActorState.SUSPENDED).using(data);
            }
        )
    );

    // READY + SagaAbortedEvent = SUSPENDED
    when(SagaActorState.READY,
        matchEvent(SagaAbortedEvent.class, StateMachineData.class,
            (event, data) -> {
              log.info("{} -> {}",SagaActorState.READY,SagaActorState.SUSPENDED);
              return goTo(SagaActorState.SUSPENDED).using(data);
            }
        )
    );

    // PARTIALLY_ACTIVE + TxEndedEvent = PARTIALLY_COMMITTED
    when(SagaActorState.PARTIALLY_ACTIVE,
        matchEvent(TxEndedEvent.class, SagaData.class,
            (event, data) -> {
              log.info("{} -> {}",SagaActorState.PARTIALLY_ACTIVE,SagaActorState.PARTIALLY_COMMITTED);
              return goTo(SagaActorState.PARTIALLY_COMMITTED).using(data);
            }
        )
    );

    // PARTIALLY_ACTIVE + SagaTimeoutEvent = SUSPENDED
    when(SagaActorState.PARTIALLY_ACTIVE,
        matchEvent(SagaTimeoutEvent.class, SagaData.class,
            (event, data) -> {
              log.info("{} -> {}",SagaActorState.PARTIALLY_ACTIVE,SagaActorState.PARTIALLY_COMMITTED);
              return goTo(SagaActorState.SUSPENDED).using(data);
            }
        )
    );

    // PARTIALLY_ACTIVE + TxAbortedEvent = FAILED
    when(SagaActorState.PARTIALLY_ACTIVE,
        matchEvent(TxAbortedEvent.class, SagaData.class,
            (event, data) -> {
              log.info("{} -> {}",SagaActorState.PARTIALLY_ACTIVE,SagaActorState.FAILED);
              return goTo(SagaActorState.FAILED).using(data);
            }
        )
    );

    // PARTIALLY_COMMITTED + TxStartedEvent = PARTIALLY_ACTIVE
    when(SagaActorState.PARTIALLY_COMMITTED,
        matchEvent(TxStartedEvent.class, SagaData.class,
            (event, data) -> {
              log.info("{} -> {}",SagaActorState.PARTIALLY_COMMITTED,SagaActorState.PARTIALLY_ACTIVE);
              return goTo(SagaActorState.PARTIALLY_ACTIVE).using(data);
            }
        )
    );

    // PARTIALLY_COMMITTED + SagaTimeoutEvent = SUSPENDED
    when(SagaActorState.PARTIALLY_COMMITTED,
        matchEvent(SagaTimeoutEvent.class, SagaData.class,
            (event, data) -> {
              log.info("{} -> {}",SagaActorState.PARTIALLY_COMMITTED,SagaActorState.SUSPENDED);
              return goTo(SagaActorState.SUSPENDED).using(data);
            }
        )
    );

    // PARTIALLY_COMMITTED + SagaEndedEvent = COMMITTED
    when(SagaActorState.PARTIALLY_COMMITTED,
        matchEvent(SagaEndedEvent.class, SagaData.class,
            (event, data) -> {
              log.info("{} -> {}",SagaActorState.PARTIALLY_COMMITTED,SagaActorState.COMMITTED);
              return goTo(SagaActorState.COMMITTED).using(data);
            }
        )
    );

    // PARTIALLY_COMMITTED + SagaEndedEvent = FAILED
    when(SagaActorState.PARTIALLY_COMMITTED,
        matchEvent(TxAbortedEvent.class, SagaData.class,
            (event, data) -> {
              log.info("{} -> {}",SagaActorState.PARTIALLY_COMMITTED,SagaActorState.FAILED);
              return goTo(SagaActorState.FAILED).using(data);
            }
        )
    );

    // FAILED + SagaTimeoutEvent = SUSPENDED
    when(SagaActorState.FAILED,
        matchEvent(SagaTimeoutEvent.class, SagaData.class,
            (event, data) -> {
              log.info("{} -> {}",SagaActorState.FAILED,SagaActorState.SUSPENDED);
              return goTo(SagaActorState.SUSPENDED).using(data);
            }
        )
    );

    // FAILED + TxComponsitedEvent = FAILED or COMPENSATED
    when(SagaActorState.FAILED,
        matchEvent(TxComponsitedEvent.class, SagaData.class,
            (event, data) -> {
              log.info("{} -> {}",SagaActorState.FAILED,SagaActorState.FAILED);
              return stay().using(data);
              //当不存在committed的tx时，状态变为COMPENSATED
              //return goTo(SagaActorState.COMPENSATED).using(data);
            }
        )
    );

    when(SagaActorState.COMMITTED,
        matchAnyEvent(
            (event, data) -> {
              log.info("{} -> END",SagaActorState.COMMITTED);
              return stay().using(data);
            }
        )
    );

    when(SagaActorState.SUSPENDED,
        matchAnyEvent(
            (event, data) -> {
              log.info("{} -> END",SagaActorState.SUSPENDED);
              return stay().using(data);
            }
        )
    );

    when(SagaActorState.COMPENSATED,
        matchAnyEvent(
            (event, data) -> {
              log.info("{} -> END",SagaActorState.COMPENSATED);
              return stay().using(data);
            }
        )
    );
//    // PartiallyCommitted + SagaEndedEvent = Committed
//    when(SagaActorState.PartiallyCommitted,
//        matchEvent(SagaEndedEvent.class, SagaData.class,
//            (event, data) -> {
//              log.info("SE globalTxId={}",event.getGlobalTxId());
//              data.setEndTime(System.currentTimeMillis());
//              return goTo(SagaActorState.Committed).using(data);
//            }
//        )
//    );
//
//    // PartiallyCommitted + TxStartedEvent = Active
//    when(SagaActorState.PartiallyCommitted,
//        matchEvent(TxStartedEvent.class, SagaData.class,
//            (event, data) -> {
//              log.info("TxStartedEvent");
//              return goTo(SagaActorState.Active).using(data);
//            }
//        )
//    );
//
//    // Committed
//    when(
//        SagaActorState.Committed,
//        matchAnyEvent(
//            (msg, data) -> {
//              return goTo(SagaActorState.IDEL)
//                  .using(data);
//            }));

    whenUnhandled(
        matchAnyEvent((event, data) -> {
          log.error("unMatch Event {}",event);
          return stay();
        })
    );

    onTransition(
        matchState(null,null, (from, to) -> {
          StateMachineData data = stateData();
          if(data instanceof SagaData){
            String globalTxId = ((SagaData)data).getGlobalTxId();
            sagaActorHolder.updateSagaCurrentState(globalTxId,to);
          }
        })
    );

    initialize();
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
