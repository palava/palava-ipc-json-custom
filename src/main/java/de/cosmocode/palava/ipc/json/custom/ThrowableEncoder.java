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
