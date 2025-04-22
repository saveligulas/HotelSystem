package fhv.hotel.event.server;

import jakarta.inject.Singleton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Singleton
public class InMemoryEventSourcing implements IEventSourcingRepository {
    private final Map<Byte, List<byte[]>> events = new HashMap<>();

    @Override
    public void saveByteEvent(Byte identifier, byte[] event) {
        events.computeIfAbsent(identifier, k -> new ArrayList<>()).add(event);
    }
}
