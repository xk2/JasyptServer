package pl.xk2.jasypt.server.server;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Map;
import java.util.function.Function;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

class RequestHandler {

    private final Object invocationContext;
    private final Method methodToInvoke;
    private final HttpMapping mapping;

    private final Map<Integer, String> queryBindParameters;
    private final Map<Integer, String> bodyBindParameters;

    private final Logger log = Logger.getLogger(this.getClass().getName());

    public RequestHandler(Object invocationContext, Method methodToInvoke) {
        this.invocationContext = invocationContext;
        this.methodToInvoke = methodToInvoke;
        RequestMapping requestMapping = methodToInvoke.getAnnotation(RequestMapping.class);
        Controller controller = invocationContext.getClass().getAnnotation(Controller.class);
        mapping = buildHttpMapping(controller, requestMapping);
        log.info("Found new RequestHandler for mapping" + mapping);
        queryBindParameters = getValueOfAnnotatedParameters(methodToInvoke, RequestQuery.class, RequestQuery::value);
        bodyBindParameters = getValueOfAnnotatedParameters(methodToInvoke, RequestBody.class, RequestBody::value);

    }

    private <T extends Annotation> Map<Integer, String> getValueOfAnnotatedParameters(Method method, Class<T> clazz, Function<T, String> value) {
        if (method.getParameterCount() == 0)
            return Collections.emptyMap();

        return IntStream.range(0, method.getParameterCount())
                .mapToObj(i -> new Pair<>(i, method.getParameters()[i]))
                .filter(pair -> pair.value().isAnnotationPresent(clazz))
                .map(pair -> new Pair(pair.key(), pair.value().getAnnotation(clazz)))
                .map(pair -> new Pair(pair.key(), value.apply((T) pair.value())))
                .collect(Collectors.toMap(p -> p.key(), p -> p.value().toString()));
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

    public Object invoke(Map<String, String> queryParameter, String body) throws InvocationTargetException, IllegalAccessException {
        Object[] parameters = prepareParameters(queryParameter, body);
        return methodToInvoke.invoke(invocationContext, parameters);
    }

    private Object[] prepareParameters(Map<String, String> queryParameter, String body) {
        if (methodToInvoke.getParameterCount() == 0) {
            return null;
        }

        Object[] parameters = new Object[methodToInvoke.getParameterCount()];

        bodyBindParameters.keySet().forEach(index -> parameters[index] = body);
        queryBindParameters.entrySet().forEach(e -> parameters[e.getKey()] = queryParameter.get(e.getValue()));

        return parameters;
    }

    record Pair<T>(int key, T value) { }
}
