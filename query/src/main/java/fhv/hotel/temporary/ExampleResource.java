package fhv.hotel.temporary;

import fhv.hotel.core.event.IConsumeEvent;
import fhv.hotel.core.event.bytebased.IReceiveByteMessage;
import fhv.hotel.core.event.bytebased.KryoEventReceiver;
import fhv.hotel.core.model.CustomerCreatedEvent;
import io.vertx.core.Vertx;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.ws.rs.Produces;

@ApplicationScoped
public class ExampleResource {
    @Inject
    IConsumeEvent<CustomerCreatedEvent> customerCreatedEventConsumer;

    @Inject
    Vertx vertx;

    @Produces
    @Singleton
    public IReceiveByteMessage byteMessageReceiver() {
        return KryoEventReceiver.builder()
                .registerConsumer(CustomerCreatedEvent.EVENT.getOrdinalByte(), CustomerCreatedEvent.class, customerCreatedEventConsumer)
                .build();
    }
}
