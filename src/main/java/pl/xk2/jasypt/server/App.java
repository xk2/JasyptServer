package pl.xk2.jasypt.server;

import pl.xk2.jasypt.server.server.Server;
import pl.xk2.jasypt.server.server.ServerConfiguration;

import java.io.IOException;

public class App {
    public static void main(String[] args) throws IOException {
        ServerConfiguration configuration = ServerConfiguration.builder().build();
        Server httpServer = new Server(configuration, "pl.xk2.jasypt.server.controllers");
        Runtime.getRuntime().addShutdownHook(new Thread(httpServer::stop));
        httpServer.listen();
    }
}