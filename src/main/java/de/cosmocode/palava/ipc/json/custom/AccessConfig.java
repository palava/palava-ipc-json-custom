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

/**
 * Configuration keys for the access logger.
 *
 * @author Tobias Sarnowski
 */
public final class AccessConfig {

    public static final String PREFIX = "custom.access.";

    public static final String CALL_THRESHOLD = PREFIX + "call.threshold";
    public static final String CALL_THRESHOLD_UNIT = PREFIX + "call.thresholdUnit";
    public static final String CONNECTION_THRESHOLD = PREFIX + "connection.threshold";
    public static final String CONNECTION_THRESHOLD_UNIT = PREFIX + "connection.thresholdUnit";

    private AccessConfig() {
    }
}
