# Chapter 9: TCP Event Bus (`TCPServer`, `TCPClient`, `Connection` - event module)

Welcome back! In [Chapter 8: Event Listener (`EventListener`)](08_event_listener___eventlistener___.md), we saw how our application uses a central `EventListener` to route incoming event messages to the correct handlers within the *same* running application.

But what if the part of the system that *publishes* an event (like our `CustomerService` creating a `CustomerCreatedEvent`) is running completely separately from the part that needs to *consume* it (like a `NotificationService` wanting to send a welcome email)? They might be different programs, maybe even running on different computers! How does the event message travel between them?

## What's the Problem?

Imagine our hotel system is split into two separate services:
1.  **Command Service:** Handles requests like creating customers, bookings, etc. (where `CustomerService` lives).
2.  **Notification Service:** Handles sending emails and other notifications.

When the Command Service creates a new customer, it publishes a `CustomerCreatedEvent`. The Notification Service needs to receive this event to send a welcome email. Since they are separate running applications, the event can't just be passed directly in memory like we saw with the `EventListener`. We need a way to send the event message *over a network* from the Command Service to the Notification Service.

## The Solution: A TCP Event Bus

To solve this, our project includes a simple **TCP Event Bus**. Think of it like a dedicated postal service specifically for our application's events.

*   **TCP:** This is a fundamental internet protocol (like the rules for making a reliable phone call between computers). It ensures messages sent from one computer arrive correctly at the other.
*   **Event Bus:** Like a real bus route, events "get on" at one stop (the publishing service) and "get off" at another (the consuming service).

This event bus has a few key parts found in the `event` module:

1.  **`TCPServer` (The Central Post Office):** A single, central program that runs and listens for connections from different services. It acts as the main hub for routing event mail.
2.  **`TCPClient` (The Mailbox/Local Post Office Branch):** Each service (like our Command Service or Notification Service) runs its own `TCPClient`. This client connects to the central `TCPServer`, allowing the service to send outgoing events and receive incoming events.
3.  **`Connection` (The Phone Line/Mail Route):** Represents the actual established link between a `TCPClient` (in a service) and the `TCPServer`. This component, existing on both the client and server side, manages the communication flow and keeps track of things like which events a specific client wants to receive.

Together, these components create a network channel allowing different services to publish and subscribe to events.

## How It Works: Sending an Event Across Services

Let's walk through how a `CustomerCreatedEvent` travels from the Command Service to the Notification Service using the TCP Event Bus:

**Setup:**

1.  The central `TCPServer` is started and running, listening for connections (like opening the main post office).
2.  The Command Service starts up and runs its `TCPClient`, which connects to the `TCPServer` (like setting up a mailbox connected to the postal network).
3.  The Notification Service starts up and runs its `TCPClient`, also connecting to the `TCPServer`.
4.  **Subscription:** The Notification Service's `TCPClient`, upon connecting, tells the `TCPServer`, "Hey, I'm interested in receiving `CUSTOMER_CREATED` events!" The server remembers this (using the `ConsumerRegistry`).

**Event Flow:**

1.  **Publishing (Command Service):** Inside the Command Service, `CustomerService` creates a customer and calls `publisher.publish(customerCreatedEvent)`. The specific [Event Publishing (`IPublishEvent`)](06_event_publishing___ipublishevent___.md) implementation used here knows about the `TCPClient`.
2.  **Sending (Command Service):** The `IPublishEvent` implementation takes the `CustomerCreatedEvent` data, formats it according to the event bus's rules (our custom protocol), and tells its `TCPClient` to send this data over the network to the `TCPServer`.
3.  **Receiving & Routing (TCPServer):** The `TCPServer` receives the data packet. It looks inside to see it's a `CUSTOMER_CREATED` event. It checks its list (`ConsumerRegistry`) and sees that the Notification Service's connection is interested in this type of event.
4.  **Forwarding (TCPServer):** The `TCPServer` sends the event data packet over the network specifically to the Notification Service's `TCPClient`.
5.  **Receiving (Notification Service):** The Notification Service's `TCPClient` receives the data packet from the server.
6.  **Local Dispatch (Notification Service):** The `TCPClient` acts like (or works with) an [IReceiveMessage](07_event_consumption___iconsumeevent_____ireceivemessage___.md) implementation. It passes the raw event data (likely still as text/JSON) to the local [EventListener](08_event_listener___eventlistener___.md).
7.  **Consumption (Notification Service):** The `EventListener` identifies the event type and routes it to the appropriate `IConsumeEvent` handler (like our `WelcomeEmailSender`), which finally processes the event (e.g., logs the message about sending an email).

The event successfully traveled from one service to another over the network!

## Code Examples (Simplified View)

Let's peek at how these components might be used or started.

**1. Starting the Server**

The `TCPServer` needs to be running somewhere. It listens on a specific network port (like a phone number).

```java
// File: event/src/main/java/fhv/hotel/event/server/TCPServer.java

@Singleton // Only one server needed
@Startup   // Start automatically when the app runs
public class TCPServer {
    @Inject Vertx vertx; // Vert.x is a library helping with network tasks

    @PostConstruct // Method to run after the server object is created
    public void start() {
        NetServer server = vertx.createNetServer(); // Create a TCP server

        // Tell the server what to do when a client connects
        server.connectHandler(this::handleNewConnection); 

        // Start listening on port 5672
        server.listen(5672, res -> {
            if (res.succeeded()) {
                Log.info("TCP Server running on port 5672"); 
            } else {
                Log.error("Failed to start TCP Server");
            }
        });
    }

    // This method is called when a new client connects
    private void handleNewConnection(NetSocket socket) {
        Log.info("New client connected from: " + socket.remoteAddress());
        // Create a Connection object to manage this specific link
        Connection connection = new Connection(socket); 
        // ... store connection & set up handlers ...
    }
}
```

*   This code uses the Vert.x library to create a TCP server.
*   `start()` makes it listen on port `5672`.
*   `handleNewConnection` is triggered every time a `TCPClient` connects. It creates a `Connection` object to manage that specific client link.

**2. Starting a Client**

Each service that wants to use the event bus needs to run a `TCPClient`.

```java
// File: event/src/main/java/fhv/hotel/event/client/TCPClient.java

public class TCPClient {
    @Inject Vertx vertx; // Use Vert.x for client operations too
    private Connection connection; // Stores our link to the server

    public void start() {
        NetClient client = vertx.createNetClient(); // Create a TCP client

        // Try to connect to the server (running on localhost, port 5672)
        client.connect(5672, "localhost", conn -> {
            if (conn.succeeded()) {
                Log.info("TCP Client connected successfully!");
                // Create a Connection object for our end of the link
                this.connection = new Connection(conn.result(), /* ... */); 
                // Now we can potentially send/receive events via this.connection
            } else {
                Log.error("TCP Client connection failed: " + conn.cause());
            }
        });
    }

    // Methods to send data would use:
    // if (this.connection != null) { /* connection.send(...) */ }
}
```

*   This code attempts to connect to the `TCPServer` at `localhost:5672`.
*   If successful, it creates its own `Connection` object representing the client's end of the link.
*   The service would then use this `connection` to send event data or handle incoming event data.

**3. Conceptual: Publishing via Client**

An [IPublishEvent](06_event_publishing___ipublishevent___.md) implementation in the Command Service might use the `TCPClient` like this (highly simplified):

```java
// Conceptual Publisher Implementation using TCPClient
public class TcpEventPublisher implements IPublishEvent<CustomerCreatedEvent> {
    
    @Inject TCPClient tcpClient; // Get the client for this service

    @Override
    public void publish(CustomerCreatedEvent event) {
        // 1. Convert the event object to the agreed format (e.g., JSON)
        String eventJson = convertToJson(event); 

        // 2. Add protocol headers (telling server it's a PUBLISH frame)
        Buffer messageBuffer = createProtocolMessage(FrameType.PUBLISH, eventJson);

        // 3. Tell the TCPClient's connection to send the data
        tcpClient.send(messageBuffer); 
    }
    // ... helper methods: convertToJson, createProtocolMessage ...
}
```

*   The publisher prepares the event data and sends it via the injected `TCPClient`.

**4. Conceptual: Receiving via Client**

The `TCPClient`'s `Connection` handles incoming data:

```java
// Simplified view inside event/src/main/java/fhv/hotel/event/client/Connection.java
class Connection {
    private NetSocket socket;
    @Inject EventListener localEventListener; // Get the local event router

    public Connection(NetSocket socket, /*...*/) {
        this.socket = socket;
        // Tell the socket to call handleIncomingData when data arrives
        socket.handler(this::handleIncomingData); 
        // ... initial setup (like sending subscription info) ...
    }

    public void handleIncomingData(Buffer data) {
        // 1. Parse the incoming data buffer based on the protocol
        //    (Check header, extract event type and JSON payload)
        String eventJson = extractEventPayload(data);
        String eventType = extractEventType(data); // e.g., "CUSTOMER_CREATED"

        // 2. If it's an event meant for us, pass it to the local EventListener
        if (/* it's a consumable event */) {
             // This triggers the flow from Chapter 7 & 8 locally!
            localEventListener.handleJsonMessageAbstract(eventJson); 
        }
    }
    // ... helper methods: extractEventPayload, extractEventType ...
}
```

*   When data arrives on the socket, `handleIncomingData` is called.
*   It decodes the message using the custom protocol rules.
*   It passes the extracted event JSON to the service's *local* `EventListener`, which then routes it to the final `IConsumeEvent` handler.

## Under the Hood: The Network Dance

Let's visualize the flow when the Command Service publishes an event and the Notification Service receives it.

```mermaid
sequenceDiagram
    participant CS_Client as Command Service TCPClient
    participant TCPSrv as TCPServer
    participant CReg as ConsumerRegistry (inside TCPServer)
    participant NS_Client as Notification Service TCPClient
    participant NS_Listener as Notification Service EventListener

    Note over CS_Client, TCPSrv, NS_Client: Connections established. NS_Client has subscribed to CUSTOMER_CREATED.

    CS_Client->>+TCPSrv: Send PUBLISH Frame (CUSTOMER_CREATED event data)
    Note right of CS_Client: Event published locally, sending to server

    TCPSrv->>+CReg: What connections want CUSTOMER_CREATED?
    CReg-->>-TCPSrv: NS_Client's connection
    Note right of TCPSrv: Look up subscribers

    TCPSrv->>+NS_Client: Forward event data packet
    Note right of TCPSrv: Send to interested client

    NS_Client->>NS_Client: Receive data, parse protocol frame
    Note right of NS_Client: Data arrives from server

    NS_Client->>+NS_Listener: handleJsonMessageAbstract(eventJson)
    Note right of NS_Client: Pass to local event routing

    NS_Listener->>NS_Listener: Route to WelcomeEmailSender.consume()
    Note right of NS_Listener: Local consumption logic runs

    NS_Listener-->>-NS_Client: (Processing complete)
    NS_Client-->>-TCPSrv: (Acknowledgement optional)
    TCPSrv-->>-CS_Client: (Acknowledgement optional)

```

This shows how the `TCPServer` acts as a central router, using the `ConsumerRegistry` to know where to forward events received from publishing clients.

**Deeper Dive into Code Components:**

*   **`TCPServer` (`event/server/TCPServer.java`):** As shown before, uses Vert.x `NetServer` to listen. `handleNewConnection` creates a server-side `Connection` instance for each client.
*   **`TCPClient` (`event/client/TCPClient.java`):** Uses Vert.x `NetClient` to connect. Creates a client-side `Connection`. Services would `@Inject` and use this client.
*   **`Connection` (Server - `event/server/Connection.java`):** Manages the state of a single client link on the server side. `handleIncomingData` processes messages from the client based on state (e.g., initial registration messages, later event publishing messages). It uses `ConsumerRegistry` to register client interests and `Publisher` to send messages *to* the client.
*   **`Connection` (Client - `event/client/Connection.java`):** Manages the client's end of the link. Sends initial setup messages (like subscriptions). `handleIncomingData` receives messages *from* the server (like forwarded events) and passes them to the local `EventListener`.
*   **`ConsumerRegistry` (`event/server/ConsumerRegistry.java`):** A simple server-side registry (like a `Map`) keeping track of which event types (identified by a `Byte` ID in the protocol) are consumed by which client connections (`NetSocket`).
    ```java
    // Simplified view
    @Singleton
    public class ConsumerRegistry {
        // Map: Event Type ID -> List of Sockets listening for it
        private Map<Byte, List<NetSocket>> eventConsumers = new HashMap<>();
    
        public void add(Byte eventType, NetSocket socket) {
            eventConsumers.computeIfAbsent(eventType, k -> new ArrayList<>())
                          .add(socket);
        }
    
        public List<NetSocket> getSockets(Byte eventType) {
            return eventConsumers.getOrDefault(eventType, List.of());
        }
    }
    ```
*   **`Publisher` (Server - `event/server/Publisher.java`):** Used by the server to send (publish) an event *to* interested clients. It gets the list of sockets from `ConsumerRegistry` and writes the data to each one.
    ```java
    // Simplified view
    @Singleton
    public class Publisher {
        @Inject ConsumerRegistry registry;
    
        public void publish(Buffer data, Byte eventIdentifier) {
            // Find all sockets interested in this event type
            for (NetSocket socket : registry.getSockets(eventIdentifier)) {
                // Send the data to each interested client
                socket.write(data); 
            }
        }
    }
    ```
*   **Protocol (`event/protocol/...`):** Defines the exact format of messages sent over TCP. Includes `Header` (with message `FrameType` like `PUBLISH` or `CONSUME`, size, etc.) and rules for the message body. This ensures clients and server understand each other. We kept this detail low here, but it's crucial for making the communication work.

## Conclusion

In this chapter, we explored the **TCP Event Bus**, a mechanism for sending events between separate services over a network.

*   It acts like a **dedicated postal service** for application events.
*   It uses **TCP** for reliable network communication.
*   Key components include:
    *   **`TCPServer`:** The central hub/post office.
    *   **`TCPClient`:** Connects each service to the hub (the local mailbox).
    *   **`Connection`:** Manages the link between a client and the server.
    *   **`ConsumerRegistry`:** Tracks which clients want which events.
*   It allows services to **publish** events to the bus and **subscribe** to events they are interested in, enabling communication between decoupled services.
*   It relies on a **custom protocol** to structure the messages sent over the network.

This provides the underlying transport layer that makes event-driven communication across different parts of our potentially distributed system possible.

We've covered commands, queries (implicitly via fetching data), events, and how they communicate. There's one more optimization technique used in this project we should look at, related to how data is loaded efficiently, especially when dealing with relationships between models.

Let's explore that in the final chapter: [Chapter 10: Shallow Projection (`IShallowProjection`)](10_shallow_projection___ishallowprojection___.md).

