package handler;

public class AuthorizationException extends Throwable {
    public AuthorizationException(String message)
    {
        super(message);
    }

    public AuthorizationException()
    {
        super();
    }
}
