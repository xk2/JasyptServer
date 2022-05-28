module controllers {
    requires jdk.httpserver;
    requires java.logging;
    requires com.google.common;
    opens pl.xk2.jasypt.server.controllers;
}