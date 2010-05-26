package de.cosmocode.palava.ipc.json.custom;

import java.util.List;
import java.util.Map;

import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;


/**
 * Custom {@link ThrowableEncoder} implementation.
 *
 * @since 1.0
 * @author Willi Schoenborn
 */
public final class CustomThrowableEncoder implements ThrowableEncoder {

    @Override
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
        
        return map;
    }

}
