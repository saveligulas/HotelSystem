package fhv.hotel.core.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

public interface IEventModel {
    ObjectMapper MAPPER = new ObjectMapper();

    @JsonProperty("event")
    Event getEvent();

    default Map<String, Object> getData() {
        return IEventModel.MAPPER.convertValue(this, new TypeReference<Map<String, Object>>() {
        });
    }

    default Byte getEventType() {
        return (byte) getEvent().ordinal();
    }
}
