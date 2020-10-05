package handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;

public class FileRequestHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            if (exchange.getRequestMethod().toUpperCase().equals("GET")) {
                String urlPath = exchange.getRequestURI().toString();
                if (urlPath == null || urlPath.equals("/")) {
                    urlPath = "/index.html";
                }
                String filePath = "web" + urlPath;
                File file = new File(filePath);
                if (file.exists()) {
                    exchange.sendResponseHeaders(200,0);
                    OutputStream respBody = exchange.getResponseBody();
                    Files.copy(file.toPath(), respBody);
                } else {
                    exchange.sendResponseHeaders(404,0);
                    OutputStream respBody = exchange.getResponseBody();
                    file = new File("web/HTML/404.html");
                    if (file.exists()) {
                        Files.copy(file.toPath(), respBody);
                    }
                }
            } else {
                //maybe send a 405 error (method not allowed)
                exchange.sendResponseHeaders(400, 0);
            }
        }
        catch(IOException e) {
            System.out.println(e.getMessage());
        }
        exchange.getResponseBody().close();
    }
}
