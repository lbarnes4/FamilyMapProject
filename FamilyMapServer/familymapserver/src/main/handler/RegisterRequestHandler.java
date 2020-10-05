package handler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import dao.DataAccessException;
import service.RegisterService;
import request.RegisterRequest;
import response.RegisterResponse;
import java.io.*;

public class RegisterRequestHandler extends handler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            if (exchange.getRequestMethod().toUpperCase().equals("POST")) {
                InputStream requestBody = exchange.getRequestBody();
                InputStreamReader reader = new InputStreamReader(requestBody);
                Gson gson = new Gson();
                RegisterRequest request = gson.fromJson(reader, RegisterRequest.class);
                RegisterService service = new RegisterService();
                RegisterResponse response = service.register(request);


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
