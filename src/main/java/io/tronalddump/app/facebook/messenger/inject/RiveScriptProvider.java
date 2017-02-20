/*
 * Copyright 2015-2017 the original author or authors.
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

package io.tronalddump.app.facebook.messenger.inject;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.rivescript.Config;
import com.rivescript.RiveScript;
import com.rivescript.session.NoOpSessionManager;
import io.tronalddump.app.facebook.messenger.cache.TagsCache;
import io.tronalddump.app.facebook.messenger.rivescript.macro.RandomQuoteMacro;
import io.tronalddump.app.facebook.messenger.rivescript.macro.SearchQuoteMacro;
import io.tronalddump.app.facebook.messenger.rivescript.macro.TagsMacro;
import io.tronalddump.client.TronaldClient;

import java.io.File;

/**
 * The {@link RiveScript} instance provider.
 *
 * @author Marcel Overdijk
 */
public class RiveScriptProvider implements Provider<RiveScript> {

    private TronaldClient tronaldClient;
    private TagsCache tagsCache;

    @Inject
    public RiveScriptProvider(TronaldClient tronaldClient, TagsCache tagsCache) {
        this.tronaldClient = tronaldClient;
        this.tagsCache = tagsCache;
    }

    @Override
    public RiveScript get() {
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("rivescript/tronald-dump.rive").getFile());
        RiveScript bot = new RiveScript(Config.Builder
                .utf8()
                .forceCase(true)
                .sessionManager(new NoOpSessionManager())
                .build());
        bot.setSubroutine("tags", new TagsMacro(tagsCache, 6));
        bot.setSubroutine("randomquote", new RandomQuoteMacro(tronaldClient, tagsCache));
        bot.setSubroutine("searchquote", new SearchQuoteMacro(tronaldClient));
        bot.loadFile(file);
        bot.sortReplies();
        return bot;
    }
}
