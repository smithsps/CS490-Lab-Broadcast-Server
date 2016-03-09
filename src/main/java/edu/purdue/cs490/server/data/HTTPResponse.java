package edu.purdue.cs490.server.data;

import java.util.HashMap;
import java.util.Map;

public class HTTPResponse {

    final String version = "HTTP/1.1";

    Integer status;
    HashMap<String, String> headers = new HashMap<>(8);
    String body;

    public static Map<Integer, String> statusCode;

    static {
        statusCode = new HashMap<>(10);

        // None Conclusive List
        statusCode.put(200, "OK");
        statusCode.put(201, "Created");
        statusCode.put(202, "Accepted");

        statusCode.put(400, "Bad Request");
        statusCode.put(401, "Unauthorized");
        statusCode.put(403, "Forbidden");
        statusCode.put(404, "Not Found");
        statusCode.put(411, "Length Required");

        statusCode.put(500, "Internal Server Error");
        statusCode.put(501, "Not Implemented");
    }

    public HTTPResponse() {}

    public String getStatusLine() {
        // Status-Line = HTTP-Version SP Status-Code SP Reason-Phrase CRLF
        return String.format("%s %s %s", version, status, statusCode.get(status));
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

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getStatus() {
        return status;
    }

    public void setHeader(String key, String value) {
        headers.put(key, value);
    }

    public String getHeader(String key) {
        return headers.get(key);
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getBody() {
        return body;
    }

}
