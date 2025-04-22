package fhv.hotel.event.server;

public interface IEventSourcingRepository {
    void saveByteEvent(Byte identifier, byte[] event);
}
