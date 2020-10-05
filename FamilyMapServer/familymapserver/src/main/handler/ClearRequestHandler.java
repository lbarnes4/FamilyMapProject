package handler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import dao.DataAccessException;
import service.ClearService;
import response.ClearResponse;

import java.io.*;

public class ClearRequestHandler extends handler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            if (exchange.getRequestMethod().toUpperCase().equals("POST")) {
                ClearService service = new ClearService();
                ClearResponse response = service.clear();

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
