package coolbeevip.playgroud.statemachine.saga.actors;

import akka.persistence.fsm.PersistentFSM;

public enum TxActorState implements PersistentFSM.FSMState {
  IDEL,
  ACTIVE,
  FAILED,
  COMMITTED,
  COMPENSATED;

  @Override
  public String identifier() {
    return name();
  }
}
