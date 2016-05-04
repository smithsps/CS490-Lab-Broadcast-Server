package edu.purdue.cs490.server.api.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.purdue.cs490.server.SQLiteData;
import edu.purdue.cs490.server.Server;
import edu.purdue.cs490.server.data.HTTPRequest;
import edu.purdue.cs490.server.data.HTTPResponse;

import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Windows {
    private static final Logger log = Logger.getLogger(Status.class.getName());
    private static SQLiteData sqlData = Server.getInstance().getSQLData();

    public static HTTPResponse handleUpdateWindows(HTTPRequest request) {
        HTTPResponse response = new HTTPResponse();
        ObjectMapper mapper = new ObjectMapper();

        switch (request.getMethod()) {
            case PUT:
            case POST:
                try {
                    log.fine(request.getBody());
                    Map data = mapper.readValue(request.getBody(), Map.class);

                    String user = "";
                    String name;
                    int time;

                    try {
                        name = (String) data.get("name");
                        time = (int) data.get("time");
                    } catch (Exception e) {
                        log.log(Level.WARNING, "Invalid windows status update.", e);
                        return response.getHTTPError(400);
                    }

                    // User is not required.
                    try {
                        user = (String) data.get("user");
                    }   catch (Exception e) {
                        log.log(Level.FINER, "Windows update had no user.", e);
                    }

                    sqlData.updateWindows(name, user, time);

                    // 200 = Success, and since we are always successful we always success.
                    // In the future we can parse the body and validate it.
                    response.setStatus(200);
                } catch (IOException e) {
                    log.log(Level.WARNING, "Error while trying to map JSON from response", e);
                }
                return response;
            default:
                return HTTPResponse.getHTTPError(405);
        }
    }
}
