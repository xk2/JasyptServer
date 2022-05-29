package pl.xk2.jasypt.server.controllers;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import pl.xk2.jasypt.server.server.Controller;
import pl.xk2.jasypt.server.server.RequestMapping;
import pl.xk2.jasypt.server.server.RequestQuery;

import java.util.logging.Logger;

import static pl.xk2.jasypt.server.server.RequestMethod.GET;

@Controller
public class JasyptController {

    private final Logger log = Logger.getLogger(this.getClass().getName());

    @RequestMapping(path = "/decrypt", method = GET)
    public String decrypt(@RequestQuery("value") String value, @RequestQuery("key") String key) {
        StandardPBEStringEncryptor textEncryptor = new StandardPBEStringEncryptor();
        textEncryptor.setAlgorithm("PBEWithMD5AndDES");
        textEncryptor.setPassword(key);
        textEncryptor.setKeyObtentionIterations(1000);
        textEncryptor.setStringOutputType("base64");
        return textEncryptor.decrypt(value);
    }

    @RequestMapping(path = "/encrypt", method = GET)
    public String encrypt(@RequestQuery("value") String value, @RequestQuery("key") String key) {
        StandardPBEStringEncryptor textEncryptor = new StandardPBEStringEncryptor();
        textEncryptor.setAlgorithm("PBEWithMD5AndDES");
        textEncryptor.setPassword(key);
        textEncryptor.setKeyObtentionIterations(1000);
        textEncryptor.setStringOutputType("base64");
        return textEncryptor.encrypt(value);
    }
}
