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

import de.cosmocode.palava.ipc.IpcCall;

/**
 * 
 * 
 * @author Tobias Sarnowski
 */
final class Access {
    
    private static final Logger LOG = LoggerFactory.getLogger(Access.class);

    private final String requestUri;
    private String sessionId;
    private String identifier;

    private int success;
    private int failure;

    public Access(String requestUri, String identifier) {
        this.requestUri = requestUri;
        this.identifier = identifier;
    }

    public void logSuccess(IpcCall call) {
        success++;
        if (sessionId == null) {
            sessionId = call.getConnection().getSession().getSessionId();
        }
    }

    public void logFailure(IpcCall call) {
        failure++;
    }

    public void doLog() {
        LOG.info("{}  ({} successful, {} failed commands, {} / {})", new Object[]{
            requestUri, success, failure, sessionId, identifier
        });
    }
    
}