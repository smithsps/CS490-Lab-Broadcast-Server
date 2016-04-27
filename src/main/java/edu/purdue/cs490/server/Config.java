package edu.purdue.cs490.server;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.Reader;
import java.util.Collections;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Config extends Properties{
    private static final Logger log = Logger.getLogger(Server.class.getName());

    static Properties defaults;
    Properties properties;

    static {
        defaults = new Properties();
        defaults.setProperty("port.plain", "5000");
        defaults.setProperty("port.ssl", "5001");

        defaults.setProperty("file.database", "lab-broadcast.sqlite3");

        defaults.setProperty("file.keystore", "keystore.jks");
        //defaults.setProperty("KeystorePassword", "");

        defaults.setProperty("size.workerpool", "60");

        defaults.put("mail.transport.protocol", "smtp");
        defaults.put("mail.smtp.auth", "true");
        defaults.put("mail.smtp.starttls.enable", "true");
        defaults.put("mail.smtp.host", "smtp.purdue.edu");
        defaults.put("mail.smtp.port", "587");
        //defaults.put("mail.smtp.username", "");
        //defaults.put("mail.smtp.password", "");

        defaults.put("logging.level", Level.INFO);
        defaults.put("file.logging", "server.log");
    }

    public Config (String filename) {
        properties = new Properties(defaults);

        try (Reader r = new FileReader(filename)) {
            properties.load(r);
        } catch (Exception ex) {
            log.log(Level.WARNING, "No config file found, proceeding with defaults.");
        }
        required();
        validate();

        Collections.list(properties.propertyNames()).forEach(p -> System.out.println(p + ":" + properties.getProperty((String) p)));
    }

    public String get(String key) {
        return properties.getProperty(key);
    }

    @Override
    public String getProperty(String key) {
        return properties.getProperty(key);
    }

    /**
     * Validate config values and remove them so the defaults are used instead.
     */
    private void validate() {
        try {
            Integer.parseInt(properties.getProperty("port.plain"));
        } catch (NumberFormatException ex){
            log.warning("port.plain property invalid, defaulting..");
            properties.remove("port.plain");
        }

        try {
            Integer.parseInt(properties.getProperty("port.ssl"));
        } catch (NumberFormatException ex){
            log.warning("port.ssl property invalid, defaulting..");
            properties.remove("port.ssl");
        }

        try {
            Integer.parseInt(properties.getProperty("size.workerpool"));
        } catch (NumberFormatException ex){
            log.warning("size.workerpool property invalid, defaulting..");
            properties.remove("size.workerpool");
        }
    }

    private void required() {
        if (!properties.containsKey("password.keystore")) {
            log.severe("KeystorePassword is required in config.properties");
            System.exit(0);
        }
        if (!properties.containsKey("mail.smtp.username")) {
            log.severe("mail.smtp.username is required in config.properties");
            System.exit(0);
        }
        if (!properties.containsKey("mail.smtp.password")) {
            log.severe("mail.smtp.password is required in config.properties");
            System.exit(0);
        }
    }
}
