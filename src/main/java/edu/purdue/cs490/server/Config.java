package edu.purdue.cs490.server;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.Reader;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Config extends Properties{
    private static final Logger log = Logger.getLogger(Server.class.getName());

    static Properties defaults;
    Properties properties;

    static {
        defaults = new Properties();
        defaults.setProperty("PlainPort", "5000");
        defaults.setProperty("SSLPort", "5001");

        defaults.setProperty("DatabaseFile", "lab-broadcast.sqlite3");

        defaults.setProperty("Keystore", "keystore.jks");
        //defaults.setProperty("KeystorePassword", "");

        defaults.setProperty("WorkerPoolSize", "60");

        defaults.put("mail.transport.protocol", "smtp");
        defaults.put("mail.smtp.auth", "true");
        defaults.put("mail.smtp.starttls.enable", "true");
        defaults.put("mail.smtp.host", "smtp.purdue.edu");
        defaults.put("mail.smtp.port", "587");
        //defaults.put("mail.smtp.username", "");
        //defaults.put("mail.smtp.password", "");
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
            Integer.parseInt(properties.getProperty("PlainPort"));
        } catch (NumberFormatException ex){
            log.warning("PlainPort property invalid, defaulting..");
            properties.remove("PlainPort");
        }

        try {
            Integer.parseInt(properties.getProperty("SSLPort"));
        } catch (NumberFormatException ex){
            log.warning("SSLPort property invalid, defaulting..");
            properties.remove("SSLPort");
        }

        try {
            Integer.parseInt(properties.getProperty("WorkerPoolSize"));
        } catch (NumberFormatException ex){
            log.warning("WorkerPoolSize property invalid, defaulting..");
            properties.remove("WorkerPoolSize");
        }
    }

    private void required() {
        if (!properties.containsKey("KeystorePassword")) {
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
