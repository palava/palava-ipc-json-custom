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

import de.cosmocode.palava.ipc.protocol.DetachedConnection;

/**
 * @author Tobias Sarnowski
 */
public interface CustomPreCallEvent {

    /**
     * Will be called before the incoming requests is parsed.
     *
     * @param request the incoming request
     * @param response the response which will be filled
     * @param connection a detached connection
     */
    void eventPreCall(Map<String, Object> request, Map<String, Object> response, DetachedConnection connection);

}
