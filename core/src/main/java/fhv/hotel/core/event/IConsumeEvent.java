package fhv.hotel.core.event;

import fhv.hotel.core.model.IEventModel;

public interface IConsumeEvent<T extends IEventModel> {
    void consume(T event);
}
