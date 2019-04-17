package coolbeevip.playgroud.statemachine.nespresso.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Setter
@Getter
public class MachineData implements CitizData {
  private boolean capsule = false;
}
