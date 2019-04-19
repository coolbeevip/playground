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

  

  ![image-20190420005436096](assets/tx_state_diagram.png)

* Tx State Machine
  ![image-20190420005126848](assets/tx_state_table.png)

  ![image-20190420005926716](assets/saga_state_diagram.png)

