package coolbeevip.playgroud.statemachine.saga.actors;

public enum SagaActorState {
  IDEL,READY,PARTIALLY_ACTIVE,PARTIALLY_COMMITTED,FAILED,COMMITTED,COMPENSATED,SUSPENDED
}
