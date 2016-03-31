package edu.purdue.cs490.server.api;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import edu.purdue.cs490.server.SQLiteData;
import edu.purdue.cs490.server.Server;
import edu.purdue.cs490.server.data.HTTPRequest;
import edu.purdue.cs490.server.data.HTTPResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Status {
    private static final Logger log = Logger.getLogger(Status.class.getName());
    private static SQLiteData sqlData = Server.getInstance().getSQLData();

    public static HTTPResponse handleStatus(HTTPRequest request) {
        HTTPResponse response = new HTTPResponse();
        ObjectMapper mapper = new ObjectMapper();


        switch (request.getMethod()) {
            case GET:
                Map<String, Integer> labs = new HashMap<>();

                labs.put("LWSNB160", sqlData.grabLab("LWSNB160"));
                labs.put("LWSNB158", sqlData.grabLab("LWSNB158"));
                labs.put("LWSNB148", sqlData.grabLab("LWSNB148"));
                labs.put("LWSNB146", sqlData.grabLab("LWSNB146"));
                labs.put("LWSNB131", sqlData.grabLab("LWSNB131"));
                labs.put("HAASG56", sqlData.grabLab("HAASG56"));
                labs.put("HAASG40", sqlData.grabLab("HAASG40"));
                labs.put("HAAS257", sqlData.grabLab("HAAS257"));

                ObjectWriter objWriter = mapper.writer().withDefaultPrettyPrinter();

                try {
                    response.setBody(objWriter.writeValueAsString(labs));
                    response.setStatus(200);
                } catch (JsonProcessingException e) {
                    log.log(Level.WARNING, "Error while building json response for labs", e);
                    return HTTPResponse.getError(500);
                }
                return response;
        }
        return HTTPResponse.getError(500);
    }

    public static HTTPResponse handleUpdateLinux(HTTPRequest request) {
        HTTPResponse response = new HTTPResponse();
        ObjectMapper mapper = new ObjectMapper();

        switch (request.getMethod()) {
            case PUT:
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
                        labroom = "HAASG56";
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
        }
        return HTTPResponse.getError(500);
    }
}
