package fhv.hotel.command.model.domain;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE)
public class Customer implements IShallowProjection<Customer, UUID> {
    public static final AtomicLong ID_GENERATOR = new AtomicLong(System.currentTimeMillis());

    private UUID uuid;
    private Long customerNumber;
    private String firstName;
    private String lastName;
    private LocalDate birthday;
    @JsonManagedReference
    private List<Booking> bookings;

    public Customer(UUID uuid) {
        this.uuid = uuid;
    }

    public Customer(UUID uuid, Long customerNumber, String firstName, String lastName, LocalDate birthday, List<Booking> bookings) {
        this.uuid = uuid;
        this.customerNumber = customerNumber;
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthday = birthday;
        this.bookings = bookings;
    }

    public UUID uuid() { return uuid; }

    public Long customerNumber() { return customerNumber; }

    public String firstName() { return firstName; }

    public String lastName() { return lastName; }

    public LocalDate birthday() { return birthday; }

    public List<Booking> bookings() { return bookings; }

    public void setBookings(List<Booking> bookings) { this.bookings = bookings; }

    public void addBooking(Booking booking) { bookings.add(booking); }

    public void bookRoom(Room room) {

    }

    @Override
    public boolean isShallow() {
        return uuid != null &&
                customerNumber == null &&
                firstName == null &&
                lastName == null &&
                birthday == null &&
                bookings == null;
    }

    @Override
    public Customer buildShallowModel(UUID uuid) {
        return null;
    }

    @Override
    public UUID getID() {
        return null;
    }

}
