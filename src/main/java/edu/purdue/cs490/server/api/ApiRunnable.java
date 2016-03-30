package edu.purdue.cs490.server.api;

import edu.purdue.cs490.server.data.HTTPRequest;
import edu.purdue.cs490.server.data.HTTPResponse;

public interface ApiRunnable {
    HTTPResponse run(HTTPRequest httpRequest);
}
