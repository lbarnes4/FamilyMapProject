package handler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import dao.DataAccessException;
import service.FillService;
import response.FillResponse;

import java.io.IOException;
import java.io.OutputStream;
import java.security.InvalidParameterException;

public class FillRequestHandler extends handler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            if (exchange.getRequestMethod().toUpperCase().equals("POST")) {
                FillService service = new FillService();
                String uri = exchange.getRequestURI().toString();
                String[] uriParts = uri.split("/");
                if (uriParts.length < 3) {
                    throw new InvalidParameterException();
                }


                String username = uriParts[2];
                int generations = uriParts.length == 3 ? 4 : Integer.parseInt(uriParts[3]);
                FillResponse response = service.fill(username,generations);

                exchange.sendResponseHeaders(response.wasSuccessful() ? 200 : 400,0);
                OutputStream respBody = exchange.getResponseBody();
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
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
