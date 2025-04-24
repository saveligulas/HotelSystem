package fhv.hotel.core.event;

import fhv.hotel.core.model.CustomerCreatedEvent;
import fhv.hotel.core.model.IEventModel;

public interface IConsumeEvent<T extends IEventModel> {
    void consume(T event);
    Class<T> getEventClass();
}
