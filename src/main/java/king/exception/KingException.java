package king.exception;

/**
 * Represents an invalid King command that can be corrected by the user.
 * The message explains the problem and how to continue the conversation.
 */
public class KingException extends Exception {

    /**
     * Creates an exception with an explanation suitable for displaying to the user.
     *
     * @param message The problem and suggested correction.
     */
    public KingException(String message) {
        super(message);
    }
}
