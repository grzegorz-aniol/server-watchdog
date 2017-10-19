package com.cloudyna.service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class HttpMonitor {

    private URL url;
    
    public HttpMonitor(String protocol, String hostName, int port, String fileName) throws MalformedURLException {
        url = new URL(protocol, hostName, port, fileName);
    }
    
    public boolean check() {
        try {
            url.openConnection().getContent();
        } catch (IOException e) {
            return false;
        }
        return true;
    }

}
