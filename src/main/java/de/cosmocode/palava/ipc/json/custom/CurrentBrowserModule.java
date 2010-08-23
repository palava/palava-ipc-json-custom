package de.cosmocode.palava.ipc.json.custom;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.Provides;
import de.cosmocode.palava.ipc.Browser;
import de.cosmocode.palava.ipc.Current;
import de.cosmocode.palava.ipc.IpcCall;
import de.cosmocode.palava.ipc.IpcCallScoped;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Tobias Sarnowski
 */
public final class CurrentBrowserModule implements Module {
    private static final Logger LOG = LoggerFactory.getLogger(CurrentBrowserModule.class);

    @Override
    public void configure(Binder binder) {
    }

    @Provides
    @IpcCallScoped
    @Current
    public Browser getCurrentBrowser(IpcCall call) {
        return new CurrentBrowser(call);
    }
}