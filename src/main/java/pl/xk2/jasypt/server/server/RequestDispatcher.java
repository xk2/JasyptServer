package pl.xk2.jasypt.server.server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.function.Function.identity;

class RequestDispatcher implements HttpHandler {

    private final Map<HttpMapping, RequestHandler> mappings;

    public RequestDispatcher(Set<RequestHandler> mappings) {
        this.mappings = mappings.stream()
                .collect(Collectors.toMap(m -> m.getMapping(), identity()));
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String uri = String.valueOf(exchange.getRequestURI());
        String method = exchange.getRequestMethod();
        InputStream is = exchange.getRequestBody();
        String response = "404!";
        int code = 404;
        RequestHandler handler = mappings.get(new HttpMapping(uri, method));
        if (handler != null) {
            try {
                response = handler.invoke().toString();
                code = 200;
            } catch (InvocationTargetException | IllegalAccessException | IllegalArgumentException e) {
                code = 500;
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                e.printStackTrace(pw);
                response = sw.toString();
            }
        }


        exchange.sendResponseHeaders(code, response.length());
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }
}
