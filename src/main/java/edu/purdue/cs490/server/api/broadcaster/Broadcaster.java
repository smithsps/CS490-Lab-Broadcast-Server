package edu.purdue.cs490.server.api.broadcaster;


import com.fasterxml.jackson.databind.ObjectMapper;
import edu.purdue.cs490.server.SQLiteData;
import edu.purdue.cs490.server.Server;
import edu.purdue.cs490.server.data.HTTPRequest;
import edu.purdue.cs490.server.data.HTTPResponse;
import edu.purdue.cs490.server.data.sql.Account;
import org.mindrot.jbcrypt.BCrypt;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Broadcaster {
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
			case GET:
				String broadcasters;
                try {
                    log.fine(request.getBody());
                    Map data = mapper.readValue(request.getBody(), Map.class);
					
					
					
					//try {
						broadcasters = sqlData.grabAllBroadcasters();
                    /*} catch (SQLException ex) {
                        System.out.println(ex.getErrorCode());
                        log.log(Level.WARNING, "Exception while trying to get broadcaster.");
                        response.setStatus(500);
                        response.setSimpleJsonMessage("error", "The username and password you entered did not match our records.");
                        return response;
                    }*/
					
				} catch (IOException e) {
                    log.log(Level.WARNING, "Error while trying to read JSON from response", e);
                    return HTTPResponse.getHTTPError(400);
                }
				
				response.setStatus(200);
				response.setSimpleJsonMessage("broadcasters", broadcasters);

				return response;
				
            case PUT:
				 try {
                    log.fine(request.getBody());
                    Map data = mapper.readValue(request.getBody(), Map.class);
					
					String username = (String) data.get("username");
                    String room = (String) data.get("room");
					String help = (String) data.get("help");
					
					//try {
                        sqlData.addBroadcaster(username, room, help);
                   /*}catch (SQLException ex) {
                        System.out.println(ex.getErrorCode());
                        log.log(Level.WARNING, "Exception while trying to get broadcaster.");
                        response.setStatus(500);
                        response.setSimpleJsonMessage("error", "The username and password you entered did not match our records.");
                        return response;
                    }*/
					response.setStatus(200);
					response.setSimpleJsonMessage("success", "Added user to broadcasters table");

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
