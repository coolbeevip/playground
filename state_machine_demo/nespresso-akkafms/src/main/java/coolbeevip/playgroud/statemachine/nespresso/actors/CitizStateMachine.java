package coolbeevip.playgroud.statemachine.nespresso.actors;

import akka.actor.AbstractFSM;
import coolbeevip.playgroud.statemachine.nespresso.message.PressPowerOFF;
import coolbeevip.playgroud.statemachine.nespresso.message.PressPowerON;
import coolbeevip.playgroud.statemachine.nespresso.message.PressTallCupButton;
import coolbeevip.playgroud.statemachine.nespresso.message.PressVentiCupButton;
import coolbeevip.playgroud.statemachine.nespresso.message.PushCapsule;
import coolbeevip.playgroud.statemachine.nespresso.model.CitizState;
import coolbeevip.playgroud.statemachine.nespresso.model.CitizData;
import coolbeevip.playgroud.statemachine.nespresso.model.MachineData;
import coolbeevip.playgroud.statemachine.nespresso.model.Uninitialized;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.Arrays;

/**
 * Citiz 胶囊咖啡机
 */

@Slf4j
public class CitizStateMachine extends AbstractFSM<CitizState, CitizData> {

  {
    // 初始化状态
    startWith(CitizState.OFF, Uninitialized.Uninitialized);

    // 关机状态 + 按下开机按钮
    when(CitizState.OFF,
            matchEvent(PressPowerON.class, Uninitialized.class,
                    (event, uninitialized) -> {
                      log.info("press power on button");
                      return goTo(CitizState.ON).using(MachineData.builder().build());
                    }
            ).event(PressPowerON.class, MachineData.class,
                    (event, data) -> {
                      log.info("press power on button");
                      if (data.isCapsule()) {
                        return goTo(CitizState.READY).using(data);
                      } else {
                        return goTo(CitizState.ON).using(data);
                      }
                    })
    );


    // 开机状态 + 10 秒无响应自动进入关机状态
    when(CitizState.ON,
            Duration.ofSeconds(10L),
            matchEvent(Arrays.asList(PressPowerOFF.class, StateTimeout()), MachineData.class,
                    (event, data) -> {
                      log.info("automatically power off after 5 second");
                      return goTo(CitizState.OFF).using(data);
                    }
            )
    );

    // 开机状态 + 压入咖啡胶囊
    when(CitizState.ON,
            matchEvent(PushCapsule.class, MachineData.class,
                    (event, data) -> {
                      log.info("push capsule");
                      data.setCapsule(true);
                      return goTo(CitizState.READY).using(data);
                    }
            )
    );

    // 就绪状态 + 按下制作小杯按钮（工作5秒钟）
    when(CitizState.READY,
            matchEvent(PressTallCupButton.class, MachineData.class, (obj, data) -> {
                      log.info("press tall cup button");
                      return goTo(CitizState.WORKING).using(data).forMax(Duration.ofSeconds(5));
                    }
            ).event(PressVentiCupButton.class, MachineData.class, (obj, data) -> {
              log.info("press venti cup button");
              return goTo(CitizState.WORKING).using(data).forMax(Duration.ofSeconds(10));
            })
    );

    // 就绪状态 + 10 秒钟后自动关机
    when(CitizState.READY,
            Duration.ofSeconds(10L),
            matchEvent(Arrays.asList(PressPowerOFF.class, StateTimeout()), MachineData.class,
                    (obj, data) -> {
                      log.info("press power off button");
                      return goTo(CitizState.OFF).using(data);
                    }
            )
    );


    // 工作状态 + 5 秒钟完成一杯
    when(CitizState.WORKING,
            Duration.ofSeconds(10L),
            matchEvent(Arrays.asList(null, StateTimeout()), MachineData.class,
                    (event, data) -> {
                      log.info("coffee productions is completed");
                      data.setCapsule(false);
                      return goTo(CitizState.ON).using(data);
                    }
            )
    );

    // 工作状态 + 按下关机按钮
    when(CitizState.WORKING,
            matchEvent(PressPowerOFF.class, MachineData.class,
                    (event, data) -> {
                      log.info("press power off button");
                      return goTo(CitizState.OFF).using(data);
                    }
            )
    );

    whenUnhandled(
            matchEvent(PressTallCupButton.class, MachineData.class,
                    (evnet, data) -> {
                      log.warn("The machine is not ready");
                      return stay();
                    }));

    onTransition(
            matchState(CitizState.OFF, CitizState.ON, (from, to) -> {
              log.info("OFF -> ON");
              CitizConditions.status.put(getSelf().path().name(), CitizState.ON.name());

              // ON
            }).state(CitizState.ON, CitizState.OFF, (from, to) -> {
              log.info("ON -> OFF");
              CitizConditions.status.put(getSelf().path().name(), CitizState.OFF.name());
            }).state(CitizState.ON, CitizState.READY, (from, to) -> {
              log.info("ON -> READY");
              CitizConditions.status.put(getSelf().path().name(), CitizState.READY.name());

              // READY
            }).state(CitizState.READY, CitizState.WORKING, (from, to) -> {
              log.info("READY -> WORKING");
              CitizConditions.status.put(getSelf().path().name(), CitizState.WORKING.name());
            }).state(CitizState.READY, CitizState.OFF, (from, to) -> {
              log.info("READY -> OFF");
              CitizConditions.status.put(getSelf().path().name(), CitizState.OFF.name());

              // WORKING
            }).state(CitizState.WORKING, CitizState.OFF, (from, to) -> {
              log.info("WORKING -> OFF");
              CitizConditions.status.put(getSelf().path().name(), CitizState.OFF.name());
            }).state(CitizState.WORKING, CitizState.ON, (from, to) -> {
              log.info("WORKING -> ON");
              CitizConditions.status.put(getSelf().path().name(), CitizState.ON.name());
            }).state(CitizState.WORKING, CitizState.READY, (from, to) -> {
              log.info("WORKING -> READY");
              CitizConditions.status.put(getSelf().path().name(), CitizState.READY.name());
            })
    );

    initialize();
  }
}
