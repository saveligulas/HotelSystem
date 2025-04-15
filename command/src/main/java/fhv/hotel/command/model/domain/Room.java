package fhv.hotel.command.model.domain;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import java.util.List;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE)
public class Room implements IShallowProjection<Room, Long> {
    private Long roomNumber;
    private String roomName;
    private String description;
    @JsonManagedReference
    private List<Booking> bookings;

    //Used for database projection
    public Room(Long roomNumber) {
        this.roomNumber = roomNumber;
    }

    public Room(Long roomNumber, String roomName, String description, List<Booking> bookings) {
        this.roomNumber = roomNumber;
        this.roomName = roomName;
        this.description = description;
        this.bookings = bookings;
    }

    public Long roomNumber() {
        return roomNumber;
    }

    public String roomName() {
        return roomName;
    }

    public String description() {
        return description;
    }

    public List<Booking> bookings() {
        return bookings;
    }

    public void addBooking(Booking booking) { bookings.add(booking); }

    public void setBookings(List<Booking> bookings) {
        this.bookings = bookings;
    }

    @Override
    public boolean isShallow() {
        return roomNumber != null &&
                roomName == null &&
                description == null &&
                bookings == null;
    }

    @Override
    public Room buildShallowModel(Long roomNumber) {
        return new Room(roomNumber);
    }

    @Override
    public Long getID() {
        return this.roomNumber;
    }


}
