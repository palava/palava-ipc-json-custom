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

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import de.cosmocode.palava.core.Registry;
import de.cosmocode.palava.core.lifecycle.Disposable;
import de.cosmocode.palava.core.lifecycle.Initializable;
import de.cosmocode.palava.core.lifecycle.LifecycleException;
import de.cosmocode.palava.ipc.*;

import java.util.Map;

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

    @Inject
    public AccessLogger(Registry registry, @Current Provider<Browser> currentBrowserProvider) {
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

    @Override
    public Map<String, Object> filter(IpcCall call, IpcCommand command, IpcCallFilterChain chain) 
        throws IpcCommandExecutionException {
        
        Access access = call.getConnection().get(ACCESS_LOG);
        
        // new connection?
        if (access == null) {
            final String identifier = call.getConnection().getSession().getIdentifier();
            access = new Access(currentBrowserProvider.get(), identifier);
            call.getConnection().set(ACCESS_LOG, access);
        }

        try {
            final Map<String, Object> result = chain.filter(call, command);
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
        final Access access = connection.get(ACCESS_LOG);
        if (access == null) return;
        access.log();
    }
    
}
