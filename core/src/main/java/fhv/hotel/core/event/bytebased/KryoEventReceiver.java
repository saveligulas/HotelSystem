package fhv.hotel.core.event.bytebased;

import com.esotericsoftware.kryo.kryo5.Kryo;
import fhv.hotel.core.event.IConsumeEvent;
import fhv.hotel.core.event.IReceiveMessage;
import fhv.hotel.core.model.IEventModel;
import fhv.hotel.core.utility.KryoSerializer;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class KryoEventReceiver<T extends IEventModel> implements IReceiveByteMessage<T> {
    private final KryoSerializer kryoSerializer;
    private final Class<T> eventClass;
    private final List<IConsumeEvent<T>> consumers = new ArrayList<>();

    private KryoEventReceiver(Class<T> eventClass, KryoSerializer kryoSerializer) {
        this.eventClass = eventClass;
        this.kryoSerializer = kryoSerializer;
    }

    @Override
    public void receiveAndConsume(byte[] data) {
        if (consumers.isEmpty()) {
            return;
        }

        T event = kryoSerializer.deserialize(data, eventClass);

        for (IConsumeEvent<T> consumer : consumers) {
            consumer.consume(event);
        }
    }

    @Override
    public void registerConsumer(IConsumeEvent<T> consumer) {
        this.consumers.add(consumer);
    }

    public void unregisterConsumer(IConsumeEvent<T> consumer) {
        this.consumers.remove(consumer);
    }

    public static class Builder<T extends IEventModel> {
        private final Class<T> eventClass;
        private final Kryo kryo = new Kryo();

        public Builder(Class<T> eventClass) {
            this.eventClass = eventClass;
            this.kryo.register(eventClass);
        }

        public Builder<T> registerClass(Class<?> clazz) {
            this.kryo.register(clazz);
            return this;
        }

        public Builder<T> registerClasses(Class<?>... classes) {
            for (Class<?> clazz : classes) {
                this.kryo.register(clazz);
            }
            return this;
        }

        public Builder<T> configureKryo(Consumer<Kryo> kryoConfigurator) {
            kryoConfigurator.accept(this.kryo);
            return this;
        }

        public KryoEventReceiver<T> build() {
            KryoSerializer kryoSerializer = new KryoSerializer(this.kryo);
            return new KryoEventReceiver<>(eventClass, kryoSerializer);
        }
    }

    public static <T extends IEventModel> Builder<T> builder(Class<T> eventClass) {
        return new Builder<>(eventClass);
    }
}
