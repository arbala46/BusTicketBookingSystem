package ticket_booking_system.exception;

public class TripNotFoundException extends RuntimeException {
    public TripNotFoundException(String message) {

        super(message);
    }
}
