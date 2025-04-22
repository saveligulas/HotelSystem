package fhv.hotel.core.event;

import fhv.hotel.core.model.Event;
import fhv.hotel.core.model.IEventModel;

public interface IReceiveMessage<T extends IEventModel> {
    void receiveAndConsume(String json);
    void registerConsumer(IConsumeEvent<T> consumer);
}
