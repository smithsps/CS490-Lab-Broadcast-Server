package edu.purdue.cs490.server.api.status;


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
                    return HTTPResponse.getHTTPError(500);
                }
                return response;
            default:
                return HTTPResponse.getHTTPError(405);
        }
    }
}
