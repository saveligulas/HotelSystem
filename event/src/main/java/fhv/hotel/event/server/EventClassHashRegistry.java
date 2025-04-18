package fhv.hotel.event.server;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventClassHashRegistry {
    private final Map<Byte, String> eventHashes = new HashMap<>();

    public boolean registerEventClass(Byte identifier, String eventClassHash) {
        return false;
    }
}
