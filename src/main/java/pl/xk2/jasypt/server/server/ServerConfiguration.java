package pl.xk2.jasypt.server.server;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ServerConfiguration {
    private final int port;
    private final int minThreads;
    private final int maxThreads;
    private final long keepAliveSeconds;
    private final int requestQueueCapacity;
    private final String context;
    private final ThreadPoolExecutor executor;


    public int getPort() {
        return port;
    }

    public int getMinThreads() {
        return minThreads;
    }


    public int getMaxThreads() {
        return maxThreads;
    }

    public long getKeepAliveSeconds() {
        return keepAliveSeconds;
    }

    public int getRequestQueueCapacity() {
        return requestQueueCapacity;
    }

    public String getContextPath() {
        return context;
    }

    public ThreadPoolExecutor getExecutor() {
        return executor;
    }

    public ServerConfiguration(int port, int minThreads, int maxThreads, long keepAliveSeconds, int requestQueueCapacity, String contextPath) {
        this.port = port;
        this.minThreads = minThreads;
        this.maxThreads = maxThreads;
        this.keepAliveSeconds = keepAliveSeconds;
        this.requestQueueCapacity = requestQueueCapacity;
        this.context = contextPath;
        this.executor = new ThreadPoolExecutor(minThreads, maxThreads, keepAliveSeconds, TimeUnit.SECONDS, new ArrayBlockingQueue<>(requestQueueCapacity));
    }

    public static ServerConfigurationBuilder builder() {
        return new ServerConfigurationBuilder();
    }

    public static class ServerConfigurationBuilder {
        private int port = 8080;
        private int minThreads = 1;
        private int maxThreads = 5;
        private long keepAliveSeconds = 60;
        private int requestQueueCapacity = 30;

        private String contextPath = "/";

        public ServerConfigurationBuilder setPort(int port) {
            this.port = port;
            return this;
        }

        public ServerConfigurationBuilder setMinThreads(int minThreads) {
            this.minThreads = minThreads;
            return this;
        }

        public ServerConfigurationBuilder setMaxThreads(int maxThreads) {
            this.maxThreads = maxThreads;
            return this;
        }

        public ServerConfigurationBuilder setKeepAliveSeconds(long keepAliveSeconds) {
            this.keepAliveSeconds = keepAliveSeconds;
            return this;
        }

        public ServerConfigurationBuilder setRequestQueueCapacity(int requestQueueCapacity) {
            this.requestQueueCapacity = requestQueueCapacity;
            return this;
        }

        public ServerConfigurationBuilder setContextPath(String contextPath) {
            this.contextPath = contextPath;
            return this;
        }

        public ServerConfiguration build() {
            return new ServerConfiguration(port, minThreads, maxThreads, keepAliveSeconds, requestQueueCapacity, contextPath);
        }
    }
}