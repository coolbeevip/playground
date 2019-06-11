package coolbeevip.playgroud.statemachine.saga.actors;

public enum TxState {
  ACTIVE,
  FAILED,
  COMMITTED,
  COMPENSATED;
}
