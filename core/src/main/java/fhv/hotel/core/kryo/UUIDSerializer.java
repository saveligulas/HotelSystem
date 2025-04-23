package fhv.hotel.core.kryo;

import com.esotericsoftware.kryo.kryo5.Kryo;
import com.esotericsoftware.kryo.kryo5.Serializer;
import com.esotericsoftware.kryo.kryo5.io.Input;
import com.esotericsoftware.kryo.kryo5.io.Output;

import java.util.UUID;

public class UUIDSerializer extends Serializer<UUID> {
    @Override
    public void write(Kryo kryo, Output output, UUID uuid) {
        output.writeLong(uuid.getMostSignificantBits());
        output.writeLong(uuid.getLeastSignificantBits());
    }

    @Override
    public UUID read(Kryo kryo, Input input, Class<? extends UUID> type) {
        long most = input.readLong();
        long least = input.readLong();
        return new UUID(most, least);
    }
}
