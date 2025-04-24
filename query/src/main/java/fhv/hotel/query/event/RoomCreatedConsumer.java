package fhv.hotel.query.event;

import fhv.hotel.core.event.IConsumeEvent;
import fhv.hotel.core.model.RoomCreatedEvent;
import fhv.hotel.query.model.RoomQueryPanacheModel;
import fhv.hotel.query.service.RoomServicePanache;
import io.quarkus.logging.Log;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

@Singleton
public class RoomCreatedConsumer implements IConsumeEvent<RoomCreatedEvent> {
    @Inject
    RoomServicePanache roomServicePanache;

    @Override
    public void consume(RoomCreatedEvent event) {
        RoomQueryPanacheModel roomModel = mapEventToModel(event);
        roomServicePanache.createRoom(roomModel);
        Log.info("Created room with number: " + roomModel.roomNumber);
    }

    private RoomQueryPanacheModel mapEventToModel(RoomCreatedEvent event) {
        return new RoomQueryPanacheModel(
            event.roomNumber(),
            event.roomName(),
            event.description(),
            event.price()
        );
    }

    @Override
    public Class<RoomCreatedEvent> getEventClass() {
        return RoomCreatedEvent.class;
    }
}
