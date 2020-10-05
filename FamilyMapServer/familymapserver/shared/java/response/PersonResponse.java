package response;

import model.Person;

/**
 * The response returned when a PersonService object calls the person method.
 */
public class PersonResponse extends Response {
    /**
     * The username of the user that is associated with the person.
     */
    private String associatedUsername;

    /**
     * The identifier of the person.
     */
    private String personID;

    /**
     * The person's first name.
     */
    private String firstName;

    /**
     * The person's last name.
     */
    private String lastName;

    /**
     * The person's gender.
     */
    private String gender;

    /**
     * The identifier of the person's father.
     */
    private String fatherID;

    /**
     * The identifier of the person's mother.
     */
    private String motherID;

    /**
     * The identifier of the person's spouse.
     */
    private String spouseID;

    public PersonResponse(Person person) {
        setAssociatedUsername(person.getAssociatedUsername());
        setPersonID(person.getPersonID());
        setFirstName(person.getFirstName());
        setLastName(person.getLastName());
        setGender(person.getGender());
        setFatherID(person.getFatherID());
        setMotherID(person.getMotherID());
        setSpouseID(person.getSpouseID());
    }

    public PersonResponse() {

    }

    public String getAssociatedUsername() {
        return associatedUsername;
    }

    public void setAssociatedUsername(String associatedUsername) {
        this.associatedUsername = associatedUsername;
    }

    public String getPersonID() {
        return personID;
    }

    public void setPersonID(String personID) {
        this.personID = personID;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getFatherID() {
        return fatherID;
    }

    public void setFatherID(String fatherID) {
        this.fatherID = fatherID;
    }

    public String getMotherID() {
        return motherID;
    }

    public void setMotherID(String motherID) {
        this.motherID = motherID;
    }

    public String getSpouseID() {
        return spouseID;
    }

    public void setSpouseID(String spouseID) {
        this.spouseID = spouseID;
    }
}
