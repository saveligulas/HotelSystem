package fhv.hotel.event.repo;

import java.util.List;

public interface IEventSourcingRepository {
    void saveByteEvent(Byte identifier, byte[] event);
    List<byte[]> getAllEvents();
    List<byte[]> getByteEventsByTypeAscending(Byte typeIdentifier);
    byte[] getLatestEventByType(Byte typeIdentifier);

}
