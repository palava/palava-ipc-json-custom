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
