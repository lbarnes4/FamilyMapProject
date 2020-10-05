package service;

import com.sun.net.httpserver.HttpServer;
import handler.*;
import java.io.IOException;
import java.net.InetSocketAddress;
import static java.lang.Integer.parseInt;

public class FamilyMapServer {

    public static void main(String[] args) throws  IOException{
        FamilyMapServer familyMapServer = new FamilyMapServer();
        familyMapServer.startServer(parseInt(args[0]));
    }

    private void startServer(int port) throws IOException {
        InetSocketAddress serverAddress = new InetSocketAddress(port);
        HttpServer server = HttpServer.create(serverAddress, 10);
        server.setExecutor(null);
        registerHandlers(server);

        server.start();
        System.out.println("FamilyMapServer listening on port " + port);
    }

    private void registerHandlers(HttpServer server) {
        server.createContext("/", new FileRequestHandler());
        server.createContext("/user/register", new RegisterRequestHandler());
        server.createContext("/user/login", new LoginRequestHandler());
        server.createContext("/clear", new ClearRequestHandler());
        server.createContext("/fill/", new FillRequestHandler());
        server.createContext("/load", new LoadRequestHandler());
        server.createContext("/person", new PersonRequestHandler());
        server.createContext("/event", new EventRequestHandler());
    }
}
