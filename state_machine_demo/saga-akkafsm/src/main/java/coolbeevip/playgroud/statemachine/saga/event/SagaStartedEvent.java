package coolbeevip.playgroud.statemachine.saga.event;


import coolbeevip.playgroud.statemachine.saga.event.base.SagaEvent;
import lombok.Builder;
import lombok.Getter;


@Getter
public class SagaStartedEvent extends SagaEvent {

  @Builder
  public SagaStartedEvent(String globalTxId) {
    super(globalTxId);
  }
}
