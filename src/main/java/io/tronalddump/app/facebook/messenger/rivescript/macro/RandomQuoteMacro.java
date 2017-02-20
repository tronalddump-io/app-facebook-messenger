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

package io.tronalddump.app.facebook.messenger.rivescript.macro;

import com.rivescript.RiveScript;
import com.rivescript.macro.Subroutine;
import io.tronalddump.app.facebook.messenger.cache.TagsCache;
import io.tronalddump.client.TronaldClient;

import java.util.List;
import java.util.logging.Logger;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;

/**
 * RiveScript {@link Subroutine} for retrieving a random quote.
 *
 * @author Marcel Overdijk
 */
public class RandomQuoteMacro implements Subroutine {

    private static final Logger logger = Logger.getLogger(RandomQuoteMacro.class.getName());

    private final TronaldClient tronaldClient;
    private final TagsCache tagsCache;

    public RandomQuoteMacro(final TronaldClient tronaldClient, final TagsCache tagsCache) {
        this.tronaldClient = requireNonNull(tronaldClient, "'tronaldClient' must not be null");
        this.tagsCache = requireNonNull(tagsCache, "'tagsCache' must not be null");
    }

    @Override
    public String call(RiveScript rivescript, String[] args) {
        if (args.length == 1) {
            String tag = args[0];
            List<String> tags = tagsCache.getTags();
            if (tags.contains(tag)) {
                logger.info("Retrieving random quote with tag: " + tag);
                return tronaldClient.getRandomQuote(tag).getValue();
            } else {
                return format("I said so many stupid things but there is no tag '%s' yet. Type 'tags' to see available tags.", tag);
            }
        } else {
            logger.info("Retrieving random quote");
            return tronaldClient.getRandomQuote().getValue();
        }
    }
}
