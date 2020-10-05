package handler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import dao.DataAccessException;
import service.LoginService;
import request.LoginRequest;
import response.LoginResponse;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

public class LoginRequestHandler extends handler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            if (exchange.getRequestMethod().toUpperCase().equals("POST")) {
                InputStream requestBody = exchange.getRequestBody();
                InputStreamReader reader = new InputStreamReader(requestBody);
                Gson gson = new Gson();
                LoginRequest request = gson.fromJson(reader, LoginRequest.class);
                LoginService service = new LoginService();
                LoginResponse response = service.login(request);


                exchange.sendResponseHeaders(response.wasSuccessful() ? 200 : 400,0);
                OutputStream respBody = exchange.getResponseBody();

                gson = new GsonBuilder().setPrettyPrinting().create();
                String respData = gson.toJson(response);

                writeString(respData,respBody);
            } else {
                exchange.sendResponseHeaders(400, 0);
            }
        }
        catch(IOException | DataAccessException e) {
            exchange.sendResponseHeaders(400,0);
            System.out.println(e.getMessage());
        }
        exchange.getResponseBody().close();
    }
}
