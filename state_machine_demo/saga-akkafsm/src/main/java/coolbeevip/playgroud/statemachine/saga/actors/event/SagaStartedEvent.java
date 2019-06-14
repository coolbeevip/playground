package coolbeevip.playgroud.statemachine.saga.actors.event;


import coolbeevip.playgroud.statemachine.saga.actors.event.base.SagaEvent;
import lombok.Builder;
import lombok.Getter;


@Getter
public class SagaStartedEvent extends SagaEvent {
  private int timeout; //second
  @Builder
  public SagaStartedEvent(String globalTxId,int timeout) {
    super(globalTxId);
    this.timeout = timeout;
  }
}
