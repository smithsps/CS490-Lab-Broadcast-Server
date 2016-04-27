package edu.purdue.cs490.server;


import edu.purdue.cs490.server.api.user.Registration;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class Mailer {
    private static final Logger log = Logger.getLogger(Registration.class.getName());
    private static SQLiteData sqlData = Server.getInstance().getSQLData();

    private Session session;
    private String username, password;

    public Mailer() {
        username = Server.getInstance().config.get("mail.smtp.username");
        password = Server.getInstance().config.get("mail.smtp.password");

        session = Session.getInstance(Server.getInstance().config,
            new javax.mail.Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(username, password);
                }
            });

        // Attempt to connect to SMTP Server manually.
        try {
            Transport transport = session.getTransport("smtp");
            transport.connect();
            transport.close();
        } catch (AuthenticationFailedException e) {
            log.log(Level.SEVERE, "Failed authentication for SMTP Server", e);
            System.exit(0);
        } catch (MessagingException e) {
            log.log(Level.WARNING, "Unknown exception trying to connection to SMTP Server", e);
        }
    }

    /**
     * Send account verification email to username@purdue.edu
     * @param username Account username@purdue.edu
     * @param verification
     * @return if success
     */
    public Boolean registration(String username, String verification) {

        String body = "Lab Broadcast Test\n Verification: " + verification;

        return sendEmail(username + "@purdue.edu", "Welcome to Lab Broadcast", body);
    }


    private Boolean sendEmail(String to, String subject, String body){
        Message message = new MimeMessage(session);
        System.out.println(to);
        System.out.println(subject);
        System.out.println(body);
        try {
            message.setFrom(new InternetAddress(username + "@purdue.edu"));
            message.setRecipient(Message.RecipientType.TO, new InternetAddress("bshrawde@purdue.edu"));

            message.setSubject(subject);
            message.setText(body);

            Transport.send(message);
        } catch (MessagingException e) {
            log.log(Level.WARNING, "Unable to sending email.", e);
            return false;
        }
        return true;
    }
}
