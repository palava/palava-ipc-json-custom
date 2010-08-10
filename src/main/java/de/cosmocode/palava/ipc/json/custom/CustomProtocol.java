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

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.google.inject.Inject;
import de.cosmocode.palava.core.Registry;
import de.cosmocode.palava.core.Registry.Key;
import de.cosmocode.palava.core.Registry.Proxy;
import de.cosmocode.palava.core.Registry.SilentProxy;
import de.cosmocode.palava.core.lifecycle.Disposable;
import de.cosmocode.palava.core.lifecycle.Initializable;
import de.cosmocode.palava.core.lifecycle.LifecycleException;
import de.cosmocode.palava.ipc.*;
import de.cosmocode.palava.ipc.json.Json;
import de.cosmocode.palava.ipc.protocol.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Implements a custom json-based ipc protocol which relies on the following
 * structure.
 *
 * Incoming requests will use the following schema:
 * <pre>
 * {
 *     "protocol": "palava/2.0",
 *     "meta": {
 *         "identifier": "optional session identifier",
 *         ...
 *     },
 *     "session": "your-session-id",
 *     "command": "fqcn.of.the.command.to.be.Executed",
 *     "arguments": {
 *         ...
 *     }
 * }
 * </pre>
 * 
 * <pre>
 * {
 *     "protocol": "palava/2.0",
 *     "session": "your-session-id",
 *     "result": {
 *         ...
 *     },
 *     "exception": { //optional
 *         "name": "fqcn.of.the.occured.Exception",
 *         "message": "message of the exception",
 *         "stacktrace": [
 *             {
 *                 "class": "fqcn.of.the.stacktrace.elements.Class",
 *                 "filename": "filename",
 *                 "line": "line number",
 *                 "method": "method name"
 *             },
 *             ....
 *         ]
 *     }
 * }
 * </pre>
 *
 * @since 1.0
 * @author Tobias Sarnowski
 * @author Willi Schoenborn
 */
public final class CustomProtocol implements Protocol, Initializable, Disposable {

    private static final Logger LOG = LoggerFactory.getLogger(CustomProtocol.class);

    // protocol keys
    public static final String VERSION = "palava2/1.0";
    public static final String PROTOCOL = "protocol";
    public static final String META = "meta";
    public static final String IDENTIFIER = "identifier";
    public static final String SESSION = "session";
    public static final String COMMAND = "command";
    public static final String ARGUMENTS = "arguments";
    public static final String RESULT = "result";
    public static final String EXCEPTION = "exception";

    // core meta informations
    public static final String REQUEST_URI = "request_uri";
    
    private final Registry registry;
    
    private final IpcCallCreateEvent createEvent;
    private final IpcCallDestroyEvent destroyEvent;

    private final CustomPreCallEvent preCallEvent;
    private final CustomPostCallEvent postCallEvent;
    
    private final IpcSessionProvider provider;
    private final IpcCommandExecutor executor;
    
    private final IpcCallScope scope;
    
    private ThrowableEncoder encoder = new CustomThrowableEncoder();
    
    @Inject
    public CustomProtocol(Registry registry, 
        @Proxy IpcCallCreateEvent createEvent,
        @SilentProxy IpcCallDestroyEvent destroyEvent,
        IpcSessionProvider provider,
        IpcCommandExecutor executor,
        IpcCallScope scope) {
        this.registry = Preconditions.checkNotNull(registry, "Registry");
        this.createEvent = Preconditions.checkNotNull(createEvent, "CreateEvent");
        this.destroyEvent = Preconditions.checkNotNull(destroyEvent, "DestroyEvent");
        this.provider = Preconditions.checkNotNull(provider, "SessionProvider");
        this.executor = Preconditions.checkNotNull(executor, "CommandExecutor");
        this.scope = Preconditions.checkNotNull(scope, "Scope");

        preCallEvent = registry.proxy(CustomPreCallEvent.class);
        postCallEvent = registry.proxy(CustomPostCallEvent.class);
    }

    @Inject(optional = true)
    void setEncoder(ThrowableEncoder encoder) {
        this.encoder = Preconditions.checkNotNull(encoder, "Encoder");
    }
    
    @Override
    public void initialize() throws LifecycleException {
        registry.register(Key.get(Protocol.class, Json.class), this);
    }
    
    @Override
    public boolean supports(Object request) {
        return VERSION.equals(((Map)request).get(PROTOCOL));
    }
    
    @Override
    public Map<String, Object> process(Object requestObj, DetachedConnection connection) throws ProtocolException {
        Map<String,Object> request = (Map<String,Object>)requestObj;

        final Map<String, Object> response = Maps.newHashMap();
        response.put(PROTOCOL, VERSION);

        // trigger manipulation event
        preCallEvent.eventPreCall(request, response, connection);

        final Map<?, ?> meta = Map.class.cast(request.get(META));
        checkNotNull(meta, META);

        final String requestUri = String.class.cast(meta.get(REQUEST_URI));
        connection.set(REQUEST_URI, requestUri);
        
        final String identifier = String.class.cast(meta.get(IDENTIFIER));
        final String sessionId = String.class.cast(request.get(SESSION));

        final IpcSession session;
        
        if (connection.isAttached()) {
            final IpcSession attached = connection.getSession();
            
            if (!attached.getSessionId().equals(sessionId)) {
                LOG.trace("SessionId of attached session differs from requested");
                session = provider.getSession(sessionId, identifier);
            } else if (!attached.getIdentifier().equals(identifier)) {
                LOG.trace("Identifier of attached session differs from requested");
                session = provider.getSession(sessionId, identifier);
            } else if (attached.isExpired()) {
                LOG.trace("Attached session is expired, using new");
                session = provider.getSession(sessionId, identifier);
            } else {
                LOG.trace("Re-using already attached session");
                session = attached;
            }
            
            connection.attachTo(session);
        } else {
            LOG.trace("Connection is not yet attached, retrieving session {}/{}", sessionId, identifier);
            session = provider.getSession(sessionId, identifier);
            connection.attachTo(session);
        }
        
        LOG.trace("Using {}", session);
        response.put(SESSION, session.getSessionId());

        final Object command = request.get(COMMAND);
        checkNotNull(command, COMMAND);
        
        @SuppressWarnings("unchecked")
        final Map<String, Object> rawArguments = Map.class.cast(request.get(ARGUMENTS));
        checkNotNull(rawArguments, ARGUMENTS);
        
        final IpcArguments arguments = new MapIpcArguments(rawArguments);
        
        final DetachedCall call = new CustomCall(arguments);
        call.attachTo(connection);
        
        createEvent.eventIpcCallCreate(call);
        scope.enter(call);
        
        try {
            final Map<String, Object> result = executor.execute(command.toString(), call);
            response.put(RESULT, result);
        /* CHECKSTYLE:OFF */
        } catch (Exception e) {
        /* CHECKSTYLE:ON */
            response.remove(RESULT);
            response.put(EXCEPTION, encoder.encode(e));
        } finally {
            destroyEvent.eventIpcCallDestroy(call);
            scope.exit();
        }

        // trigger manipulation events
        postCallEvent.eventPostCall(request, response, connection);

        return response;
    }
    
    private void checkNotNull(Object reference, Object args) throws ProtocolException {
        if (reference == null) {
            throw new ProtocolException(String.format("Missing %s", args));
        }
    }
    
    @Override
    public Object onError(Throwable t, Object request) {
        LOG.warn("Unexpected exception in custom protocol", t);
        return newHashMap(
            PROTOCOL, VERSION,
            SESSION, ((Map)request).get(SESSION),
            EXCEPTION, encoder.encode(t)
        );
    }
    
    private Map<String, Object> newHashMap(String k1, Object v1, String k2, Object v2, String k3, Object v3) {
        final Map<String, Object> returnValue = Maps.newHashMap();
        returnValue.put(k1, v1);
        returnValue.put(k2, v2);
        returnValue.put(k3, v3);
        return returnValue;
    }
    
    @Override
    public void dispose() throws LifecycleException {
        registry.remove(this);
    }

}
