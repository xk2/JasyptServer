package pl.xk2.jasypt.server.server;

import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.reflect.ClassPath;

class ControllerClassLoader {

    private final Map<Class, Object> objectStore = new HashMap<>();


    public Set<RequestHandler> createHttpRequestHandlers(String packageName) {
        Set<Class> classes = loadClassesWithGuava(packageName);

        return classes.stream()
                .filter(clazz -> clazz.isAnnotationPresent(Controller.class))
                .flatMap(controllerType -> Arrays.stream(controllerType.getMethods()))
                .filter(method -> method.isAnnotationPresent(RequestMapping.class))
                .map(method -> new RequestHandler(getObjectInstanceByType(method.getDeclaringClass()), method))
                .collect(Collectors.toSet());

    }

    //TODO fix me plz
    private Set<Class> loadClassesWithGuava(String packageName) {
        try {
            return ClassPath.from(ClassLoader.getSystemClassLoader())
                    .getAllClasses()
                    .stream()
                    .filter(clazz -> clazz.getPackageName().equalsIgnoreCase(packageName))
                    .map(clazz -> clazz.load())
                    .collect(Collectors.toSet());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    //TODO fix me plz then remove method above and guava dependency.
    private Set<Class> loadClasses2(String packageName) {
        String name = packageName.replaceAll("[.]", "/");
        InputStream stream = getClass().getClassLoader().getResourceAsStream(name);
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        return reader.lines().collect(Collectors.toSet()).stream()
                .filter(line -> line.endsWith(".class"))
                .map(line -> getClass(line, packageName))
                .collect(Collectors.toSet());

    }

    private Object getObjectInstanceByType(Class controllerType) {
        try {
            if (!objectStore.containsKey(controllerType)) {
                objectStore.put(controllerType, controllerType.getDeclaredConstructor().newInstance());
            }
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
        return objectStore.get(controllerType);
    }

    private Class getClass(String className, String packageName) {
        try {
            return Class.forName(packageName + "."
                    + className.substring(0, className.lastIndexOf('.')));
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

}
