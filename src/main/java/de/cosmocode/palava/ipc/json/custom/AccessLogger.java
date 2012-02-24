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
import java.util.concurrent.TimeUnit;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

import de.cosmocode.palava.core.Registry;
import de.cosmocode.palava.core.lifecycle.Disposable;
import de.cosmocode.palava.core.lifecycle.Initializable;
import de.cosmocode.palava.core.lifecycle.LifecycleException;
import de.cosmocode.palava.ipc.Browser;
import de.cosmocode.palava.ipc.Current;
import de.cosmocode.palava.ipc.IpcCall;
import de.cosmocode.palava.ipc.IpcCallFilter;
import de.cosmocode.palava.ipc.IpcCallFilterChain;
import de.cosmocode.palava.ipc.IpcCommand;
import de.cosmocode.palava.ipc.IpcCommandExecutionException;
import de.cosmocode.palava.ipc.IpcConnection;
import de.cosmocode.palava.ipc.IpcConnectionDestroyEvent;

/**
 * A filter which logs ipc access.
 * 
 * @author Tobias Sarnowski
 */
@Singleton
final class AccessLogger implements IpcCallFilter, IpcConnectionDestroyEvent, Initializable, Disposable {

    private static final String ACCESS_LOG = "ACCESS_LOG";
    
    private final Registry registry;
    private final Provider<Browser> currentBrowserProvider;

    private long callThreshold;
    private TimeUnit callThresholdUnit;
    private long connectionThreshold;
    private TimeUnit connectionThresholdUnit;

    @Inject
    AccessLogger(Registry registry, @Current Provider<Browser> currentBrowserProvider) {
        this.registry = registry;
        this.currentBrowserProvider = currentBrowserProvider;
    }

    @Override
    public void initialize() throws LifecycleException {
        registry.register(IpcConnectionDestroyEvent.class, this);
    }

    @Override
    public void dispose() throws LifecycleException {
        registry.remove(this);
    }

    @Inject(optional = true)
    public void setCallThreshold(@Named(AccessConfig.CALL_THRESHOLD) long callThreshold) {
        this.callThreshold = callThreshold;
    }

    @Inject(optional = true)
    public void setCallThresholdUnit(@Named(AccessConfig.CALL_THRESHOLD_UNIT) TimeUnit callThresholdUnit) {
        this.callThresholdUnit = callThresholdUnit;
    }

    @Inject(optional = true)
    public void setConnectionThreshold(@Named(AccessConfig.CONNECTION_THRESHOLD) long connectionThreshold) {
        this.connectionThreshold = connectionThreshold;
    }

    @Inject(optional = true)
    public void setConnectionThresholdUnit(
            @Named(AccessConfig.CONNECTION_THRESHOLD_UNIT) TimeUnit connectionThresholdUnit) {
        this.connectionThresholdUnit = connectionThresholdUnit;
    }

    @Override
    public Map<String, Object> filter(IpcCall call, IpcCommand command, IpcCallFilterChain chain)
        throws IpcCommandExecutionException {
        
        Access access = Access.class.cast(call.getConnection().get(ACCESS_LOG));
        
        // new connection?
        if (access == null) {
            final String identifier = call.getConnection().getSession().getIdentifier();
            access = new Access(currentBrowserProvider.get(), identifier);
            call.getConnection().put(ACCESS_LOG, access);
        }

        try {
            final long startedCall = System.currentTimeMillis();
            final Map<String, Object> result = chain.filter(call, command);
            final long stoppedCall = System.currentTimeMillis();

            if (callThreshold > 0) {
                final long used = stoppedCall - startedCall;
                final long threshold = callThresholdUnit.toMillis(callThreshold);

                if (used >= threshold) {
                    access.getLog().warn("SLOW CALL detected: {} {} [used: {}ms, threshold: {}ms]",
                        new Object[]{command.getClass().getName(),
                            call.getArguments().toString(),
                            used,
                            threshold});
                }
            }

            access.success(call);
            return result;
        } catch (IpcCommandExecutionException e) {
            access.failure(call);
            throw e;
        /* CHECKSTYLE:OFF */
        } catch (RuntimeException e) {
        /* CHECKSTYLE:ON */
            access.failure(call);
            throw e;
        }
    }

    @Override
    public void eventIpcConnectionDestroy(IpcConnection connection) {
        final Access access = Access.class.cast(connection.get(ACCESS_LOG));
        if (access == null) return;

        if (connectionThreshold > 0) {
            final long used = System.currentTimeMillis() - access.getStarted();
            final long threshold = connectionThresholdUnit.toMillis(connectionThreshold);

            if (used >= threshold) {
                access.getLog().warn("SLOW CONNECTION detected: {} [used: {}ms, threshold: {}ms]",
                    new Object[]{access.getRequestUrl(), used, threshold});
            }
        }

        access.log();
    }
    
}
