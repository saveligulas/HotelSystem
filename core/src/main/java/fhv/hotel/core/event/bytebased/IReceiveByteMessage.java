package fhv.hotel.core.event.bytebased;

import fhv.hotel.core.event.IConsumeEvent;
import fhv.hotel.core.model.IEventModel;

public interface IReceiveByteMessage<T extends IEventModel> {
    void receiveAndConsume(byte[] message);
    void registerConsumer(IConsumeEvent<T> consumer);
}
