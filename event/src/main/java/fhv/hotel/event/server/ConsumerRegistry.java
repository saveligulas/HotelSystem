package fhv.hotel.event.server;

import io.vertx.core.net.NetSocket;
import jakarta.inject.Singleton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Singleton
public class ConsumerRegistry {
    private Map<Byte, List<NetSocket>> eventSocketConsumers = new HashMap<>();

    public void add(Byte eventType, NetSocket netSocket) {
        List<NetSocket> list = eventSocketConsumers.computeIfAbsent(eventType, k -> new ArrayList<>());
        list.add(netSocket);
    }

    public List<NetSocket> getSockets(Byte eventType) {
        return eventSocketConsumers.computeIfAbsent(eventType, k -> new ArrayList<>());
    }
}
