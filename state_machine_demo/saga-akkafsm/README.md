# Saga

### Event Definition

- E<sub>ss</sub> SagaStartedEvent 
- E<sub>se</sub> SagaEndedEvent
- E<sub>sa</sub>SagaAbortedEvent  (Tx抛出非超时异常时发送)
- E<sub>so</sub>SagaTimeoutEvent (Tx抛出超时异常时发送)
- E<sub>ts</sub> TxStartedEvent
- E<sub>te</sub> TxEndedEvent
- E<sub>ta</sub> TxAbortedEvent (Tx内部方法抛出非超时异常时)
- E<sub>to</sub> TxTimeoutEvent (Tx内部方法抛出超时异常时)
- E<sub>co</sub> TxCompensateEvent
- IE<sub>sfc</sub> Internal Event Successful full compensation



### Saga Sequence Diagram

正常时序图

| id | current state       | event              | next state          |
|----| ------------------- | ------------------ | ------------------- |
|1| START               | SagaStartedEvent-1 | IDEL                |
|2| IDEL                | TxStartedEvent-11  | PARTIALLY_ACTIVE    |
|3| PARTIALLY_ACTIVE    | TxEndedEvent-11    | PARTIALLY_COMMITTED |
|4| PARTIALLY_COMMITTED | TxStartedEvent-12  | PARTIALLY_ACTIVE    |
|5| PARTIALLY_ACTIVE    | TxEndedEvent-12    | PARTIALLY_COMMITTED |
|6| PARTIALLY_COMMITTED | SagaEndedEvent-1   | END                 |

![image-20190420005126848](assets/sequence-booking-normal.png)

异常时序图

| id | current state       | event              | next state          |
|----| ------------------- | ------------------ | ------------------- |
|1| START               | SagaStartedEvent-1 | IDEL                |
|2| IDEL                | TxStartedEvent-11  | PARTIALLY_ACTIVE    |
|3| PARTIALLY_ACTIVE    | TxEndedEvent-11    | PARTIALLY_COMMITTED |
|4| PARTIALLY_COMMITTED | TxStartedEvent-12  | PARTIALLY_ACTIVE    |
|5| PARTIALLY_ACTIVE    | TxEndedEvent-12    | PARTIALLY_COMMITTED |
|6| PARTIALLY_COMMITTED | SagaEndedEvent-1   | END                 |


![image-20190420005126848](assets/saga_state_table.png)

![saga_state_diagram](assets/saga_state_diagram.png)

* 正常状态事件

  SagaStartedEvent-1
  TxStartedEvent-1
  TxEndedEvent-1
  TxStartedEvent-2
  TxEndedEvent-2
  SagaEndedEvent-1

  

* 异常状态事件：Tx2异常

  SagaStartedEvent-1
  TxStartedEvent-1
  TxEndedEvent-1
  TxStartedEvent-2
  TxAbortedEvent-2
  SagaAbortedEvent-1

  

* 异常状态事件：Tx2超时

  SagaStartedEvent-1
  TxStartedEvent-1
  TxEndedEvent-1
  TxStartedEvent-2
  TxAbortedEvent-2
  SagaAbortedEvent-1

* Tx State Machine
  ![image-20190420005126848](assets/tx_state_table.png)

  ![tx_state_diagram](assets/tx_state_diagram.png)

* Reference

  * [servicecomb](https://cwiki.apache.org/confluence/display/SERVICECOMB/Using+StateMachine+for+tracing+the+transaction+states)
  * [state transition table](https://en.wikipedia.org/wiki/State_transition_table)
  * [uml](http://plantuml.com/en/)


