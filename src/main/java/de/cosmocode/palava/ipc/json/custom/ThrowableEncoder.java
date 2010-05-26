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

/**
 * Defines an encoder for {@link Throwable}s. A {@link ThrowableEncoder}
 * can be used in the {@link CustomProtocol} to define a custom
 * way to encode {@link Exception}s.
 *
 * @since 1.0
 * @author Willi Schoenborn
 */
public interface ThrowableEncoder {

    /**
     * Transforms the specified throwable into a map.
     * 
     * @since 1.0
     * @param throwable the throwable
     * @return a map containing all relevant information
     */
    Map<String, Object> encode(Throwable throwable);
    
}
