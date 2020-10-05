package response;

import model.Person;
import java.util.List;

/**
 * The response returned when an PersonsService object calls the persons method.
 */
public class PersonsResponse extends Response {
    /**
     * The list of persons associated with a given user.
     */
    public List<Person> data;
}
