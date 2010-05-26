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

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;

import de.cosmocode.palava.ipc.IpcArguments;
import de.cosmocode.palava.ipc.IpcCall;
import de.cosmocode.palava.ipc.IpcConnection;
import de.cosmocode.palava.ipc.MapIpcArguments;
import de.cosmocode.palava.ipc.protocol.DetachedCall;
import de.cosmocode.palava.scope.AbstractScopeContext;

/**
 * Custom {@link IpcCall} implementation.
 *
 * @since 1.0
 * @author Willi Schoenborn
 */
final class CustomCall extends AbstractScopeContext implements DetachedCall {

    private Map<Object, Object> context;
    
    private final IpcArguments arguments;
    
    private IpcConnection connection;
    
    public CustomCall(IpcArguments arguments) {
        this.arguments = Preconditions.checkNotNull(arguments, "Arguments"); 
    }
    
    public CustomCall(Map<String, Object> arguments) {
        Preconditions.checkNotNull(arguments, "Arguments");
        this.arguments = new MapIpcArguments(arguments);
    }
    
    @Override
    protected Map<Object, Object> context() {
        if (context == null) {
            context = Maps.newHashMap();
        }
        return context;
    }

    @Override
    public IpcArguments getArguments() {
        return  arguments;
    }

    @Override
    public IpcConnection getConnection() {
        Preconditions.checkState(connection != null, "Not yet attached to a connection");
        return connection;
    }
    
    @Override
    public void attachTo(IpcConnection c) {
        this.connection = Preconditions.checkNotNull(c, "Connection");
    }

}
