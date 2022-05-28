package pl.xk2.jasypt.server.server;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Logger;

class RequestHandler {

    private final Object invocationContext;
    private final Method handler;
    private final HttpMapping mapping;

    private final Logger log = Logger.getLogger(this.getClass().getName());

    public RequestHandler(Object invocationContext, Method handler) {
        this.invocationContext = invocationContext;
        this.handler = handler;
        RequestMapping requestMapping = handler.getAnnotation(RequestMapping.class);
        Controller controller = invocationContext.getClass().getAnnotation(Controller.class);
        mapping = buildHttpMapping(controller, requestMapping);
        log.info("Found new RequestHandler for mapping" + mapping);

    }

    private HttpMapping buildHttpMapping(Controller controller, RequestMapping requestMapping) {
        String method = requestMapping.method().toString();
        String path = controller.value() + "/" + requestMapping.path();
        path = path.replaceAll("/+", "/");
        return new HttpMapping(path, method);
    }

    public HttpMapping getMapping() {
        return mapping;
    }

    public Object invoke() throws InvocationTargetException, IllegalAccessException {
        return handler.invoke(invocationContext);
    }
}

