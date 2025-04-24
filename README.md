# Hotel System - CQRS and Event Sourcing Implementation

This project implements a hotel reservation system using the Command Query Responsibility Segregation (CQRS) pattern combined with Event Sourcing. The system allows for hotel room bookings, customer management, and room administration through a clean, domain-driven design.

## Architecture Overview

The application follows a modular architecture with clear separation of concerns:

```
HotelSystem/
├── command/         # Command-side implementation (write operations)
├── core/            # Shared models and interfaces
├── event/           # Event bus implementation
├── query/           # Query-side implementation (read operations)
└── ui/              # Simple combined OpenAPI UI
```

### CQRS Pattern

The system separates **command operations** (write) from **query operations** (read):

- **Command Side**: Handles operations that modify the system state (create bookings, add customers, etc.)
- **Query Side**: Handles data retrieval operations with models optimized for specific read patterns
- **Event Communication**: Changes from the command side are propagated to the query side via events

## Key Components

### Core Module

Contains shared components used across the application:

- **Event Models**: `CustomerCreatedEvent`, `RoomBookedEvent`, etc.
- **Common Interfaces**: 
  - `IEventModel`: Base contract for all events
  - `IBasicRepository`: Repository pattern abstraction
  - `IPublishEvent`: Event publishing interface
  - `IConsumeEvent`: Event consumption interface

### Custom Event Bus

The application features a custom TCP-based event bus implementation:

- **Server-Client Architecture**:
  - `TCPServer`: Central event router that runs as a standalone service
  - `TCPClient`: Used by both command and query modules to connect to the server
  - `Connection`: Manages the communication channel between clients and server

- **Protocol**: Uses a custom protocol for efficiently transmitting events between components

### Repository Implementations

#### Command Side

Uses a custom in-memory repository implementation:

- `InMemoryBookingRepository`, `InMemoryCustomerRepository`, etc.
- Implements the `IBasicRepository` interface
- Utilizes the `ShallowProjection` pattern to efficiently handle object relationships
- Prevents infinite reference loops when storing complex domain objects
- Allows for 1-deep relation depiction if retrieved

#### Query Side

Uses Hibernate Panache for object persistence:

- `BookingQueryPanacheModel`, `CustomerQueryPanacheModel`, etc.
- JPA annotations for entity mapping
- Optimized structures for query performance

### Event Consumption

Events flow through the system in the following way:

1. **Publication**: Command side produces events via `IPublishEvent`
2. **Transmission**: Events are sent over the TCP Event Bus
3. **Reception**: Query side receives events via `IReceiveByteMessage`
4. **Routing**: `IReceiveByteMessage` then routes events to appropriate consumers
5. **Consumption**: Event-specific consumers (`IConsumeEvent`) update query models

## Setting Up and Running the Application

### Option 1: Using Docker Compose

The application can be run using Docker Compose for easier setup:

```bash
# From the project root
docker-compose up -d
```

This will start all necessary services:
- Command Service on port 9000
- Query Service on port 9005
- Event Bus on port 5672

### Option 2: Using Gradle

For development, you can use the provided Gradle tasks:

```bash
# IMPORTANT: Use the parallel flag or it won't work correctly
./gradlew devOrdered --parallel
```

This runs the services in the correct order:
1. Event Bus
2. Command Service
3. Query Service

## Enabling Initial Data Rollout

When starting the query server, you can enable initial data rollout to populate the database with events:

1. Open `query/src/main/java/fhv/hotel/query/event/EventConfig.java`
2. In the `initClient()` method, set the second parameter to `true` in the TCPClient constructor:
   ```java
   TCPClient client = new TCPClient(vertx, true, byteMessageReceiver);
   ```
   
This triggers the client to request all historical events from the event bus when connecting, allowing the query side to build its model from past events.

## Accessing the API

Once the services are running:

1. Open `ui/index.html` in your browser to access the combined OpenAPI UI
2. Use the tabs to switch between Command and Query APIs

**Note**: The GET endpoints in the Command API (like `/booking/{id}`) are primarily there for testing the custom database implementation. In a production CQRS system, read operations would typically be handled by the Query API.

## Domain Models

### Command Side

- **Customer**: Personal information and booking history
- **Room**: Hotel room details including number, name, description, and price
- **Booking**: Reservation linking a customer to a room for specific dates

### Query Side

Specialized models optimized for specific query patterns:

- **BookingQueryPanacheModel**: Flat representation of bookings
- **CustomerQueryPanacheModel**: Customer data optimized for queries
- **RoomQueryPanacheModel**: Room information for availability checks

## Event Types

The system uses various events to propagate changes:

- **Customer Events**: `CustomerCreatedEvent`, `CustomerUpdatedEvent`
- **Room Events**: `RoomCreatedEvent`, `RoomUpdatedEvent`
- **Booking Events**: `RoomBookedEvent`, `BookingPaidEvent`, `BookingCancelledEvent`

Each event contains the relevant data needed for the query side to update its models.
