package coolbeevip.playgroud.statemachine.nespresso.actors;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;

import coolbeevip.playgroud.statemachine.nespresso.Application;
import coolbeevip.playgroud.statemachine.nespresso.message.CitizEvent;
import coolbeevip.playgroud.statemachine.nespresso.model.CitizState;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.StateMachineMessageHeaders;
import org.springframework.test.context.junit4.SpringRunner;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class})
public class ApplicationTest {

  @Autowired
  StateMachine<CitizState, CitizEvent> stateMachine;


  @Before
  public void setup() {
    stateMachine.start();
  }

  @After
  public void down() {
    stateMachine.stop();
  }

  /**
   * 常规测试
   * 开机 -> 放入胶囊 -> 制作咖啡 -> 关机
   * */
  @Test
  @SneakyThrows
  public void normalTest() {
    Assert.assertEquals(stateMachine.getState().getId(), CitizState.OFF);

    //开机
    stateMachine.sendEvent(CitizEvent.PressPowerON);
    Assert.assertEquals(stateMachine.getState().getId(), CitizState.ON);

    //放入胶囊
    stateMachine.sendEvent(CitizEvent.PushCapsule);
    Assert.assertEquals(stateMachine.getState().getId(), CitizState.READY);

    //制作小杯
    //stateMachine.sendEvent(CitizEvent.PressTallCupButton);
    stateMachine.sendEvent(MessageBuilder.withPayload(CitizEvent.PressTallCupButton).setHeader(
        "working_time",5000).build());
    Assert.assertEquals(stateMachine.getState().getId(), CitizState.WORKING);

    //小杯5秒制作完成
    await().atMost(6, SECONDS).until(() -> stateMachine.getState().getId().equals(CitizState.ON));

    //关机
    stateMachine.sendEvent(CitizEvent.PressPowerOFF);
    Assert.assertEquals(stateMachine.getState().getId(), CitizState.OFF);
  }

  /**
   * 开机状态 10 秒钟后自动关机
   */
  @Test
  @SneakyThrows
  public void autoPowerOffWhenOnTest() {
    Assert.assertEquals(stateMachine.getState().getId(), CitizState.OFF);
    stateMachine.sendEvent(CitizEvent.PressPowerON);
    Assert.assertEquals(stateMachine.getState().getId(), CitizState.ON);
    await().atMost(11, SECONDS).until(() -> stateMachine.getState().getId().equals(CitizState.OFF));
  }

  /**
   * 就绪状态 10 秒钟后自动关机
   */
  @Test
  @SneakyThrows
  public void autoPowerOffWhenReadyTest() {
    stateMachine.sendEvent(CitizEvent.PressPowerON);
    stateMachine.sendEvent(CitizEvent.PushCapsule);
    await().atMost(11, SECONDS).until(() -> stateMachine.getState().getId().equals(CitizState.OFF));
  }

  /**
   * 制作咖啡
   * */
  @Test
  @SneakyThrows
  public void makingCoffee() {
    Assert.assertEquals(stateMachine.getState().getId(), CitizState.OFF);
    stateMachine.sendEvent(CitizEvent.PressPowerON);

    //小杯
    stateMachine.sendEvent(CitizEvent.PushCapsule);
    stateMachine.sendEvent(CitizEvent.PressTallCupButton);
    Assert.assertEquals(stateMachine.getState().getId(), CitizState.WORKING);
    await().atMost(6, SECONDS).until(() -> stateMachine.getState().getId().equals(CitizState.ON));

    //大杯
    stateMachine.sendEvent(CitizEvent.PushCapsule);
    stateMachine.sendEvent(CitizEvent.PressVentiCupButton);
    Assert.assertEquals(stateMachine.getState().getId(), CitizState.WORKING);
    await().atMost(6, SECONDS).until(() -> stateMachine.getState().getId().equals(CitizState.ON));

    //关机
    stateMachine.sendEvent(CitizEvent.PressPowerOFF);
    Assert.assertEquals(stateMachine.getState().getId(), CitizState.OFF);
  }

  /**
   * 制作时关机，重新开机后自动进入就绪状态(胶囊还未用完)
   * TODO 如何传递变量是否有胶囊
   * */
  @Test
  @SneakyThrows
  public void powerOnAfterPowerOffWhenWorking(){
    Assert.assertEquals(stateMachine.getState().getId(), CitizState.OFF);
    stateMachine.sendEvent(CitizEvent.PressPowerON);
    stateMachine.sendEvent(CitizEvent.PushCapsule);
    stateMachine.sendEvent(CitizEvent.PressTallCupButton);
    stateMachine.sendEvent(CitizEvent.PressPowerOFF);
    stateMachine.sendEvent(CitizEvent.PressPowerON);
    Assert.assertEquals(stateMachine.getState().getId(), CitizState.READY);
  }

  /**
   * 非就绪状态 + 按制作咖啡按钮
   * */
  @Test
  @SneakyThrows
  public void pressMakeButtonWhenNonReady () {
    stateMachine.sendEvent(CitizEvent.PressPowerON);
    stateMachine.sendEvent(CitizEvent.PressTallCupButton);
    Assert.assertEquals(stateMachine.getState().getId(), CitizState.ON);
  }
}