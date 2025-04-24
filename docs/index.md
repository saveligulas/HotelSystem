# Documentation: CQRS - Hotel system

This project implements a hotel booking system using the **CQRS** (*Command Query Responsibility Segregation*) pattern.
It separates operations that *change* data (**Commands**, like making a booking) from operations that *read* data (**Queries**, like viewing available rooms).
*Events* (like `RoomBookedEvent`) are created by Commands and used to update the Query side, keeping the read models eventually consistent.
The system uses a custom **TCP Event Bus** with **Kryo Serialization** for efficient communication between the Command and Query components.

```mermaid
flowchart TD
    A0["CQRS (Command Query Responsibility Segregation)
"]
    A1["Event Model (IEventModel & Specific Events)
"]
    A2["Event Publishing (IPublishEvent)
"]
    A3["Event Consumption (IConsumeEvent, KryoEventReceiver, IReceiveByteMessage)
"]
    A4["Event Bus Transport (TCP Server/Client & Frame Protocol)
"]
    A5["Command Domain Models & Services
"]
    A6["Query Projections & Panache Models
"]
    A7["Repository (IBasicRepository)
"]
    A8["Kryo Serialization
"]
    A0 -- "Defines Write Side" --> A5
    A0 -- "Defines Read Side" --> A6
    A0 -- "Links Sides Via" --> A4
    A5 -- "Uses for State" --> A7
    A5 -- "Uses to Publish Events" --> A2
    A2 -- "Publishes" --> A1
    A2 -- "Uses Implementation Of" --> A4
    A4 -- "Transports Serialized Data ..." --> A8
    A4 -- "Delivers Events To" --> A3
    A3 -- "Uses to Deserialize" --> A8
    A3 -- "Consumes" --> A1
    A3 -- "Updates" --> A6
    A6 -- "Reads From Query Store" --> A7
    A8 -- "Serializes/Deserializes" --> A1
    A4 -- "Uses for Event Sourcing" --> A7
```

## Chapters

1. [CQRS (Command Query Responsibility Segregation)
](01_cqrs__command_query_responsibility_segregation__.md)
2. [Command Domain Models & Services
](02_command_domain_models___services_.md)
3. [Query Projections & Panache Models
](03_query_projections___panache_models_.md)
4. [Event Model (`IEventModel` & Specific Events)
](04_event_model___ieventmodel____specific_events__.md)
5. [Event Publishing (`IPublishEvent`)
](05_event_publishing___ipublishevent___.md)
6. [Event Bus Transport (TCP Server/Client & Frame Protocol)
](06_event_bus_transport__tcp_server_client___frame_protocol__.md)
7. [Event Consumption (`IConsumeEvent`, `KryoEventReceiver`, `IReceiveByteMessage`)
](07_event_consumption___iconsumeevent____kryoeventreceiver____ireceivebytemessage___.md)
8. [Repository (`IBasicRepository`)
](08_repository___ibasicrepository___.md)
9. [Kryo Serialization
](09_kryo_serialization_.md)
