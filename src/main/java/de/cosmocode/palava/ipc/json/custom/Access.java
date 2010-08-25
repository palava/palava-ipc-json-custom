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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.cosmocode.palava.ipc.Browser;
import de.cosmocode.palava.ipc.IpcCall;

/**
 * A utility class which is used by {@link AccessLogger} to count and log
 * succeeded and failed command execution.
 *  
 * @author Tobias Sarnowski
 * @author Willi Schoenborn
 */
final class Access {
    
    private static final Logger LOG = LoggerFactory.getLogger(Access.class);

    private String sessionId;
    private String identifier;

    private final String requestUrl;

    private int success;
    private int failure;

    public Access(Browser browser, String identifier) {
        this.identifier = identifier;

        final StringBuilder url = new StringBuilder();
        url.append(browser.isHttps() ? "https://" : "http://");
        url.append(browser.getHttpHost());
        url.append(browser.getRequestUri());

        this.requestUrl = url.toString();

        LOG.debug("\n\n========== {} ==========\n", this.requestUrl);
    }

    /**
     * Counts a successful command execution.
     * 
     * @since 1.2
     * @param call the current call
     */
    public void success(IpcCall call) {
        success++;
        if (sessionId == null) {
            sessionId = call.getConnection().getSession().getSessionId();
        }
    }

    /**
     * Counts a failed command execution.
     * 
     * @since 1.2
     * @param call the current call
     */
    public void failure(IpcCall call) {
        failure++;
    }

    /**
     * Logs the collected results using the {@link Logger} of this class.
     * 
     * @since 1.2
     */
    public void log() {
        LOG.info("{}  ({} successful, {} failed commands, {} / {})", new Object[]{
            requestUrl, success, failure, sessionId, identifier
        });
    }
    
}
