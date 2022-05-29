package pl.xk2.jasypt.server.server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static java.util.function.Function.identity;

class RequestDispatcher implements HttpHandler {

    private final Logger log = Logger.getLogger(this.getClass().getName());

    private final Map<HttpMapping, RequestHandler> mappings;

    public RequestDispatcher(Set<RequestHandler> mappings) {
        this.mappings = mappings.stream()
                .collect(Collectors.toMap(m -> m.getMapping(), identity()));
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        Map<String, String> queryParameters = getQueryMap(exchange);
        String requestBody = getBody(exchange);
        String method = exchange.getRequestMethod();
        Object response = "404!";
        int code = 404;
        RequestHandler handler = mappings.get(new HttpMapping(path, method));

        if (handler != null) {
            try {
                response = handler.invoke(queryParameters, requestBody);
                code = 200;
            } catch (Exception ex) {
                log.log(Level.WARNING,  "Request handling exception", ex);
                code = 500;
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                ex.printStackTrace(pw);
                response = sw.toString();
            }
        }
        if (response != null) {
            exchange.sendResponseHeaders(code, response.toString().length());
            OutputStream os = exchange.getResponseBody();
            os.write(response.toString().getBytes());
            os.close();
        } else {
            exchange.sendResponseHeaders(code, -1);
        }

    }

    private Map<String, String> getQueryMap(HttpExchange exchange) {
        return Optional.ofNullable(exchange)
                .map(HttpExchange::getRequestURI)
                .map(URI::getQuery)
                .stream()
                .flatMap(query -> Arrays.stream(query.split("&")))
                .collect(Collectors.toMap(param -> param.split("=")[0], param -> param.split("=")[1]));

    }

    public String getBody(HttpExchange exchange) throws IOException {
        return new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
    }
}
