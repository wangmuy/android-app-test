package com.example.test;

import android.util.Log;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;

public class PostEchoServer extends NanoHTTPD {
    public PostEchoServer(int port) {
        super(port);
    }

    @Override
    public Response serve(IHTTPSession session) {
        Log.d(MainActivity.TAG, "PostEchoServer serve: tid="+Thread.currentThread().getId());
        Map<String, String> files = new HashMap<>();
        try {
            session.parseBody(files);
        } catch (IOException e) {
            return newFixedLengthResponse(Response.Status.INTERNAL_ERROR, MIME_PLAINTEXT,
                    "SERVER INTERNAL ERROR: IOException: " + e.getMessage());
        } catch (ResponseException e) {
            return newFixedLengthResponse(e.getStatus(), MIME_PLAINTEXT, e.getMessage());
        }
        String data = files.get("postData");
        Log.d(MainActivity.TAG, "serve recv data="+data);
        return newFixedLengthResponse(Response.Status.OK, NanoHTTPD.MIME_PLAINTEXT, data);
    }
}
