package edu.purdue.cs490.server.api.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.purdue.cs490.server.SQLiteData;
import edu.purdue.cs490.server.Server;
import edu.purdue.cs490.server.data.HTTPRequest;
import edu.purdue.cs490.server.data.HTTPResponse;
import edu.purdue.cs490.server.data.sql.Account;
import org.mindrot.jbcrypt.BCrypt;
import org.sqlite.SQLiteErrorCode;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Verify {
    private static final Logger log = Logger.getLogger(Registration.class.getName());
    private static SQLiteData sqlData = Server.getInstance().getSQLData();

    public static HTTPResponse handleVerify(HTTPRequest request) {

        //SSL is REQUIRED for all user operations. Mostly for passing the password privately.
        if (!request.isSSL()) {
            return HTTPResponse.getHTTPError(497);
        }

        HTTPResponse response = new HTTPResponse();
        ObjectMapper mapper = new ObjectMapper();

        switch (request.getMethod()) {
            case POST:
                try {
                    log.finer(request.getBody());
                    Map data = mapper.readValue(request.getBody(), Map.class);
                    String verifyCode = (String) data.get("verify");

                    // Grab username param from url or json body.
                    String username;
                    try {
                        username = request.getUri().split("/")[3];
                    } catch (ArrayIndexOutOfBoundsException ex) {
                        username = (String) data.get("username");
                    }

                    try {
                        Account account = sqlData.getAccount(username);

                        if (account.active) {
                            response.setStatus(200);
                            response.setSimpleJsonMessage("success", "Account already verified!");
                            return response;
                        }

                        if (!account.verifyCode.equals(verifyCode)) {
                            response.setStatus(403);
                            response.setSimpleJsonMessage("error", "Incorrect verification code.");
                            return response;
                        }

                        sqlData.setAccountActive(username, verifyCode, true);
                    } catch (SQLException ex) {
                        log.log(Level.WARNING, "Unexpected exception while trying to verify user.", ex);
                        response.setStatus(500);
                        response.setSimpleJsonMessage("error", "There was an unknown exception processing your verification request.");
                        return response;
                    }

                    response.setStatus(200);
                    response.setSimpleJsonMessage("success", "Account was successfully verified!");
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
