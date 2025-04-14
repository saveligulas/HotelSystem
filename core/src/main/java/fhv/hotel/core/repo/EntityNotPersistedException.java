package fhv.hotel.core.repo;

public class EntityNotPersistedException extends RuntimeException {
    public EntityNotPersistedException(String message) {
        super(message);
    }
}
