package edu.purdue.cs490.server.api.status;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import edu.purdue.cs490.server.SQLiteData;
import edu.purdue.cs490.server.Server;
import edu.purdue.cs490.server.data.HTTPRequest;
import edu.purdue.cs490.server.data.HTTPResponse;

import java.io.IOException;
import java.sql.SQLException;
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

                try {
                    labs.put("LWSNB160", sqlData.gradWindowsLab("LWSNB160"));
                    labs.put("LWSNB158", sqlData.grabLinuxLab("LWSNB158"));
                    labs.put("LWSNB148", sqlData.grabLinuxLab("LWSNB148"));
                    labs.put("LWSNB146", sqlData.grabLinuxLab("LWSNB146"));
                    labs.put("LWSNB131", sqlData.gradWindowsLab("LWSNB131"));
                    labs.put("HAASG56", sqlData.gradWindowsLab("HAASG56"));
                    labs.put("HAASG40", sqlData.grabLinuxLab("HAASG40"));
                    labs.put("HAAS257", sqlData.grabLinuxLab("HAAS257"));
                } catch (SQLException e) {
                    log.log(Level.WARNING, "Unable to create status response", e);
                    return response.getHTTPError(500);
                }

                ObjectWriter objWriter = mapper.writer().withDefaultPrettyPrinter();

                try {
                    response.setBody(objWriter.writeValueAsString(labs));
                    response.setStatus(200);
                } catch (JsonProcessingException e) {
                    log.log(Level.WARNING, "Error while building json response for labs", e);
                    return HTTPResponse.getHTTPError(500);
                }
                return response;
            default:
                return HTTPResponse.getHTTPError(405);
        }
    }
}
