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
import com.google.inject.Singleton;
import de.cosmocode.palava.core.Registry;
import de.cosmocode.palava.core.lifecycle.Disposable;
import de.cosmocode.palava.core.lifecycle.Initializable;
import de.cosmocode.palava.core.lifecycle.LifecycleException;
import de.cosmocode.palava.ipc.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * @author Tobias Sarnowski
 */
@Singleton
final class AccessLogger implements IpcCallFilter, IpcConnectionDestroyEvent, Initializable, Disposable {
    private static final Logger LOG = LoggerFactory.getLogger(AccessLogger.class);

    private static final String ACCESS_LOG = "ACCESS_LOG";
    private Registry registry;


    @Inject
    public AccessLogger(Registry registry) {
        this.registry = registry;
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
    public Map<String, Object> filter(IpcCall call, IpcCommand command, IpcCallFilterChain chain) throws IpcCommandExecutionException {
        // new connection?
        Access access = call.getConnection().get(ACCESS_LOG);
        if (access == null) {
            access = new Access(
                    String.class.cast(call.getConnection().get(CustomProtocol.REQUEST_URI)),
                    call.getConnection().getSession().getIdentifier()
            );
            call.getConnection().set(ACCESS_LOG, access);
        }

        try {
            Map<String,Object> result = chain.filter(call, command);
            access.logSuccess(call);
            return result;
        } catch (IpcCommandExecutionException e) {
            access.logFailure(call);
            throw e;
        } catch (RuntimeException e) {
            access.logFailure(call);
            throw e;
        }
    }

    @Override
    public void eventIpcConnectionDestroy(IpcConnection connection) {
        Access access = connection.get(ACCESS_LOG);
        if (access != null) {
            access.doLog();
        }
    }
}