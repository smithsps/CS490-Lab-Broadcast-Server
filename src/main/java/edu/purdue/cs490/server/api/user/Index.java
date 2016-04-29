package edu.purdue.cs490.server.api.user;


import com.fasterxml.jackson.databind.ObjectMapper;
import edu.purdue.cs490.server.SQLiteData;
import edu.purdue.cs490.server.Server;
import edu.purdue.cs490.server.data.HTTPRequest;
import edu.purdue.cs490.server.data.HTTPResponse;

import java.util.logging.Logger;

public class Index {
    private static final Logger log = Logger.getLogger(Registration.class.getName());
    private static SQLiteData sqlData = Server.getInstance().getSQLData();

    static String html = "<!doctype html>\n" +
            "<html>\n" +
            "<head>\n" +
            "    <title>Lab Broadcast API Documentation</title>\n" +
            "</head>\n" +
            "<body>\n" +
            "   <h2>Lab Broadcast API Documentation</h2>\n" +
            "   <p>Last Updated: April 29, 2016</p>\n" +
            "   <p><a href=\"https://github.com/smithsps/CS490-Lab-Broadcast-Server\">Github Project</a></p>\n" +
            "   \n" +
            "</body>\n" +
            "</html>";

    public static HTTPResponse handleIndex(HTTPRequest request) {
        HTTPResponse response = new HTTPResponse();

        switch (request.getMethod()) {
            case GET:
                response.setStatus(200);
                response.setHeader("Content-Type", "text\\html");
                response.setBody(html);

                return response;
            default:
                return HTTPResponse.getHTTPError(405);
        }
    }
}
