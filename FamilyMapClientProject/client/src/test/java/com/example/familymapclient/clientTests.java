package com.example.familymapclient;

import org.junit.Test;

import java.util.List;

import model.Event;
import model.Person;
import model.User;
import request.LoginRequest;
import request.RegisterRequest;
import response.EventsResponse;
import response.LoginResponse;
import response.PersonsResponse;
import response.RegisterResponse;

import static org.junit.Assert.*;

/**
 * before running these tests, use localhost:8080 to clear the database and then load the data in json/loadData
 */
public class clientTests {
    @Test
    public void registerPass() {
        //register new user (lucas barnes)
        HttpClient httpClient = new HttpClient("http://localhost:8080/user/register");
        RegisterRequest request = new RegisterRequest("username","password",
                "email","firstname","lastname","m");
        RegisterResponse response = httpClient.register(request);
        assertTrue(response.wasSuccessful());
    }

    @Test
    public void duplicateRegisterFail() {
        //register lucas barnes again
        HttpClient httpClient = new HttpClient("http://localhost:8080/user/register");
        RegisterRequest request = new RegisterRequest("username","password",
                "email","firstname","lastname","m");
        RegisterResponse response = httpClient.register(request);
        assertFalse(response.wasSuccessful());
    }

    @Test
    public void loginFail() {
        //non-existent user
        HttpClient httpClient = new HttpClient("http://localhost:8080/user/login");
        LoginRequest request = new LoginRequest("nonsense","password");
        LoginResponse response = httpClient.login(request);
        assertFalse(response.wasSuccessful());
    }

    @Test
    public void loginPass() {
        //log in with sheila parker
        HttpClient httpClient = new HttpClient("http://localhost:8080/user/login");
        LoginRequest request = new LoginRequest("sheila","parker");
        LoginResponse response = httpClient.login(request);
        assertTrue(response.wasSuccessful());
        assertEquals("sheila",response.getUserName());
        assertEquals("Sheila_Parker",response.getPersonID());
    }

    @Test
    public void getPeoplePass() {
        //get sheila parker's people and make sure there are the right amount of them
        HttpClient httpClient = new HttpClient("http://localhost:8080/user/login");
        LoginRequest request = new LoginRequest("sheila","parker");
        LoginResponse loginResponse = httpClient.login(request);
        assertTrue(loginResponse.wasSuccessful());
        DataCache.getInstance().authString = loginResponse.getAuthToken();
        httpClient = new HttpClient("http://localhost:8080/person");
        PersonsResponse response = httpClient.getPersons();
        assertTrue(response.wasSuccessful());
        assertEquals(8,response.data.size());
    }

    @Test
    public void getPeopleFail() {
        //make unauthorized request and make sure it doesn't return anything
        DataCache.getInstance().authString = null;
        HttpClient httpClient = new HttpClient("http://localhost:8080/person");
        PersonsResponse response = httpClient.getPersons();
        assertNull(response);
    }

    @Test
    public void getEventPass() {
        //get sheila parker's events and make sure there are the right amount of them
        HttpClient httpClient = new HttpClient("http://localhost:8080/user/login");
        LoginRequest request = new LoginRequest("sheila","parker");
        LoginResponse loginResponse = httpClient.login(request);
        assertTrue(loginResponse.wasSuccessful());
        DataCache.getInstance().authString = loginResponse.getAuthToken();
        httpClient = new HttpClient("http://localhost:8080/event");
        EventsResponse response = httpClient.getEvents();
        assertTrue(response.wasSuccessful());
        assertEquals(16,response.data.size());
    }

    @Test
    public void getEventsFail() {
        //make unauthorized request and make sure it doesn't return anything
        DataCache.getInstance().authString = null;
        HttpClient httpClient = new HttpClient("http://localhost:8080/event");
        EventsResponse response = httpClient.getEvents();
        assertNull(response);
    }

    @Test
    public void filterMalesPass() {
        HttpClient httpClient = new HttpClient("http://localhost:8080/user/login");
        LoginRequest request = new LoginRequest("sheila","parker");
        LoginResponse loginResponse = httpClient.login(request);
        assertTrue(loginResponse.wasSuccessful());
        DataCache dataCache = DataCache.getInstance();
        dataCache.authString = loginResponse.getAuthToken();
        httpClient = new HttpClient("http://localhost:8080/person");
        PersonsResponse personsResponse = httpClient.getPersons();
        dataCache.persons = personsResponse.data;
        httpClient = new HttpClient("http://localhost:8080/event");
        EventsResponse eventsResponse = httpClient.getEvents();
        dataCache.allEvents = eventsResponse.data;
        dataCache.userPerson = dataCache.getPerson(loginResponse.getPersonID());
        dataCache.filterEvents(true,true,false,true);
        for (Event event: dataCache.events) {
            Person person = dataCache.getPerson(event.getPersonID());
            assertEquals("f",person.getGender());
        }
    }

    @Test
    public void filterFemalesPass() {
        HttpClient httpClient = new HttpClient("http://localhost:8080/user/login");
        LoginRequest request = new LoginRequest("sheila","parker");
        LoginResponse loginResponse = httpClient.login(request);
        assertTrue(loginResponse.wasSuccessful());
        DataCache dataCache = DataCache.getInstance();
        dataCache.authString = loginResponse.getAuthToken();
        httpClient = new HttpClient("http://localhost:8080/person");
        PersonsResponse personsResponse = httpClient.getPersons();
        dataCache.persons = personsResponse.data;
        httpClient = new HttpClient("http://localhost:8080/event");
        EventsResponse eventsResponse = httpClient.getEvents();
        dataCache.allEvents = eventsResponse.data;
        dataCache.userPerson = dataCache.getPerson(loginResponse.getPersonID());
        dataCache.filterEvents(true,true,true,false);
        for (Event event: dataCache.events) {
            Person person = dataCache.getPerson(event.getPersonID());
            assertEquals("m",person.getGender());
        }
    }

    @Test
    public void orderEvents() {
        HttpClient httpClient = new HttpClient("http://localhost:8080/user/login");
        LoginRequest request = new LoginRequest("sheila","parker");
        LoginResponse loginResponse = httpClient.login(request);
        assertTrue(loginResponse.wasSuccessful());
        DataCache dataCache = DataCache.getInstance();
        dataCache.authString = loginResponse.getAuthToken();
        httpClient = new HttpClient("http://localhost:8080/person");
        PersonsResponse personsResponse = httpClient.getPersons();
        dataCache.persons = personsResponse.data;
        httpClient = new HttpClient("http://localhost:8080/event");
        EventsResponse eventsResponse = httpClient.getEvents();
        dataCache.allEvents = eventsResponse.data;
        dataCache.userPerson = dataCache.getPerson(loginResponse.getPersonID());
        dataCache.filterEvents(true,true,true,true);
        List<Event> events = dataCache.getEventsOfPerson("Sheila_Parker");
        int year = 0;
        for (Event event : events) {
            assertTrue(event.getYear() >= year);
            year = event.getYear();
        }
    }
}