package coolbeevip.playgroud.statemachine.saga.actors;

import akka.persistence.fsm.PersistentFSM;

public enum SagaActorState implements PersistentFSM.FSMState {
  IDEL,
  READY,
  PARTIALLY_ACTIVE,
  PARTIALLY_COMMITTED,
  FAILED,
  COMMITTED,
  COMPENSATED,
  SUSPENDED;

  @Override
  public String identifier() {
    return name();
  }
}
