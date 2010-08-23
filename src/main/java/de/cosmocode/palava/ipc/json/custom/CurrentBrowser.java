package de.cosmocode.palava.ipc.json.custom;

import de.cosmocode.palava.ipc.Browser;
import de.cosmocode.palava.ipc.IpcCall;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Tobias Sarnowski
 */
final class CurrentBrowser implements Browser {
    private static final Logger LOG = LoggerFactory.getLogger(CurrentBrowser.class);

    private final IpcCall ipcCall;

    CurrentBrowser(IpcCall ipcCall) {
        this.ipcCall = ipcCall;
    }

    private String getKey(String key) {
        if (!ipcCall.contains(key)) {
            throw new UnsupportedOperationException("Information " + key + " not available");
        }
        String value = ipcCall.get(key);
        if (value == null) {
            throw new UnsupportedOperationException("Information " + key + " is null");
        }
        return value;
    }

    private boolean getBoolKey(String key) {
        String https = getKey("HTTP_HOST");
        if ("TRUE".equalsIgnoreCase(https)) {
            return true;
        } else if ("FALSE".equalsIgnoreCase(https)) {
            return false;
        } else {
            throw new UnsupportedOperationException("Information " + key + " is not valid");
        }
    }

    @Override
    public String getHttpHost() {
        return getKey("HTTP_HOST");
    }

    @Override
    public boolean isHttps() {
        return getBoolKey("HTTPS");
    }

    @Override
    public String getRequestUri() {
        return getKey("REQUEST_URI");
    }

    @Override
    public String getRequestMethod() {
        return getKey("REQUEST_METHOD");
    }

    @Override
    public String getReferer() {
        //return getKey("REFERER");
        throw new UnsupportedOperationException("Referer not yet implemented!");
    }

    @Override
    public String getRemoteAddress() {
        return getKey("REMOTE_ADDRESS");
    }

    @Override
    public String getUserAgent() {
        return getKey("HTTP_USER_AGENT");
    }

    @Override
    public String getHttpAccept() {
        return getKey("HTTP_ACCEPT");
    }

    @Override
    public String getHttpAcceptLanguage() {
        return getKey("HTTP_ACCEPT_LANGUAGE");
    }

    @Override
    public String getHttpAcceptEncoding() {
        return getKey("HTTP_ACCEPT_ENCODING");
    }

    @Override
    public String getHttpAcceptCharset() {
        return getKey("HTTP_ACCEPT_CHARSET");
    }


    @Override
    public String toString() {
        return "CurrentBrowser{" +
                "ipcCall=" + ipcCall +
                '}';
    }
}