package coolbeevip.playgroud.statemachine.saga.actors.event;


import coolbeevip.playgroud.statemachine.saga.actors.event.base.SagaEvent;
import lombok.Builder;
import lombok.Getter;


@Getter
public class SagaStartedEvent extends SagaEvent {

  @Builder
  public SagaStartedEvent(String globalTxId) {
    super(globalTxId);
  }
}
