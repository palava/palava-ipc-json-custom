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

import com.google.inject.Binder;
import com.google.inject.Module;

import de.cosmocode.palava.ipc.Browser;
import de.cosmocode.palava.ipc.Current;
import de.cosmocode.palava.ipc.IpcCallScoped;

/**
 * Binds {@link Browser} to {@link CurrentBrowser}.
 * 
 * @since 2.0
 * @author Tobias Sarnowski
 */
public final class CurrentBrowserModule implements Module {

    @Override
    public void configure(Binder binder) {
        binder.bind(Browser.class).annotatedWith(Current.class).to(CurrentBrowser.class).in(IpcCallScoped.class);
    }

}
