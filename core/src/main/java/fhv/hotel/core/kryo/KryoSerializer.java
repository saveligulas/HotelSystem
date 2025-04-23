package fhv.hotel.core.kryo;

import com.esotericsoftware.kryo.kryo5.Kryo;
import com.esotericsoftware.kryo.kryo5.io.Input;
import com.esotericsoftware.kryo.kryo5.io.Output;
import org.jboss.logging.Logger;

import java.io.ByteArrayOutputStream;
import java.util.function.Consumer;

public class KryoSerializer {
    private static final Logger LOG = Logger.getLogger(KryoSerializer.class);
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
        
        // Temporarily disable references to prevent deserialization issues
        boolean originalReferences = kryo.getReferences();
        try {
            kryo.setReferences(false);
            kryo.writeObject(output, object);
        } finally {
            kryo.setReferences(originalReferences);
            output.close();
        }
        
        return outputStream.toByteArray();
    }

    public <T> T deserialize(byte[] data, Class<T> type) {
        if (data == null || data.length == 0) {
            LOG.warn("Attempted to deserialize null or empty data for type: " + type.getSimpleName());
            return null;
        }
        
        Input input = new Input(data);
        T object = null;
        
        // Temporarily disable references to prevent deserialization issues
        boolean originalReferences = kryo.getReferences();
        try {
            kryo.setReferences(false);
            object = kryo.readObject(input, type);
        } catch (Exception e) {
            LOG.error("Failed to deserialize data of length " + data.length + " for type " + type.getSimpleName(), e);
            throw e;
        } finally {
            kryo.setReferences(originalReferences);
            input.close();
        }
        
        return object;
    }
}
