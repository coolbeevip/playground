# Saga 状态机（Saga State Machine）

### 事件定义(Event Definition)

- E<sub>ss</sub> SagaStartedEvent

  全局事务启动事件

  it is the event to start a global transaction

- E<sub>se</sub> SagaEndedEvent

  全局事务结束事件

  it is the event to close a global transaction

- E<sub>sa</sub> SagaAbortedEvent

  全局事务失败事件，会触发全局事务中的子事务进行补偿，补偿全部成功后状态变为 `COMPENSATED`  补偿失败状态变为 `SUSPENDED`

  it is the event to abort a global transaction and cause the sub-transactions to do the compensate, the status will change to `COMPENSATED` after successfully compensating all sub-transactions, otherwise it will be changed to `SUSPENDED`

- E<sub>so</sub> SagaTimeoutEvent

  全局事务超时事件，当全局事务请求出现网络超时、请求超时或者其他通信异常（我们需要定义一个触发此事件的正向异常集合或者反向异常集合）将发送此事件，这时无法确定Alpha是否成功处理了这次请求，所以此事件将导致全局事务挂起，这个事件与 `SagaAbortedEvent` 事件的区别是收到到此事件后不会进行补偿操作

  it is the event to timeout a global transaction, the event is sent when network connection timeout, or  request timeout or other communication exception (we need to define a forward exception set or a reverse exception set that triggers this event). At this time, it is impossible to determine whether Alpha successfully processed the request so this event will cause the global transaction to `SUSPENDED`. The only difference between this event and the `SagaAbortedEvent` event is that no compensation is done.

- E<sub>ts</sub> TxStartedEvent

  子事务启动事件

  it is the event to start a sub-transaction

- E<sub>te</sub> TxEndedEvent

  子事务结束事件

  it is the event to close a sub-transaction

- E<sub>ta</sub> TxAbortedEvent

  子事务失败事件

  it is the event to abort a sub-transaction

- E<sub>tm</sub> TxComponsitedEvent

  子事务补偿成功事件

  it is the event to componsited a sub-transaction

### 状态机设计（State Machine Design）

状态机由Saga 状态机（全局事务）、Tx 状态机（子事务）组成

The state machine consists of a Saga state machine (global transaction) and a Tx state machine (sub-transaction)

#### Saga 状态机（Saga State Machine）

![saga_state_diagram](assets/saga_state_diagram.png)

##### Saga 状态定义（Saga State Definition）

* IDEL 

  空闲状态：状态实例创建后的初始化状态

  Initialization state after the state instance is created

* PARTIALLY_ACTIVE 

  部分子事务激活状态：收到了子事务开始事件  `TxStartedEvent` 后的状态

  Status after receiving `TxStartedEvent`

* PARTIALLY_COMMITTED 

  部分子事务已提交状态：表示已经收到了事务结束事件 `TxEndedEvent`

  Status after receiving  `TxEndedEvent`

* FAILED 

  失败状态：全局事务失败状态，当收到 `TxAbortedEvent` 或者 `SagaAbortedEvent` 事件满足进入此状态后将对所有已提交的子事务进行补偿，补偿全部成功后进入 `COMPENSATED` 状态，补偿失败后进入 `SUSPENDED` 状态

  Global transaction failed, Compensation for all committed state sub-transactions after receiving `TxAbortedEvent` 或者 `SagaAbortedEvent` , After the compensation is successful, the status change to `COMPENSATED`, otherwise it change to `SUSPENDED`

* COMMITTED 

  已提交状态：全局事务正常结束状态，所有子事物都已经提交

  it is final state, global transaction and sub-transaction committed

* COMPENSATED 

  已补偿状态：全局事务正常结束状态，所有已经提交的子事务补偿成功

  it is final state, all sub-transactions compensate successfully

* SUSPENDED

  事件挂起状态：全局事务异常结束状态，当出现不符合预期的事件状态组合（IDEL + SagaEndedEvent）或者Omega异常（java.net.ConnectException）时将进入挂起状态，设计挂起状态的目的是为了避免问题扩散，此时需要人工介入处理

  it is final state, global transaction ended abnormally, when there is an unexpected combination of event states or Omega exception, it will change to suspended state. The suspended state is designed to avoid problem proliferation, which requires manual intervention, manual intervention is required at this time.

| <font size=1>State (Current/Next)</font> | <font size=1>IDEL</font>           | <font size=1>PARTIALLY_ACTIVE</font> | <font size=1>PARTIALLY_COMMITTED</font> | <font size=1>FAILED</font>         | <font size=1>COMPENSATED</font>    | <font size=1>COMMITTED</font>      | <font size=1>SUSPENDED</font>                       |
| ---------------------------------------- | ---------------------------------- | ------------------------------------ | --------------------------------------- | ---------------------------------- | ---------------------------------- | ---------------------------------- | --------------------------------------------------- |
| <font size=1>IDEL</font>                 | <font size=1>E<sub>ss</sub></font> | <font size=1>E<sub>ts</sub></font>   |                                         |                                    |                                    |                                    | <font size=1>E<sub>se</sub> / E<sub>sa</sub></font> |
| <font size=1>PARTIALLY_ACTIVE</font>     |                                    |                                      | <font size=1>E<sub>te</sub></font>      | <font size=1>E<sub>ta</sub></font> |                                    |                                    | <font size=1>E<sub>so</sub></font>                  |
| <font size=1>PARTIALLY_COMMITTED</font>  |                                    | <font size=1>E<sub>ts</sub></font>   |                                         | <font size=1>E<sub>sa</sub></font> |                                    | <font size=1>E<sub>se</sub></font> |                                                     |
| <font size=1>FAILED</font>               |                                    |                                      |                                         | <font size=1>E<sub>tm</sub></font> | <font size=1>E<sub>tm</sub></font> |                                    |                                                     |
| <font size=1>COMPENSATED</font>          |                                    |                                      |                                         |                                    |                                    |                                    |                                                     |
| <font size=1>COMMITTED</font>            |                                    |                                      |                                         |                                    |                                    |                                    |                                                     |
| <font size=1>SUSPENDED</font>            |                                    |                                      |                                         |                                    |                                    |                                    |                                                     |

**注意：** FALIED + E<sub>tm</sub> = COMPENSATED 只有在所有状态是 Committed 的 Tx 都补偿完毕后会触发

**NOTE：** FALIED + E<sub>tm</sub> = COMPENSATED Triggered only after all Tx whose state is Committed is compensated

#### Tx 状态机（Tx State Machine）

![image-20190510094215976](assets/tx_state_diagram.png)

##### Tx 状态定义（Tx State Definition）

- IDEL

  空闲状态：状态实例创建后的初始化状态

  Initialization state after the state instance is created

- ACTIVE

  激活状态：事务已经开始还未结束时的状态

  Status after receiving `TxStartedEvent`

* FAILED

  失败状态：子事务结束状态，事务失败

  Status after receiving `TxAbortedEvent`

* COMMITTED

  已提交状态：子事务结束状态，事务已经提交

  it is final state, the sub-transaction committed

* COMPENSATED

  已补偿状态：子事务结束状态，事务已经补偿

  it is final state, the sub-transactions compensate successfully

| <font size=1>State (Current/Next)</font> | <font size=1>IDEL</font>           | <font size=1>ACTIVE</font>         | <font size=1>FAILED</font>         | <font size=1>COMMITTED</font>      | <font size=1>COMPENSATED</font>    |
| ---------------------------------------- | ---------------------------------- | ---------------------------------- | ---------------------------------- | ---------------------------------- | ---------------------------------- |
| <font size=1>IDEL</font>                 | <font size=1>E<sub>ss</sub></font> | <font size=1>E<sub>ts</sub></font> |                                    |                                    |                                    |
| <font size=1>ACTIVE</font>               |                                    |                                    | <font size=1>E<sub>te</sub></font> |                                    | <font size=1>E<sub>ta</sub></font> |
| <font size=1>FAILED</font>               |                                    |                                    |                                    | <font size=1>E<sub>ts</sub></font> |                                    |
| <font size=1>COMMITTED</font>            |                                    |                                    | <font size=1>E<sub>te</sub></font> |                                    | <font size=1>E<sub>ta</sub></font> |
| <font size=1>COMPENSATED</font>          |                                    |                                    |                                    |                                    |                                    |



### 时序图（Sequence Diagram）

* 正常时序图（saga-sequence-successful-scenario）

![image-20190510094215976](assets/saga-sequence-successful-scenario.png)

以上场景，共发送6个事件，下表描述事件发送顺序和状态变化

In the above scenario, a total of 6 events are sent. The following table describes the event sending sequence and status changes.

| <font size=2>id</font> | <font size=2>current state</font>       | <font size=2>event</font>              | <font size=2>next state</font>          |
|----| ------------------- | ------------------ | ------------------- |
|<font size=2>1</font>| <font size=2>START</font>               | <font size=2>SagaStartedEvent-1</font> | <font size=2>IDEL</font>               |
|<font size=2>2</font>| <font size=2>IDEL</font>                | <font size=2>TxStartedEvent-11</font>  | <font size=2>PARTIALLY_ACTIVE</font>    |
|<font size=2>3</font>| <font size=2>PARTIALLY_ACTIVE</font>    | <font size=2>TxEndedEvent-11</font>    | <font size=2>PARTIALLY_COMMITTED</font> |
|<font size=2>4</font>| <font size=2>PARTIALLY_COMMITTED</font> | <font size=2>TxStartedEvent-12</font>  | <font size=2>PARTIALLY_ACTIVE</font>    |
|<font size=2>5</font>| <font size=2>PARTIALLY_ACTIVE</font>    | <font size=2>TxEndedEvent-12</font>    | <font size=2>PARTIALLY_COMMITTED</font> |
|<font size=2>6</font>| <font size=2>PARTIALLY_COMMITTED</font> | <font size=2>SagaEndedEvent-1</font>   | <font size=2>END</font>                 |




* 异常时序图(酒店服务内部异常) saga-sequence-hotel-inner-exception-scenario

![image-20190510103456016](assets/saga-sequence-hotel-inner-exception-scenario.png)

以上场景，共发送6个事件，下表描述了事件发送顺序和状态变化，这里需要注意的是如果BOOKING捕获的是HOTEL的内部异常，那么不需要再发送SagaAbortedEvent事件。补偿的发起由Saga状态机状态转换到FAILED时触发[34]，Saga状态机会发送 TxCompensateEvent 事件给状态是 COMMITTED 的Tx状态机[38]，补偿执行完毕后CAR服务会发送 TxCompensitedEvent 给 Saga 状态机[42]，Saga 状态机判断所有Tx状态机的状态，如果还存在 COMMITTED 状态的 Tx 时则保持自身状态 FAILED 不变[43]，否则修改自身状态为 COMPENSATED[44]

In the above scenario, a total of 6 events are sent. The following table describes the event sending sequence and status changes. It should be noted here that if BOOKING catches the internal exception of the HOTEL, then there is no need to send the SagaAbortedEvent event. The compensation trigger is triggered by the Saga state machine state to FAILED [34], and the Saga state machine sends the TxCompensateEvent event to the Tx state machine whose current state is COMMITTED [38]. After the compensation is completed, the CAR service will send TxCompensitedEvent to the Saga state machine [42]. The Saga state machine will judge the state of all Tx state machines. If there is still a  COMMITTED state of the Tx, the Tx keep FAILED state [43] otherwise, modify Saga  status to COMPENSATED[44]

**注意：** BOOKING收到异常和ALPHA进行的事物补偿是异步进行的，此时BOOKING要考虑自身情况决定下一步的动作（强烈不建议自动重试）

**NOTE：** BOOKING receives exceptions and ALPHA's transaction compensation is asynchronous, BOOKING should consider its own situation to determine the next action (strongly not recommended automatic retry)

**注意：** 因为TxCompensateEvent是Saga发给Tx的内部事件，所以在状态图和状态表中没有体现

**NOTE：** Because TxCompensateEvent is an internal event sent by Saga to Tx, it is not reflected in the state diagram and state table.

| <font size=2>id</font> | <font size=2>current state</font>       | <font size=2>event</font>              | <font size=2>next state</font>          |
|----| ------------------- | ------------------ | ------------------- |
|<font size=2>1</font>| <font size=2>START</font>             | <font size=2>SagaStartedEvent-1</font> | <font size=2>IDEL</font>                |
|<font size=2>2</font>| <font size=2>IDEL</font>                | <font size=2>TxStartedEvent-11</font>  | <font size=2>PARTIALLY_ACTIVE</font>    |
|<font size=2>3</font>| <font size=2>PARTIALLY_ACTIVE</font>    | <font size=2>TxEndedEvent-11</font>    | <font size=2>PARTIALLY_COMMITTED</font> |
|<font size=2>4</font>| <font size=2>PARTIALLY_COMMITTED</font> | <font size=2>TxStartedEvent-12</font>  | <font size=2>PARTIALLY_ACTIVE</font>    |
|<font size=2>5</font>| <font size=2>PARTIALLY_ACTIVE</font>    | <font size=2>TxAbortedEvent-12</font>  | <font size=2>FAILED</font> |
|<font size=2>6</font>| <font size=2>FAILED</font> | <font size=2>TxComponsitedEvent-11</font>   | <font size=2>COMPENSATED</font>                 |




* 异常时序图(酒店服务超时异常-中断线程成功) saga-sequence-hotel-timeout-interrupted-exception-scenario

![image-1](assets/saga-sequence-hotel-timeout-interrupted-exception-scenario.png)

以上场景和酒店服务内部异常处理流程类似，因为线程中断后会抛出 TransactionTimeoutException ，然后HOTEL发送 TxAbortedEvent 事件

The above scenario is similar to the `saga-sequence-hotel-inner-exception-scenario` , because a TransactionTimeoutException is thrown after the thread is interrupted, and then the HOTEL sends a TxAbortedEvent event.

**注意：** Omega设置的超时时间必须小于 request 的超时时间和 connect 的超时时间[23]

**NOTE：** Omega's timeout must be less than the request's timeout and connect's timeout [23]

**注意：** Omega超时后有可能会出现线程无法中断的情况，这个时候不会发送 TxAbortedEvent 事件[31]，此时最终可能会导致BOOKING收到超时异常，这种情况会在 `异常时序图(酒店服务超时异常时中断线程失败)` 讨论

**NOTE：** After the Omega timeout, there may be a situation where the thread cannot be interrupted. The TxAbortedEvent event [31] will not be sent. At this time, the BOOKING may receive a timeout exception. This situation will be in the `saga-sequence-hotel-timeout-non-interrupt-exception-scenario` Discussion




* 异常时序图(酒店服务超时异常-中断线程失败) saga-sequence-hotel-timeout-non-interrupt-exception-scenario

![image-20190513095152745](assets/saga-sequence-hotel-timeout-non-interrupt-exception-scenario.png)

以上场景是HOTEL服务调用异常的特殊情况（在酒店服务调用可能成功或者失败前请求超时异常 `java.net.SocketTimeoutException`，或者网络异常 `java.net.ConnectException`），在BOOKING收到异常时HOTEL服务的Saga事物状态不确定，为了避免问题进一步扩大，此处将Saga事物状态设置为 `SUSPENDED` ，等待人工介入处理

The above scenario is a special case of the HOTEL service  exception (request timeout exception `java.net.SocketTimeoutException`  or a network exception `java.net.ConnectException`) , HOTEL service call may succeed or fail, In order to avoid further expansion of the error, the Saga transaction status is set to `SUSPENDED` here, waiting for manual intervention.

**注意：** 为了尽可能避免以上问题的发生可以设置Omega超时时间，并且小于 request 的超时时间和 connect 的超时时间，这样在大部分情况下HOTEL处理线程都可以被终止，并且发送 `TxAbortedEvent` 事件，这样处理过程就变成了 `异常时序图(酒店服务超时异常-中断线程成功)`

**NOTE：** In order to avoid the above problems as much as possible, the Omega timeout less than the request timeout and the connect timeout. In most cases the HOTEL processing thread can be terminated and the `TxAbortedEvent` event is sent, the process is the same as the  `saga-sequence-hotel-timeout-interrupted-exception-scenario`

| <font size=2>id</font> | <font size=2>current state</font>                            | <font size=2>event</font>                                    | <font size=2>next state</font>                              |
| ---------------------- | ------------------------------------------------------------ | ------------------------------------------------------------ | ----------------------------------------------------------- |
| <font size=2>1</font>  | <font size=2>START</font>                                    | <font size=2>SagaStartedEvent-1</font>                       | <font size=2>IDEL</font>                                    |
| <font size=2>2</font>  | <font size=2>IDEL</font>                                     | <font size=2>TxStartedEvent-11</font>                        | <font size=2>PARTIALLY_ACTIVE</font>                        |
| <font size=2>3</font>  | <font size=2>PARTIALLY_ACTIVE</font>                         | <font size=2>TxEndedEvent-11</font>                          | <font size=2>PARTIALLY_COMMITTED</font>                     |
| <font size=2>4</font>  | <font size=2>PARTIALLY_COMMITTED</font>                      | <font size=2>TxStartedEvent-12</font>                        | <font size=2>PARTIALLY_ACTIVE</font>                        |
| <font size=2>5</font>  | <font size=2>PARTIALLY_ACTIVE</font>                         | <font size=2 color=red>TxEndedEvent-12 or TxAbortedEvent-12</font> | <font size=2 color=red>PARTIALLY_COMMITTED or FAILED</font> |
| <font size=2>6</font>  | <font size=2 color=red>PARTIALLY_ACTIVE or PARTIALLY_COMMITTED or FAILED</font> | <font size=2>SagaTimeoutEvent-1</font>                       | <font size=2>SUSPEDNED</font>                               |




* 异常时序图(酒店服务失败后补偿异常) saga-sequence-hotel-inner-exception-compensate-exception-scenario

![image-20190513152610572](assets/saga-sequence-hotel-inner-exception-compensate-exception-scenario.png)

以上场景是调用HOTEL补偿方法时失败（在进程N次重试失败后），将Saga事物状态设置为 `SUSPENDED`，等待人工介入处理（这与原来的进行事务补偿失败后自动重试，直到成功的方式不同）

The above scenario is a failure when the HOTEL compensation method is called (retry after failure N), the Saga transaction status is set to `SUSPENDED`, waiting for manual intervention

**注意：** 补偿失败后将永远不在进行补偿，进入挂起状态，等待人工介入

**NOTE：** After the compensation fails, it will never be compensated, enter the suspended state, and wait for manual intervention.

**注意：** 建议允许用户定义补偿失败的策略，例如补偿几次，每次补偿间隔时间等

**NOTE：** It is recommended to allow the user to define the strategy of compensation failure, such as the number of compensation, compensation interval, etc.

**注意：** 补偿失败后Saga状态的设置由TxFSM发起，此时在以下事件列表并没有体现（这可能不利于挂起原因的跟踪，是否考虑内部事件也进行记录）

**NOTE：** After the compensation fails, the Saga state change is triggered by Tx, which is not reflected in the following event list (this may be detrimental to the tracking of the cause of the suspension, whether internal events are also considered for recording)

| <font size=2>id</font> | <font size=2>current state</font>       | <font size=2>event</font>              | <font size=2>next state</font>          |
|----| ------------------- | ------------------ | ------------------- |
|<font size=2>1</font>| <font size=2>START</font>             | <font size=2>SagaStartedEvent-1</font> | <font size=2>IDEL</font>                |
|<font size=2>2</font>| <font size=2>IDEL</font>                | <font size=2>TxStartedEvent-11</font>  | <font size=2>PARTIALLY_ACTIVE</font>    |
|<font size=2>3</font>| <font size=2>PARTIALLY_ACTIVE</font>    | <font size=2>TxEndedEvent-11</font>    | <font size=2>PARTIALLY_COMMITTED</font> |
|<font size=2>4</font>| <font size=2>PARTIALLY_COMMITTED</font> | <font size=2>TxStartedEvent-12</font>  | <font size=2>PARTIALLY_ACTIVE</font>    |
|<font size=2>5</font>| <font size=2>PARTIALLY_ACTIVE</font>    | <font size=2>TxAbortedEvent-12</font>  | <font size=2>FAILED</font> |
|<font size=2>6</font>| <font size=2>FAILED</font> | <font size=2>TxFSM call SagaFSM</font> | <font size=2>SUSPENDED</font>        |



* 异常时序图（订单服务异常）saga-sequence-booking-exception-scenario

![image-20190513160304768](assets/saga-sequence-booking-exception-scenario.png)

以上场景是CAR，HOTEL都调用成功后，BOOKING服务异常是的事件补偿

The above scenario describes the event compensation of the BOOKING service exception after the CAR and the HOTEL are successfully called.

**注意：** 以上时序图简化了 `TxComponsitedEvent` 事件发送过程，此处需要讨论此事件是否需要发送，还是根据补偿方法执行成功就认为补偿成功

**NOTE：** The above sequence diagram simplifies the `TxComponsitedEvent` event sending process (here we need to discuss whether this event needs to be sent or if the compensation method is successful, the compensation is considered successful.).

| <font size=2>id</font> | <font size=2>current state</font>       | <font size=2>event</font>              | <font size=2>next state</font>          |
|----| ------------------- | ------------------ | ------------------- |
|<font size=2>1</font>| <font size=2>START</font>             | <font size=2>SagaStartedEvent-1</font> | <font size=2>IDEL</font>                |
|<font size=2>2</font>| <font size=2>IDEL</font>                | <font size=2>TxStartedEvent-11</font>  | <font size=2>PARTIALLY_ACTIVE</font>    |
|<font size=2>3</font>| <font size=2>PARTIALLY_ACTIVE</font>    | <font size=2>TxEndedEvent-11</font>    | <font size=2>PARTIALLY_COMMITTED</font> |
|<font size=2>4</font>| <font size=2>PARTIALLY_COMMITTED</font> | <font size=2>TxStartedEvent-12</font>  | <font size=2>PARTIALLY_ACTIVE</font>    |
|<font size=2>5</font>| <font size=2>PARTIALLY_ACTIVE</font>    | <font size=2>TxEndedEvent-12</font> | <font size=2>PARTIALLY_COMMITTED</font> |
|<font size=2>6</font>| <font size=2>PARTIALLY_COMMITTED</font> | <font size=2>SagaAbortedEvent-1</font> | <font size=2>COMPENSATED</font>                 |



* 异常时序图(Omega发送SagaEndedEvent通信异常) saga-sequence-omega-exception-sagaendedevent-scenario

![image-20190513175813161](assets/saga-sequence-omega-exception-sagaendedevent-scenario.png)

以上场景是最后发送 `SagaEndedEvent` 失败的情况，如果Saga配置了timeout，那么在超时后Saga状态自动变成 `SUSPENDED` 

The above scenario is the case where the last send `SagaEndedEvent` fails. If Saga configures timeout, the Saga state automatically becomes `SUSPENDED` after the timeout.

**注意：** Saga超时如何设置？经验值？（是否可以根据实际执行时长移动平均后取3Sigma）

**NOTE：** How to set Saga timeout, Experience value? (Or 3 Sigma be taken after moving the average according to the actual execution time?)

**注意：** 如果Saga没有设置超时，那么Saga状态将一直处于 `PARTIALLY_COMMITTED` 状态，除非收到 `SagaEndedEvent` 后事务结束，这样就需要 SagaEndedEvent 需要重发机制（待讨论）

**NOTE：** If Saga does not set a timeout, the Saga state will remain `PARTIALLY_COMMITTED` , unless the transaction ends after receiving `SagaEndedEvent`, which requires a resend mechanism for SagaEndedEvent (to be discussed)




* 异常时序图(Omega发送TxStartedEvent通信异常) saga-sequence-omega-exception-txstartedevent-scenario

![image-20190513220239362](assets/saga-sequence-omega-exception-txstartedevent-scenario.png)

以上场景是Omega RPC 请求失败后抛出 `OmegaException`  给 BOOKING，BOOKING发送 `SagaAbortedEvent`  事件实现事务的补偿，事件与状态组合如下

The above scenario is that after the Omega RPC request fails, `OmegaException` is thrown to BOOKING, and BOOKING sends the `SagaAbortedEvent` event to implement the transaction compensation. The event and state are combined as follows

| <font size=2>id</font> | <font size=2>current state</font>       | <font size=2>event</font>              | <font size=2>next state</font>          |
|----| ------------------- | ------------------ | ------------------- |
|<font size=2>1</font>| <font size=2>START</font>             | <font size=2>SagaStartedEvent-1</font> | <font size=2>IDEL</font>                |
|<font size=2>2</font>| <font size=2>IDEL</font>                | <font size=2>TxStartedEvent-11</font>  | <font size=2>PARTIALLY_ACTIVE</font>    |
|<font size=2>3</font>| <font size=2>PARTIALLY_ACTIVE</font>    | <font size=2>TxEndedEvent-11</font>    | <font size=2>PARTIALLY_COMMITTED</font> |
|<font size=2>4</font>| <font size=2>PARTIALLY_COMMITTED</font> | <font size=2>SagaAbortedEvent-1</font> | <font size=2>FAILED</font> |
|<font size=2>5</font>| <font size=2>FAILED</font> | <font size=2>Tx call Saga FSM</font> | <font size=2>COMPENSATED</font> |

### 其他

* 挂起的事物及时通知用户（MQ或者其他方式）

  Suspend transaction to notify users in time (MQ or other ways)

* 挂起的事物需要有详细的调用过程清单或者拓扑已便于用户手动分析

  The suspended transaction need to have a detailed list of calling procedures or topologies that are easy for users to manually analyze

* 一段时间内经常失败的事物，Alpha熔断相同的请求？

  A transaction that often fails over time, Alpha fuses the same request？

* 关于请求超时，网络超时，Omega超时，Saga超时需要有一个推荐配置，例如必须设置Omega超时，Omega超时必须在所有超时中最小。或者根据Omega的超时设置，动态修改请求的超时时间=Omega超时时间+N秒

  For request timeouts, network timeouts, Omega timeouts, Saga timeouts, a recommended configuration is required, such as Omega timeouts must be set, Omega timeouts must be minimum in all timeouts. Or according to Omega timeout dynamic modification other timeouts，requests = Omega timeout + N seconds

* 状态机的持久化和恢复

  Design of State Machine Persistence

### Reference

* [servicecomb](https://cwiki.apache.org/confluence/display/SERVICECOMB/Using+StateMachine+for+tracing+the+transaction+states)


