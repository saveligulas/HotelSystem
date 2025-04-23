package fhv.hotel.event.repo;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


@ApplicationScoped
public class PostgresEventSourcingRepository implements IEventSourcingRepository, PanacheRepositoryBase<ByteEvent, UUID> {

    @Inject
    EntityManager em;

    @Override
    @Transactional
    public void saveByteEvent(Byte identifier, byte[] event) {
        ByteEvent newEvent = new ByteEvent();
        newEvent.setTypeIdentifier(identifier);
        newEvent.setEvent(event);
        persist(newEvent);
    }

    @Override
    @Transactional
    public List<byte[]> getAllEvents() {
        return findAll(Sort.by("createdAt").ascending())
                .stream()
                .map(ByteEvent::getEvent)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<byte[]> getByteEventsByTypeAscending(Byte typeIdentifier) {
        return find("typeIdentifier", Sort.by("createdAt").ascending(), typeIdentifier)
                .stream()
                .map(ByteEvent::getEvent)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public byte[] getLatestEventByType(Byte typeIdentifier) {
        ByteEvent event = find("typeIdentifier", Sort.by("createdAt").descending(), typeIdentifier)
                .firstResult();
        return event != null ? event.getEvent() : null;
    }
}