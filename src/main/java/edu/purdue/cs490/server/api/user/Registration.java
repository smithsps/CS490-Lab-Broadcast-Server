package edu.purdue.cs490.server.api.user;


import com.fasterxml.jackson.databind.ObjectMapper;
import edu.purdue.cs490.server.SQLiteData;
import edu.purdue.cs490.server.Server;
import edu.purdue.cs490.server.data.HTTPRequest;
import edu.purdue.cs490.server.data.HTTPResponse;
import org.mindrot.jbcrypt.BCrypt;
import org.sqlite.SQLiteErrorCode;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Registration {
    private static final Logger log = Logger.getLogger(Registration.class.getName());
    private static SQLiteData sqlData = Server.getInstance().getSQLData();

    public static HTTPResponse handleRegistration(HTTPRequest request) {

        //SSL is REQUIRED for all user operations. Mostly for passing the password privately.
        if (!request.isSSL()) {
            return HTTPResponse.getHTTPError(497);
        }

        HTTPResponse response = new HTTPResponse();
        ObjectMapper mapper = new ObjectMapper();

        switch (request.getMethod()) {
            case PUT:
            case POST:
                try {
                    log.fine(request.getBody());
                    Map data = mapper.readValue(request.getBody(), Map.class);

                    String username = (String) data.get("username");
                    String password = (String) data.get("password");
                    String hashedPassword, verify;

                    // Default of genSalt is 10 rounds.
                    hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

                    // Generate Verification Code
                    verify = UUID.randomUUID().toString().substring(0, 6);

                    // I really couldn't decide on how best to implement this.
                    // But I wanted custom error messages like:
                    // "This user account is already registered."
                    // So if we handle the sql errors here, it kinda works?
                    try {
                        sqlData.createAccount(username, hashedPassword, verify);
                    } catch (SQLException ex) {
                        System.out.println(ex.getErrorCode());
                        switch (SQLiteErrorCode.getErrorCode(ex.getErrorCode())) {
                            case SQLITE_CONSTRAINT:
                                log.fine(username + " was attempted to be registered but is already in use.");
                                response.setStatus(403);
                                response.setJsonMessage("error", "Username is already registered.");
                                return response;
                            default:
                                log.log(Level.WARNING, "Exception while trying to register user.", ex);
                                response.setStatus(500);
                                response.setJsonMessage("error", "There was an unknown exception processing your request.");
                                return response;
                        }
                    }

                    response.setStatus(200);
                    response.setJsonMessage("success", "Account was successfully created, a verification code would be sent to " +
                                                       "registered account email. ");
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
