package pl.xk2.jasypt.server.controllers;

import pl.xk2.jasypt.server.server.Controller;
import pl.xk2.jasypt.server.server.RequestMapping;

import java.util.logging.Logger;

import static pl.xk2.jasypt.server.server.RequestMethod.GET;

@Controller
public class JasyptController {

    private final Logger log = Logger.getLogger(this.getClass().getName());

    @RequestMapping(path = "/decrypt", method = GET)
    public String decrypt() {
        return "decrypt";
    }

    @RequestMapping(path = "/encrypt", method = GET)
    public String encrypt() {
        return "encrypt!";
    }
}
