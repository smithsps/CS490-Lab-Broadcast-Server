package edu.purdue.cs490.server.data;

import java.util.HashMap;

public class HTTPRequest {

    HTTPMethod method;
    String uri, version;
    HashMap<String, String> headers = new HashMap<String, String>(8);
    String body;
    Boolean ssl;

    public HTTPRequest() {}

    public void setMethod(String method) {
        this.method = HTTPMethod.valueOf(method.toUpperCase());
    }

    public HTTPMethod getMethod() {
        return method;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getUri() {
        return uri;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getVersion() {
        return version;
    }

    public void setHeader(String key, String value) {
        headers.put(key, value);
    }

    public String getHeader(String key) {
        return headers.get(key);
    }

    public boolean hasHeader(String key) {
        return headers.containsKey(key);
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getBody() {
        return body;
    }

    public void setSSL(Boolean ssl) {
        this.ssl = ssl;
    }

    public Boolean isSSL() {
        return ssl;
    }

}
