package fhv.hotel.command.model.domain;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonBackReference;

import java.time.LocalDate;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE)
public class Booking implements IShallowProjection<Booking, UUID> {
    public static final AtomicLong ID_GENERATOR = new AtomicLong(System.currentTimeMillis());

    private UUID uuid;
    private Long bookingNumber;
    private boolean payed;
    private boolean cancelled;
    @JsonBackReference
    private Room room;
    @JsonBackReference
    private Customer customer;
    private LocalDate startDate;
    private LocalDate endDate;

    public Booking(UUID uuid) {
        this.uuid = uuid;
    }

    public Booking(UUID uuid, Long bookingNumber, boolean payed, boolean cancelled, Room room, Customer customer, LocalDate startDate, LocalDate endDate) {
        this.uuid = uuid;
        this.bookingNumber = bookingNumber;
        this.payed = payed;
        this.cancelled = cancelled;
        this.room = room;
        this.customer = customer;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public UUID uuid() {
        return uuid;
    }

    public Long bookingNumber() {
        return bookingNumber;
    }

    public boolean payed() {
        return payed;
    }

    public boolean cancelled() {
        return cancelled;
    }

    public Room room() {
        return room;
    }

    public Customer customer() {
        return customer;
    }

    public LocalDate startDate() {
        return startDate;
    }

    public LocalDate endDate() {
        return endDate;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    @Override
    public boolean isShallow() {
        return uuid != null &&
                bookingNumber == null &&
                room == null &&
                customer == null;
    }

    @Override
    public Booking buildShallowModel(UUID uuid) {
        return new Booking(uuid);
    }

    @Override
    public UUID getID() {
        return this.uuid;
    }
}
