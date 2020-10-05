package dao;

import model.Person;

import java.sql.*;
import java.util.ArrayList;

/**
 * Provides an interface to the Person database table.
 */
public class PersonDao {
    private final Connection conn;

    public PersonDao(Connection conn)
    {
        this.conn = conn;
    }

    /**
     * Insert a Person object into the database.
     * @param person - the Person to be inserted.
     */
    public void insert(Person person) throws DataAccessException, IllegalArgumentException {
        if (person == null) {
            throw new IllegalArgumentException();
        }
        //We can structure our string to be similar to a sql command, but if we insert question
        //marks we can change them later with help from the statement
        String sql = "INSERT INTO Persons (personID,username,firstName,lastName, " +
                "gender,fatherID,motherID,spouseID) VALUES(?,?,?,?,?,?,?,?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            //Using the statements built-in set(type) functions we can pick the question mark we want
            //to fill in and give it a proper value. The first argument corresponds to the first
            //question mark found in our sql String
            stmt.setString(1, person.getPersonID());
            stmt.setString(2, person.getAssociatedUsername());
            stmt.setString(3, person.getFirstName());
            stmt.setString(4, person.getLastName());
            stmt.setString(5, person.getGender());
            stmt.setString(6, person.getFatherID());
            stmt.setString(7, person.getMotherID());
            stmt.setString(8, person.getSpouseID());

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error encountered while inserting Person into the database");
        }
    }

    /**
     * Delete all Persons from the database.
     */
    public void deleteAll() throws DataAccessException
    {
        try (Statement stmt = conn.createStatement()){
            String sql = "DELETE FROM Persons";
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            throw new DataAccessException("SQL Error encountered while clearing Persons");
        }
    }

    /**
     * Delete all Persons belonging to one User from the database.
     * @param userName - the userName of the User whose Persons are to be deleted.
     */
    public void deletePersonsOfUser(String userName) throws DataAccessException {
        String sql = "DELETE FROM Persons WHERE UserName = ?;";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, userName);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error encountered while deleting persons of user");
        }
    }

    /**
     * Get one Person from the database.
     * @param personID - the personID of the Person to be returned.
     * @return the Person object.
     */
    public Person getPerson(String personID) throws DataAccessException {
        Person person;
        ResultSet rs = null;
        String sql = "SELECT * FROM Persons WHERE PersonID = ?;";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, personID);
            rs = stmt.executeQuery();
            if (rs.next()) {
                person = new Person(rs.getString("PersonID"), rs.getString("UserName"),
                        rs.getString("FirstName"), rs.getString("LastName"),
                        rs.getString("Gender"), rs.getString("FatherID"),
                        rs.getString("MotherID"), rs.getString("SpouseID"));
                return person;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error encountered while finding person");
        } finally {
            if(rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

        }
        return null;
    }
    /**
     * Get all Persons belonging to one User from the database.
     * @param userName - the userName of the User whose Persons are to be returned.
     * @return a list of Persons.
     */
    public ArrayList<Person> getPersonsOfUser(String userName) throws DataAccessException {
        ArrayList<Person> persons = new ArrayList<>();
        Person person;
        ResultSet rs = null;
        String sql = "SELECT * FROM Persons WHERE UserName = ?;";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, userName);
            rs = stmt.executeQuery();
            while (rs.next()) {
                person = new Person(rs.getString("PersonID"), rs.getString("UserName"),
                        rs.getString("FirstName"), rs.getString("LastName"),
                        rs.getString("Gender"), rs.getString("FatherID"),
                        rs.getString("MotherID"), rs.getString("SpouseID"));
                persons.add(person);
            }
            return persons;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error encountered while finding persons");
        } finally {
            if(rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
