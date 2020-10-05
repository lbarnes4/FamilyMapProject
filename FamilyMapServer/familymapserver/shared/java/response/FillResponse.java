package response;

/**
 * The response returned when a FillService object calls the fill method.
 */
public class FillResponse extends Response {
    public FillResponse(String message, boolean success) {
        this.setMessage(message);
        this.setSuccess(success);
    }

    public FillResponse() {

    }
}
