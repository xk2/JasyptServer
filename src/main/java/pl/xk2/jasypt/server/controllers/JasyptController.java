package pl.xk2.jasypt.server.controllers;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import pl.xk2.jasypt.server.server.Controller;
import pl.xk2.jasypt.server.server.RequestMapping;
import pl.xk2.jasypt.server.server.RequestQuery;

import static pl.xk2.jasypt.server.server.RequestMethod.GET;

@Controller
public class JasyptController {

    @RequestMapping(path = "/decrypt", method = GET)
    public String decrypt(@RequestQuery("value") String value, @RequestQuery("key") String key) {
        return getTextEncryptor(key).decrypt(value);
    }

    @RequestMapping(path = "/encrypt", method = GET)
    public String encrypt(@RequestQuery("value") String value, @RequestQuery("key") String key) {
        return getTextEncryptor(key).encrypt(value);
    }

    private StandardPBEStringEncryptor getTextEncryptor(String password) {
        StandardPBEStringEncryptor textEncryptor = new StandardPBEStringEncryptor();
        textEncryptor.setAlgorithm("PBEWithMD5AndDES");
        textEncryptor.setKeyObtentionIterations(1000);
        textEncryptor.setStringOutputType("base64");
        textEncryptor.setPassword(password);
        return  textEncryptor;
    }
}
