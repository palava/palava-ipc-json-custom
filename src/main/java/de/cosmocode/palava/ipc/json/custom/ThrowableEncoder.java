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

import java.util.List;
import java.util.Map;

import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * Encodes {@link Throwable}s into {@link Map}s.
 * 
 * @since 1.0
 * @author Willi Schoenborn
 */
final class ThrowableEncoder {

    /**
     * Encodes the given throwable into a map.
     * 
     * @since 1.0
     * @param throwable the given throwable
     * @return a map containing all required information
     * @throws NullPointerException if throwable is null
     */
    public Map<String, Object> encode(Throwable throwable) {
        Preconditions.checkNotNull(throwable, "Throwable");
        final Throwable root = Throwables.getRootCause(throwable);
        final Map<String, Object> map = Maps.newHashMap();
        
        map.put("name", root.getClass().getName());
        map.put("message", root.getMessage());
        
        final List<Map<String, Object>> stacktrace = Lists.newLinkedList();
        
        for (StackTraceElement element : root.getStackTrace()) {
            final Map<String, Object> mappedElement = Maps.newHashMap();
            
            mappedElement.put("class", element.getClassName());
            mappedElement.put("filename", element.getFileName());
            mappedElement.put("line", element.getLineNumber());
            mappedElement.put("method", element.getMethodName());
            
            stacktrace.add(mappedElement);
        }
        
        map.put("stacktrace", stacktrace);

        final List<String> superNames = Lists.newArrayList();
        fillSuperName(superNames, throwable.getClass());
        map.put("superNames", superNames);
        
        return map;
    }

    private void fillSuperName(List<String> names, Class<?> throwable) {
        // do not go further than Throwable
        if (throwable == null || throwable == Exception.class) {
            return;
        }

        // list all super classes
        fillSuperName(names, throwable.getSuperclass());

        // enlist it
        names.add(throwable.getName());
    }

}
