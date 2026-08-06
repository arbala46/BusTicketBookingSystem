package ticket_booking_system.exception;

public class UserAlreadyExistsException extends RuntimeException {

        public UserAlreadyExistsException(String message)
        {
            super(message);
        }

}
