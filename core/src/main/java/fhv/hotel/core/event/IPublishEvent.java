package fhv.hotel.core.event;

import fhv.hotel.core.model.IEventModel;

public interface IPublishEvent<T extends IEventModel> {
    void publish(T event);
}
