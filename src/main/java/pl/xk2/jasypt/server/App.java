package pl.xk2.jasypt.server;

import pl.xk2.jasypt.server.server.Server;

import java.io.IOException;

public class App {
    public static void main(String[] args) throws IOException {
        Server httpServer = new Server(8080, "/", "pl.xk2.jasypt.server.controllers");
        Runtime.getRuntime().addShutdownHook(new Thread(httpServer::stop));
        httpServer.listen();
    }
}