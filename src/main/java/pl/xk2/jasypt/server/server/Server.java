package pl.xk2.jasypt.server.server;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Set;
import java.util.logging.Logger;

public class Server {

    private final HttpServer server;

    private final ControllerClassLoader bootstrapper = new ControllerClassLoader();
    private final ServerConfiguration serverConfiguration;

    Logger log = Logger.getLogger(Server.class.getName());

    public Server(ServerConfiguration configuration, String controllerPackage) throws IOException {
        Set<RequestHandler> handlerSet = bootstrapper.createHttpRequestHandlers(controllerPackage);
        this.serverConfiguration = configuration;
        server = HttpServer.create(new InetSocketAddress(configuration.getPort()), 0);
        server.setExecutor(serverConfiguration.getExecutor());
        server.createContext(configuration.getContextPath(), new RequestDispatcher(handlerSet));
    }

    public void listen() {
        log.info("\nStarting server... on port " + serverConfiguration.getPort());
        server.start();
    }

    public void stop() {
        log.info("\nClosing server... ");
        server.stop(0);
    }
}
