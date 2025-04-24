package fhv.hotel.query.event;

import fhv.hotel.core.event.IConsumeEvent;
import fhv.hotel.core.event.bytebased.IReceiveByteMessage;
import fhv.hotel.core.event.bytebased.KryoEventReceiver;
import fhv.hotel.core.model.CustomerCreatedEvent;
import fhv.hotel.event.client.TCPClient;
import io.quarkus.runtime.Startup;
import io.vertx.core.Vertx;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
@Startup
public class EventConfig {

    @Inject
    Vertx vertx;

    @Inject
    IConsumeEvent<CustomerCreatedEvent> customerCreatedEventConsumer;

    @PostConstruct
    public void initClient() {
        IReceiveByteMessage byteMessageReceiver = new KryoEventReceiver.Builder()
                .registerConsumer(CustomerCreatedEvent.EVENT.getOrdinalByte(), CustomerCreatedEvent.class, customerCreatedEventConsumer)
                .build();
        TCPClient client = new TCPClient(vertx, true, byteMessageReceiver);
    }
}
