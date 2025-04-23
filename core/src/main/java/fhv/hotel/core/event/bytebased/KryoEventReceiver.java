package fhv.hotel.core.event.bytebased;

import com.esotericsoftware.kryo.kryo5.Kryo;
import com.esotericsoftware.kryo.kryo5.minlog.Log;
import fhv.hotel.core.event.IConsumeEvent;
import fhv.hotel.core.model.IEventModel;
import fhv.hotel.core.kryo.KryoSerializer;
import fhv.hotel.core.utility.HexConverter;
import org.jboss.logging.Logger;

import java.util.*;
import java.util.function.Consumer;

public class KryoEventReceiver implements IReceiveByteMessage {
    private static final Logger LOG = Logger.getLogger(KryoEventReceiver.class);

    private final Map<Byte, Class<? extends IEventModel>> idEventClasses = new HashMap<>();
    private final Map<Class<? extends IEventModel>, List<IConsumeEvent<?>>> consumersByClass = new HashMap<>();
    private final KryoSerializer kryoSerializer;

    public KryoEventReceiver() {
        this.kryoSerializer = new KryoSerializer();
    }

    @Override
    public <T extends IEventModel> void registerConsumer(byte eventTypeId, Class<T> eventClass, IConsumeEvent<T> consumer) {
        kryoSerializer.registerClass(eventClass);

        idEventClasses.put(eventTypeId, eventClass);

        List<IConsumeEvent<?>> consumers = consumersByClass.computeIfAbsent(
                eventClass,
                k -> new ArrayList<>()
        );
        consumers.add(consumer);

        LOG.info("Registered consumer for event type: " + eventClass.getSimpleName() + " with ID: " + eventTypeId);
    }

    @Override
    public boolean handlesType(byte type) {
        return idEventClasses.containsKey(type);
    }

    @Override
    public byte[] getEventTypeIds() {
        byte[] eventTypeIds = new byte[idEventClasses.size()];
        Iterator<Byte> keyIterator = idEventClasses.keySet().iterator();

        for (int i = 0; i < idEventClasses.size(); i++) {
            eventTypeIds[i] = keyIterator.next();
        }

        return eventTypeIds;
    }

    @Override
    public void receiveAndProcess(byte[] message) {
        Log.info("Received event: " + HexConverter.toHex(message));

        if (message == null || message.length <= 1) {
            LOG.warn("Received empty or invalid message");
            return;
        }
        byte eventTypeId = message[0];

        Class<? extends IEventModel> eventClass = idEventClasses.get(eventTypeId);
        if (eventClass == null) {
            LOG.debug("No registered class for event type ID: " + eventTypeId);
            return;
        }

        byte[] eventData = new byte[message.length - 1];
        System.arraycopy(message, 1, eventData, 0, eventData.length);

        IEventModel event;
        try {
            event = kryoSerializer.deserialize(eventData, eventClass);
        } catch (Exception e) {
            LOG.error("Failed to deserialize event data for type: " + eventClass.getSimpleName(), e);
            return;
        }

        List<IConsumeEvent<?>> consumers = consumersByClass.get(eventClass);
        if (consumers == null || consumers.isEmpty()) {
            LOG.debug("No consumers registered for event class: " + eventClass.getSimpleName());
            return;
        }

        for (IConsumeEvent<?> consumer : consumers) {
            notifyConsumer(consumer, event);
        }
    }

    @SuppressWarnings("unchecked") // cast is safe because we register consumer with its class
    private <T extends IEventModel> void notifyConsumer(IConsumeEvent<?> consumer, IEventModel event) {
        try {
            ((IConsumeEvent<T>) consumer).consume((T) event);
        } catch (ClassCastException e) {
            LOG.error("Type mismatch when dispatching event to consumer", e);
        } catch (Exception e) {
            LOG.error("Error in consumer while processing event", e);
        }
    }

    public static class Builder {
        private final KryoEventReceiver receiver = new KryoEventReceiver();

        public Builder configureKryo(Consumer<Kryo> configurator) {
            receiver.kryoSerializer.configureKryo(configurator);
            return this;
        }

        public <T extends IEventModel> Builder registerEventClass(Class<T> eventClass) {
            receiver.kryoSerializer.registerClass(eventClass);
            return this;
        }

        public <T extends IEventModel> Builder registerConsumer(
                byte eventTypeId,
                Class<T> eventClass,
                IConsumeEvent<T> consumer) {
            receiver.registerConsumer(eventTypeId, eventClass, consumer);
            return this;
        }

        public KryoEventReceiver build() {
            return receiver;
        }
    }

    public static Builder builder() {
        return new Builder();
    }
}
