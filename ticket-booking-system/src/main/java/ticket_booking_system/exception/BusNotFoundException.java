package ticket_booking_system.exception;

public class BusNotFoundException extends RuntimeException {

    public BusNotFoundException(String message) {
        super(message);
    }
}
