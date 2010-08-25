/**
 * Copyright 2010 CosmoCode GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.cosmocode.palava.ipc.json.custom;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.cosmocode.palava.ipc.Browser;
import de.cosmocode.palava.ipc.IpcCall;

/**
 * @since 2.0
 * @author Tobias Sarnowski
 */
final class CurrentBrowser implements Browser {
    private static final Logger LOG = LoggerFactory.getLogger(CurrentBrowser.class);

    private final IpcCall ipcCall;

    CurrentBrowser(IpcCall ipcCall) {
        this.ipcCall = ipcCall;
    }

    private String getKey(String key) {
        Map<?, ?> meta = ipcCall.get(CustomProtocol.META);
        if (!meta.containsKey(key)) {
            throw new UnsupportedOperationException("Information " + key + " not available");
        }
        Object value = meta.get(key);
        if (value == null) {
            throw new UnsupportedOperationException("Information " + key + " is null");
        }
        return value.toString();
    }

    private boolean getBoolKey(String key) {
        String value = getKey(key);
        if ("TRUE".equalsIgnoreCase(value) || "1".equals(value)) {
            return true;
        } else if ("FALSE".equalsIgnoreCase(value) || "0".equals(value)) {
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
        return getKey("HTTP_REFERER");
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