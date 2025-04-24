package fhv.hotel.event.repo;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "byte_events", schema = "public")
public class ByteEvent {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "type_identifier", nullable = false)
    private Byte typeIdentifier;

    @Lob
    @Column(name = "event", nullable = false)
    private byte[] event;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Byte getTypeIdentifier() {
        return typeIdentifier;
    }

    public void setTypeIdentifier(Byte typeIdentifier) {
        this.typeIdentifier = typeIdentifier;
    }

    public byte[] getEvent() {
        return event;
    }

    public void setEvent(byte[] event) {
        this.event = event;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
