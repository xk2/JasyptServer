package pl.xk2.jasypt.server.server;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class Server {

    private final int port;
    private final int minThreads = 1;
    private final int maxThreads = 5;
    private final long keepAliveSeconds = 30;
    private final int requestQueueCapacity = 60;
    private final ThreadPoolExecutor executor;
    private final HttpServer server;

    private final ControllerClassLoader bootstrapper = new ControllerClassLoader();

    Logger log = Logger.getLogger(Server.class.getName());

    public Server(int port, String context, String controllerPackage) throws IOException {
        this.port = port;
        Set<RequestHandler> handlerSet = bootstrapper.createHttpRequestHandlers(controllerPackage);
        server = HttpServer.create(new InetSocketAddress(port), 0);
        executor = new ThreadPoolExecutor(minThreads, maxThreads, keepAliveSeconds, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(requestQueueCapacity));
        server.setExecutor(executor);
        server.createContext(context, new RequestDispatcher(handlerSet));
    }

    public void listen() {
        log.info("\nStarting  server... on port " + port);
        server.start();
    }

    public void stop() {
        log.info("\nClosing server... ");
        server.stop(0);
    }
}
