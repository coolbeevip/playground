# Saga

* Event Definition

  - E<sub>ss</sub> SagaStartedEvent
  - E<sub>se</sub> SagaEndedEvent
  - E<sub>ts</sub> TxStartedEvent
  - E<sub>te</sub> TxEndedEvent
  - E<sub>ta</sub> TxAbortedEvent
  - E<sub>co</sub> TxCompensateEvent
  - IE<sub>sfc</sub> Internal Event Successful full compensation

* Saga State Machine

  ![image-20190420005126848](assets/saga_state_table.png)

  ![saga_state_diagram](assets/saga_state_diagram.png)

* Tx State Machine
  ![image-20190420005126848](assets/tx_state_table.png)

  ![tx_state_diagram](assets/tx_state_diagram.png)

* Reference

  * [servicecomb](https://cwiki.apache.org/confluence/display/SERVICECOMB/Using+StateMachine+for+tracing+the+transaction+states)
  * [state transition table](https://en.wikipedia.org/wiki/State_transition_table)
  * [uml](http://plantuml.com/en/)


