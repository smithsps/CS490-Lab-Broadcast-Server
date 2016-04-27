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

                    String machine = (String) data.get("name");
                    int occupied = (Boolean) data.get("occupied") ? 1 : 0;

                    //There is probably a better way to do this, but its fine for now.
                    String labroom = "";
                    if (machine.contains("moore")) {
                        labroom = "LWSNB146";
                    } else if (machine.contains("sslab")) {
                        labroom = "LWSNB158";
                    } else if (machine.contains("pod")) {
                        labroom = "LWSNB148";
                    } else if (machine.contains("borg")) {
                        labroom = "HAASG40";
                    } else if (machine.contains("xinu")) {
                        labroom = "HAAS257";
                    }

                    sqlData.updateLabPC(labroom, machine, occupied);

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
