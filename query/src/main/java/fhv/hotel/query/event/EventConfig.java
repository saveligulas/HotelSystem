package fhv.hotel.query.event;

import fhv.hotel.core.event.IConsumeEvent;
import fhv.hotel.core.event.bytebased.IReceiveByteMessage;
import fhv.hotel.core.event.bytebased.KryoEventReceiver;
import fhv.hotel.core.model.*;
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
    
    @Inject
    IConsumeEvent<CustomerUpdatedEvent> customerUpdatedEventConsumer;
    
    @Inject
    IConsumeEvent<RoomBookedEvent> roomBookedEventConsumer;
    
    @Inject
    IConsumeEvent<BookingPaidEvent> bookingPaidConsumer;
    
    @Inject
    IConsumeEvent<BookingCancelledEvent> bookingCancelledConsumer;

    @PostConstruct
    public void initClient() {
        IReceiveByteMessage byteMessageReceiver = new KryoEventReceiver.Builder()
                .registerConsumer(CustomerCreatedEvent.EVENT.getOrdinalByte(), CustomerCreatedEvent.class, customerCreatedEventConsumer)
                .registerConsumer(CustomerUpdatedEvent.EVENT.getOrdinalByte(), CustomerUpdatedEvent.class, customerUpdatedEventConsumer)
                .registerConsumer(RoomBookedEvent.EVENT.getOrdinalByte(), RoomBookedEvent.class, roomBookedEventConsumer)
                .registerConsumer(BookingPaidEvent.EVENT.getOrdinalByte(), BookingPaidEvent.class, bookingPaidConsumer)
                .registerConsumer(BookingCancelledEvent.EVENT.getOrdinalByte(), BookingCancelledEvent.class, bookingCancelledConsumer)
                .build();
                
        TCPClient client = new TCPClient(vertx, true, byteMessageReceiver);
    }
}
