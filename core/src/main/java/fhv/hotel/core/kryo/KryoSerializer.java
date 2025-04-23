package fhv.hotel.core.kryo;

import com.esotericsoftware.kryo.kryo5.Kryo;
import com.esotericsoftware.kryo.kryo5.io.Input;
import com.esotericsoftware.kryo.kryo5.io.Output;

import java.io.ByteArrayOutputStream;
import java.util.function.Consumer;

public class KryoSerializer {
    private final Kryo kryo;

    public KryoSerializer() {
        this.kryo = StandardKryoConfig.get();
    }

    public void registerClass(Class<?> clazz) {
        kryo.register(clazz);
    }

    public void configureKryo(Consumer<Kryo> configurator) {
        configurator.accept(kryo);
    }

    public <T> byte[] serialize(T object) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Output output = new Output(outputStream);
        kryo.writeObject(output, object);
        output.close();
        return outputStream.toByteArray();
    }

    public <T> T deserialize(byte[] data, Class<T> type) {
        Input input = new Input(data);
        T object = kryo.readObject(input, type);
        input.close();
        return object;
    }
}
