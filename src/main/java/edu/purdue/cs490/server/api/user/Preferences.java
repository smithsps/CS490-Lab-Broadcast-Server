package edu.purdue.cs490.server.api.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.purdue.cs490.server.SQLiteData;
import edu.purdue.cs490.server.Server;
import edu.purdue.cs490.server.data.HTTPRequest;
import edu.purdue.cs490.server.data.HTTPResponse;
import edu.purdue.cs490.server.data.sql.Account;
import edu.purdue.cs490.server.data.sql.User;

import org.mindrot.jbcrypt.BCrypt;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Preferences {
    private static final Logger log = Logger.getLogger(Preferences.class.getName());
    private static SQLiteData sqlData = Server.getInstance().getSQLData();

    public static HTTPResponse handleUser(HTTPRequest request) {

        //SSL is REQUIRED for all user operations. Mostly for passing the password privately.
        if (!request.isSSL()) {
            return HTTPResponse.getHTTPError(497);
        }

        HTTPResponse response = new HTTPResponse();
        ObjectMapper mapper = new ObjectMapper();

        switch (request.getMethod()) {
            case GET:
            	User user = new User();
                try {
                    log.fine(request.getBody());
                    Map data = mapper.readValue(request.getBody(), Map.class);
					
					String username = (String) data.get("username");
					
					try{
						user = sqlData.grabUserPreferences(username);
                    }catch(SQLException ex){
						System.out.println(ex.getErrorCode());
                        log.log(Level.WARNING, "Exception while trying to grab user.");
                        response.setStatus(500);
                        response.setSimpleJsonMessage("error", "Did not match our records.");
					}	
					
				} catch (IOException e) {
                    log.log(Level.WARNING, "Error while trying to read JSON from response", e);
                    return HTTPResponse.getHTTPError(400);
                }
				
				ObjectWriter objWriter = mapper.writer().withDefaultPrettyPrinter();
				
				try{
				    response.setBody(objWriter.writeValueAsString(user));
					response.setStatus(200);
				} catch (JsonProcessingException e) {
                    log.log(Level.WARNING, "Error while building json response for user", e);
                    return HTTPResponse.getHTTPError(500);
                }

				return response;

            case PUT:
                try {
                    log.fine(request.getBody());
                    Map data = mapper.readValue(request.getBody(), Map.class);

                    String username = (String) data.get("username");
                    String courses = (String) data.get("courses");
                    String current = (String) data.get("current");
                    String languages = (String) data.get("languages");

                    sqlData.addUser(username, courses, current, languages);


                } catch (IOException e) {
                    log.log(Level.WARNING, "Error while trying to read JSON from response", e);
                    return HTTPResponse.getHTTPError(400);
                }

                response.setStatus(200);
                response.setSimpleJsonMessage("success", "Successfuly added user to tUser table");
                return response;

            default:
                return HTTPResponse.getHTTPError(405);
        }
    }
}
