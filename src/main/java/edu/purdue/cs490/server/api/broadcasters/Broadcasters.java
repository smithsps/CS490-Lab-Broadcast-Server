package edu.purdue.cs490.server.api.broadcasters;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.purdue.cs490.server.SQLiteData;
import edu.purdue.cs490.server.Server;
import edu.purdue.cs490.server.data.HTTPRequest;
import edu.purdue.cs490.server.data.HTTPResponse;
import edu.purdue.cs490.server.data.sql.Account;
import edu.purdue.cs490.server.data.sql.Broadcaster;

import org.mindrot.jbcrypt.BCrypt;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.ArrayList;

public class Broadcasters {
    private static final Logger log = Logger.getLogger(Broadcaster.class.getName());
    private static SQLiteData sqlData = Server.getInstance().getSQLData();

    public static HTTPResponse handleBroadcaster(HTTPRequest request) {

        //SSL is REQUIRED for all user operations. Mostly for passing the password privately.
        if (!request.isSSL()) {
            return HTTPResponse.getHTTPError(497);
        }

        HTTPResponse response = new HTTPResponse();
        ObjectMapper mapper = new ObjectMapper();

        switch (request.getMethod()) {
			case POST:

                ArrayList<Broadcaster> broadcasters = new ArrayList<Broadcaster>();
                try {
                    log.fine(request.getBody());
                    Map data = mapper.readValue(request.getBody(), Map.class);
					
					String room = (String) data.get("room");
					try{
						broadcasters = sqlData.grabSpecificBroadcasters(room);
					}catch(SQLException ex){
						System.out.println(ex.getErrorCode());
                        log.log(Level.WARNING, "Exception while trying to grab broadcasters.");
                        response.setStatus(500);
                        response.setSimpleJsonMessage("error", "Did not match our records.");
					}				
				} catch (IOException e) {
                    log.log(Level.WARNING, "Error while trying to read JSON from response", e);
                    return HTTPResponse.getHTTPError(400);
                }
				ObjectWriter objWriter = mapper.writer().withDefaultPrettyPrinter();
				
				try{
				    response.setBody(objWriter.writeValueAsString(broadcasters));
					response.setStatus(200);
				} catch (JsonProcessingException e) {
                    log.log(Level.WARNING, "Error while building json response for broadcasters", e);
                    return HTTPResponse.getHTTPError(500);
                }

				return response;
				
            case PUT:
				 try {
                    log.fine(request.getBody());
                    Map data = mapper.readValue(request.getBody(), Map.class);
					
					String username = (String) data.get("username");
                    String room = (String) data.get("room");
					String courses = (String) data.get("courses");
					

                    sqlData.addBroadcaster(username, room, courses);

					response.setStatus(200);
					response.setSimpleJsonMessage("success", "Added user to broadcasters table");

					return response;
				} catch (IOException e) {
                    log.log(Level.WARNING, "Error while trying to read JSON from response", e);
                    return HTTPResponse.getHTTPError(400);
                }
			case DELETE:
				 try {
                    log.fine(request.getBody());
                    Map data = mapper.readValue(request.getBody(), Map.class);
					
					String username = (String) data.get("username");
					
					sqlData.removeBroadcaster(username);

					response.setStatus(200);
					response.setSimpleJsonMessage("success", "Deleted user to broadcasters table");

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
