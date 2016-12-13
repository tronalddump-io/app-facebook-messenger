/*
 * Copyright 2015-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.tronalddump.app.facebook.messenger;

import com.github.marsbits.restfbmessenger.DefaultMessenger;
import com.github.marsbits.restfbmessenger.Messenger;
import com.github.marsbits.restfbmessenger.MessengerProvider;
import com.github.marsbits.restfbmessenger.webhook.CallbackHandler;

/**
 * The Tronald Dump IO {@code MessengerProvider}.
 *
 * @author Marcel Overdijk
 */
public class TronaldDumpMessengerProvider implements MessengerProvider {

    @Override
    public Messenger getMessenger() {
        String verifyToken = System.getProperty("facebook.verify_token");
        String accessToken = System.getProperty("facebook.access_token");
        String appSecret = System.getProperty("facebook.app_secret");
        CallbackHandler callbackHandler = new TronaldDumpCallbackHandler();
        return new DefaultMessenger(verifyToken, accessToken, appSecret, callbackHandler);
    }
}
