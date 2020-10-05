package model;

import response.PersonResponse;

/**
 * An object representing any person including the user and all their relatives.
 */
public class Person {
    public Person(String personID,String associatedUsername,String firstName,String lastName,
                  String gender,String fatherID,String motherID,String spouseID) {
        this.personID = personID;
        this.associatedUsername = associatedUsername;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.fatherID = fatherID;
        this.motherID = motherID;
        this.spouseID = spouseID;
    }

    public Person(PersonResponse response) {
        this.personID = response.getPersonID();
        this.associatedUsername = response.getAssociatedUsername();
        this.firstName = response.getFirstName();
        this.lastName = response.getLastName();
        this.gender = response.getGender();
        this.fatherID = response.getFatherID();
        this.motherID = response.getMotherID();
        this.spouseID = response.getSpouseID();
    }
    /**
     * The unique identifier of the person.
     */
    private String personID;

    /**
     * The username of the user to which is associated.
     */
    private String associatedUsername;

    /**
     * The first name of the person.
     */
    private String firstName;

    /**
     * The last name of the person.
     */
    private String lastName;

    /**
     * The gender of the person.
     */
    private String gender;

    /**
     * The identifier of the father of the person.
     */
    private String fatherID;

    /**
     * The identifier of the mother of the person.
     */
    private String motherID;

    /**
     * The identifier of the spouse of the person.
     */
    private String spouseID;

    public Person() {

    }

    public String getPersonID() {
        return personID;
    }

    public void setPersonID(String personID) {
        this.personID = personID;
    }

    public String getAssociatedUsername() {
        return associatedUsername;
    }

    public void setAssociatedUsername(String associatedUsername) {
        this.associatedUsername = associatedUsername;
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
        spouseID = spouseID;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null)
            return false;
        if (o instanceof Person) {
            Person oPerson = (Person) o;
            return oPerson.getPersonID().equals(getPersonID()) &&
                    oPerson.getAssociatedUsername().equals(getAssociatedUsername()) &&
                    oPerson.getFirstName().equals(getFirstName()) &&
                    oPerson.getLastName().equals(getLastName()) &&
                    oPerson.getGender().equals(getGender()) &&
                    (
                        (oPerson.getFatherID() == null && getFatherID() == null) ||
                        (
                            oPerson.getFatherID() != null && getFatherID() != null &&
                            oPerson.getFatherID().equals(getFatherID())
                        )
                    ) &&
                    (
                        (oPerson.getMotherID() == null && getMotherID() == null) ||
                        (
                            oPerson.getMotherID() != null && getMotherID() != null &&
                            oPerson.getMotherID().equals(getMotherID())
                        )
                    ) &&
                    (
                        (oPerson.getSpouseID() == null && getSpouseID() == null) ||
                        (
                            oPerson.getSpouseID() != null && getSpouseID() != null &&
                            oPerson.getSpouseID().equals(getSpouseID())
                        )
                    );
        } else {
            return false;
        }
    }
}
