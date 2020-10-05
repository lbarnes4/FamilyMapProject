package handler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import dao.DataAccessException;
import service.PersonService;
import service.PersonsService;
import response.Response;
import java.io.IOException;
import java.io.OutputStream;

public class PersonRequestHandler extends handler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            if (exchange.getRequestMethod().toUpperCase().equals("GET")) {
                Response response;
                String uri = exchange.getRequestURI().toString();
                String[] uriParts = uri.split("/");
                Headers reqHeaders = exchange.getRequestHeaders();
                if (reqHeaders.containsKey("Authorization")) {
                    // Extract the auth token from the "Authorization" header
                    String authToken = reqHeaders.getFirst("Authorization");
                    if (uriParts.length < 3) {
                        PersonsService personsService = new PersonsService();
                        response = personsService.persons(authToken);
                    } else {
                        PersonService personService = new PersonService();
                        String personID = uriParts[2];
                        response = personService.person(authToken, personID);
                    }
                    exchange.sendResponseHeaders(response.wasSuccessful() ? 200 : 400, 0);
                    OutputStream respBody = exchange.getResponseBody();
                    Gson gson;
                    gson = new GsonBuilder().setPrettyPrinting().create();
                    String respData = gson.toJson(response);
                    writeString(respData, respBody);
                } else {
                    throw new AuthorizationException("Error: unauthorized request");
                }
            } else {
                exchange.sendResponseHeaders(400, 0);
            }
        }
        catch(IOException | DataAccessException | AuthorizationException e) {
            exchange.sendResponseHeaders(400,0);
            System.out.println(e.getMessage());
        }
        exchange.getResponseBody().close();
    }
}
