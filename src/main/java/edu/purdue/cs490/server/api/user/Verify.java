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

        String code = request.getUri().split("/")[3];

        System.out.println("Recieved: " + code);

        switch (request.getMethod()) {
            case PUT:
            case POST:
                try {
                    log.fine(request.getBody());
                    Map data = mapper.readValue(request.getBody(), Map.class);


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
