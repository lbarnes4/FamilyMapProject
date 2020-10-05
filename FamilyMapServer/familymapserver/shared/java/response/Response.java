package response;

/**
 * A super class containing basic elements of all response classes.
 */
public class Response {

    /**
     * A boolean describing whether or not a service was successful.
     */
    private boolean success;

    /**
     * A message for the user.
     */
    private String message;

    public boolean wasSuccessful() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
