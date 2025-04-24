package fhv.hotel.core.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import fhv.hotel.core.model.IEventModel;
import org.jboss.logging.Logger;

import java.util.HashMap;
import java.util.Map;

@Deprecated
public class EventListener {

    private static final Logger LOG = Logger.getLogger(EventListener.class);

    private class ReceiverConsumerPair<T extends IEventModel> {
        private IReceiveMessage<T> receiver;
        private IConsumeEvent<T> consumer;
        private Class<? extends IEventModel> eventClass;

        public ReceiverConsumerPair(IReceiveMessage<T> receiver, IConsumeEvent<T> consumer, Class<? extends IEventModel> eventClass) {
            this.receiver = receiver;
            this.consumer = consumer;
            this.eventClass = eventClass;
        }
    }

    private Map<String, ReceiverConsumerPair<?>> receiverConsumerPairs = new HashMap<>();

    public <T extends IEventModel> void register(IReceiveMessage<T> receiver, IConsumeEvent<T> consumer, String eventName, Class<T> eventClass) {
        this.receiverConsumerPairs.put(eventName, new ReceiverConsumerPair<>(receiver, consumer, eventClass));

        }

    public void handleJsonMessageAbstract(String jsonMessage) throws JsonProcessingException {
        JsonNode root = IEventModel.MAPPER.readTree(jsonMessage);
        LOG.info("Received JSON message: " + root);
        String eventName = root.get("event").asText();

        ReceiverConsumerPair<?> receiverConsumerPair = receiverConsumerPairs.get(eventName);
        if (receiverConsumerPair == null) {
            LOG.debug("No receiver consumer for event: " + eventName);
            return;
        }
        receiverConsumerPair.receiver.receiveAndConsume(jsonMessage);
    }
}
