package response;

import model.Event;
import java.util.List;

/**
 * The response returned when an EventsService object calls the events method.
 */
public class EventsResponse extends Response {
    /**
     * The list of events associated with a given user.
     */
    public List<Event> data;
}
