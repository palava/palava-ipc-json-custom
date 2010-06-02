package de.cosmocode.palava.ipc.json.custom;

import com.google.inject.Binder;
import com.google.inject.Module;

/**
 * Binds {@link CustomProtocol} as eager singleton.
 *
 * @since 
 * @author Willi Schoenborn
 */
public final class CustomProtocolModule implements Module {

    @Override
    public void configure(Binder binder) {
        binder.bind(CustomProtocol.class).asEagerSingleton();
    }

}
