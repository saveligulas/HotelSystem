package fhv.hotel.core.kryo;

import com.esotericsoftware.kryo.kryo5.Kryo;
import fhv.hotel.core.model.*;

import java.util.UUID;


public class StandardKryoConfig {
    public static final Kryo KRYO;

    static {
        KRYO = new Kryo();
        KRYO.setReferences(true);
        KRYO.setRegistrationRequired(false);
        KRYO.register(CustomerCreatedEvent.class);
        KRYO.register(CustomerUpdatedEvent.class);
        KRYO.register(BookingCancelledEvent.class);
        KRYO.register(BookingPaidEvent.class);
        KRYO.register(RoomBookedEvent.class);
        KRYO.register(UUID.class, new UUIDSerializer());
    }

    public static Kryo get() {
        return KRYO;
    }
}
