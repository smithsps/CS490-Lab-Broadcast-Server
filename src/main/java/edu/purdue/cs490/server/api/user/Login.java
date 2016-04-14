package edu.purdue.cs490.server.api.user;


import com.fasterxml.jackson.databind.ObjectMapper;
import edu.purdue.cs490.server.SQLiteData;
import edu.purdue.cs490.server.Server;
import edu.purdue.cs490.server.data.HTTPRequest;
import edu.purdue.cs490.server.data.HTTPResponse;
import edu.purdue.cs490.server.data.sql.Account;
import org.mindrot.jbcrypt.BCrypt;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Login {
    private static final Logger log = Logger.getLogger(Registration.class.getName());
    private static SQLiteData sqlData = Server.getInstance().getSQLData();

    public static HTTPResponse handleLogin(HTTPRequest request) {

        //SSL is REQUIRED for all user operations. Mostly for passing the password privately.
        if (!request.isSSL()) {
            return HTTPResponse.getHTTPError(497);
        }

        HTTPResponse response = new HTTPResponse();
        ObjectMapper mapper = new ObjectMapper();

        switch (request.getMethod()) {
            case POST:
                try {
                    log.fine(request.getBody());
                    Map data = mapper.readValue(request.getBody(), Map.class);

                    String username = (String) data.get("username");
                    String password = (String) data.get("password");
                    String session;


                    Account account;
                    try {
                        account = sqlData.getAccount(username);
                    } catch (SQLException ex) {
                        System.out.println(ex.getErrorCode());
                        log.log(Level.WARNING, "Exception while trying to login user.");
                        response.setStatus(500);
                        response.setSimpleJsonMessage("error", "The username and password you entered did not match our records.");
                        return response;
                    }

                    if (BCrypt.checkpw(password, account.passwordHash) && username.equals(account.username)) {
                        session = UUID.randomUUID().toString().replace("-", "");
                    } else {
                        log.fine("Attempted login on account, " + username + " failed.");
                        response.setStatus(403);
                        response.setSimpleJsonMessage("error", "The username and password you entered did not match our records.");
                        return response;
                    }

                    try {
                        sqlData.createSession(username, session);
                    } catch (SQLException e) {
                        response.setStatus(500);
                        response.setSimpleJsonMessage("error", "Login successful, but was unable to create session.");
                        return response;
                    }

                    response.setStatus(200);
                    response.setSimpleJsonMessage("session", session);

                    return response;
                } catch (IOException e) {
                    log.log(Level.WARNING, "Error while trying to read JSON from response", e);
                    return HTTPResponse.getHTTPError(400);
                }

            default:
                return HTTPResponse.getHTTPError(405);
        }
    }
}
