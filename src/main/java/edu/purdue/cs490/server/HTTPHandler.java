package edu.purdue.cs490.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import edu.purdue.cs490.server.data.HTTPRequest;
import edu.purdue.cs490.server.data.HTTPResponse;



public class HTTPHandler implements Runnable{
    Socket clientSocket;
    BufferedWriter outToClient;
    BufferedReader inFromClient;

    private static final Logger log = Logger.getLogger(HTTPHandler.class.getName());

    public HTTPHandler(Socket client) {
        this.clientSocket = client;

        try {
            this.inFromClient = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        } catch(IOException e) {
            log.log(Level.WARNING, "Could not create input reader for client.", e);
        }
        try {
            this.outToClient = new BufferedWriter(new OutputStreamWriter(this.clientSocket.getOutputStream()));
        } catch(IOException e) {
            log.log(Level.WARNING, "Could not create output writer for client.", e);
        }
    }


    public HTTPRequest handleHTTPRequest(String request) {

        HTTPRequest req = new HTTPRequest();

        String[] tokRequest = request.split("\\r?\\n");

        String[] tokRequestLine = tokRequest[0].split(" ");
        req.setMethod(tokRequestLine[0]);
        req.setUri(tokRequestLine[1]);
        req.setVersion(tokRequestLine[2]);

        for (int i = 1; i < tokRequest.length; i++) {
            String[] tokHeader = tokRequest[i].split(": ");
            req.setHeader(tokHeader[0], tokHeader[1]);
        }

        if (req.hasHeader("Content-Length")) {
            int contentLength = Integer.parseInt(req.getHeader("Content-Length"));

            try {
                char[] buffer = new char[contentLength];
                inFromClient.read(buffer, 0, contentLength);
                req.setBody(new String(buffer));
            } catch(IOException e) {
                log.log(Level.WARNING, "Unable to read from socket", e);
            }
        }

        return req;
    }

    public void handlePUT(HTTPRequest req) {
        System.out.format("Received: %s %s %s\n", req.getMethod(), req.getUri(), req.getVersion());
        ObjectMapper mapper = new ObjectMapper();
        try {
            System.out.println(req.getBody());
            Map data = mapper.readValue(req.getBody(), Map.class);

            // Replace with SQL Insert
            // Server.getInstance().occupied.put((String) data.get("name"), (Boolean) data.get("occupied"));

            // 200 = Success, and since we are always successful we always success.
            // In the future we can parse the body and validate it.
            this.outToClient.write("HTTP/1.1 200");
        } catch (IOException e) {
            log.log(Level.WARNING, "Error while trying to map JSON from response", e);
        }
    }

    public void handleGET(HTTPRequest req) {
        System.out.format("Received: %s %s %s\n", req.getMethod(), req.getUri(), req.getVersion());

        HTTPResponse response = new HTTPResponse();

        response.setHeader("Access-Control-Allow-Origin", "*");

        SQLiteData reqLab = new SQLiteData();

        Map<String, Integer> labs = new HashMap<>();

        labs.put("LWSNB160", reqLab.grabLab("LWSNB160"));
        labs.put("LWSNB158", reqLab.grabLab("LWSNB158"));
        labs.put("LWSNB148", reqLab.grabLab("LWSNB148"));
        labs.put("LWSNB146", reqLab.grabLab("LWSNB146"));
        labs.put("LWSNB131", reqLab.grabLab("LWSNB131"));
        labs.put("HAASG56", reqLab.grabLab("HAASG56"));
        labs.put("HAASG40", reqLab.grabLab("HAASG40"));
        labs.put("HAAS257", reqLab.grabLab("HAAS257"));

        ObjectMapper mapper = new ObjectMapper();
        ObjectWriter objWriter = mapper.writer().withDefaultPrettyPrinter();

        try {
            response.setBody(objWriter.writeValueAsString(labs));
        } catch (JsonProcessingException e) {
            log.log(Level.WARNING, "Error while building json response for labs", e);
        }

        try {
            this.outToClient.write(response.getResponse());
        } catch (IOException e) {
            log.log(Level.WARNING, "Error while trying to write response", e);
        }
    }

    public void run() {
        try {
            String message = "";
            String read;
            while(((read = inFromClient.readLine()) != null) && !(read.equals(""))){
                message += read + '\n';
            }

            if (!message.contains("HTTP/1.1")) {
                // Only Accept HTTP Requests
                return;
            }

            HTTPRequest req = handleHTTPRequest(message);

            switch(req.getMethod()) {
                case PUT:
                    handlePUT(req);
                    break;
                case GET:
                    handleGET(req);
                    break;
                default:
                    log.log(Level.WARNING, "Received unsupported HTTP Request", req);
                    this.outToClient.write("HTTP/1.1 500 Unsupported Request");
                    break;
            }
        } catch(IOException e) {
            log.log(Level.WARNING, "Problem with TCP I/O", e);
        } finally {
            try {
                this.outToClient.close();
                this.inFromClient.close();
            } catch (IOException e) {
                log.log(Level.WARNING, "Trouble trying to close socket.", e);
            }
        }
    }
}
