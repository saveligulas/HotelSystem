package fhv.hotel.core.event.bytebased;

import fhv.hotel.core.event.IConsumeEvent;
import fhv.hotel.core.model.IEventModel;

public interface IReceiveByteMessage {
    void receiveAndProcess(byte[] message);
    <T extends IEventModel> void registerConsumer(byte eventTypeId, Class<T> eventClass, IConsumeEvent<T> consumer);
    boolean handlesType(byte type);
    byte[] getEventTypeIds();
}
