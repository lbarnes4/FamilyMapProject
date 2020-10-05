package response;

/**
 * The response returned when a LoadService object calls the load method.
 */
public class LoadResponse extends Response {
    public LoadResponse(String message, boolean success) {
        this.setMessage(message);
        this.setSuccess(success);
    }

    public LoadResponse() {

    }
}
