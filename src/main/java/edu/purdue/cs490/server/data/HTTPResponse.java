package edu.purdue.cs490.server.data;

import java.util.HashMap;
import java.util.Map;

public class HTTPResponse {

    final String version = "HTTP/1.1";

    Integer status = 500;
    HashMap<String, String> headers = new HashMap<>(8);
    String body;

    public static Map<Integer, String> statusDescription;

    static {
        statusDescription = new HashMap<>(15);

        // None Conclusive List (Maybe better as enum?)
        statusDescription.put(200, "OK");
        statusDescription.put(201, "Created");
        statusDescription.put(202, "Accepted");

        statusDescription.put(400, "Bad Request");
        statusDescription.put(401, "Unauthorized");
        statusDescription.put(403, "Forbidden");
        statusDescription.put(404, "Not Found");
        statusDescription.put(405, "Method Not Allowed");
        statusDescription.put(411, "Length Required");
        statusDescription.put(497, "HTTP Request Sent to HTTPS Resource");

        statusDescription.put(500, "Internal Server Error");
        statusDescription.put(501, "Not Implemented");
    }

    public HTTPResponse() {
        // Default Headers
        setHeader("Access-Control-Allow-Origin", "*");
        setHeader("Content-Type", "application/json");
        setBody("");
    }

    public static HTTPResponse getHTTPError(int errorCode) {
        HTTPResponse response = new HTTPResponse();
        response.setStatus(errorCode);
        response.setSimpleJsonMessage("error", errorCode + " " + response.getStatusMessage(errorCode));
        return response;
    }

    public String getStatusLine() {
        // Status-Line = HTTP-Version SP Status-Code SP Reason-Phrase CRLF
        return String.format("%s %s %s", version, getStatus(), getStatusMessage(getStatus()));
    }

    public String getResponse() {
        StringBuilder res = new StringBuilder();
        res.append(getStatusLine());
        res.append("\r\n");

        for (String k : headers.keySet()) {
            res.append(k);
            res.append(": ");
            res.append(getHeader(k));
            res.append("\r\n");
        }
        res.append("\r\n");
        res.append(getBody());

        return res.toString();
    }

    public void setSimpleJsonMessage(String key, String value) {
        setBody(String.format("{\"%s\": \"%s\"}", key, value));
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

    private String getStatusMessage(int status) {
        if (statusDescription.containsKey(getStatus())) {
            return statusDescription.get(getStatus());
        }
        return "Internal Server Error";
    }

    public void setHeader(String key, String value) {
        headers.put(key, value);
    }

    public String getHeader(String key) {
        return headers.get(key);
    }

    public void setBody(String body) {
        this.body = body;
        setHeader("Content-Length", String.valueOf(body.length()));
    }

    public String getBody() {
        return body;
    }

}
