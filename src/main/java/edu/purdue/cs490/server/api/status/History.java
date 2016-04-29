package edu.purdue.cs490.server.api.status;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import edu.purdue.cs490.server.SQLiteData;
import edu.purdue.cs490.server.Server;
import edu.purdue.cs490.server.data.HTTPRequest;
import edu.purdue.cs490.server.data.HTTPResponse;

import java.sql.SQLException;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class History {
    private static final Logger log = Logger.getLogger(Status.class.getName());
    private static SQLiteData sqlData = Server.getInstance().getSQLData();

    public static HTTPResponse handleHistory(HTTPRequest request) {
        HTTPResponse response = new HTTPResponse();
        ObjectMapper mapper = new ObjectMapper();


        switch (request.getMethod()) {
            case GET:
                Map<String, Object> labHistory = new HashMap<>();

                long startTime = Instant.now().getEpochSecond() - 30 * 60 * 1000;
                long endTime = Instant.now().getEpochSecond() + 30 * 60 * 1000;

                labHistory.put("startTime", startTime);
                labHistory.put("endTime", endTime);
                labHistory.put("interval", 30);

                try {
                    labHistory.put("LWSNB146", sqlData.getHistory("moore", startTime, endTime, 30));
                    labHistory.put("LWSNB148", sqlData.getHistory("pod", startTime, endTime, 30));
                    labHistory.put("LWSNB158", sqlData.getHistory("sslab", startTime, endTime, 30));
                    labHistory.put("HAASG40", sqlData.getHistory("borg", startTime, endTime, 30));
                    labHistory.put("HAAS257", sqlData.getHistory("pod", startTime, endTime, 30));
                } catch (SQLException e) {
                    log.log(Level.WARNING, "Unable to create history response", e);
                    return response.getHTTPError(500);
                }

                ObjectWriter objWriter = mapper.writer().withDefaultPrettyPrinter();

                try {
                    response.setBody(objWriter.writeValueAsString(labHistory));
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

