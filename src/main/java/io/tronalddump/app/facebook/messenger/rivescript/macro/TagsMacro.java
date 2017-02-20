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

import java.util.List;

import static java.util.Objects.requireNonNull;

/**
 * RiveScript {@link Subroutine} for retrieving tags.
 *
 * @author Marcel Overdijk
 */
public class TagsMacro implements Subroutine {

    private final TagsCache tagsCache;
    private final int tagsPageSize;

    public TagsMacro(final TagsCache tagsCache, final int categoriesPageSize) {
        this.tagsCache = requireNonNull(tagsCache, "'tagsCache' must not be null");
        this.tagsPageSize = categoriesPageSize;
    }

    @Override
    public String call(RiveScript rivescript, String[] args) {
        int pageNumber = 1;
        try {
            pageNumber = Integer.parseInt(args[0]);
        } catch (NumberFormatException ignore) {
        }
        List<List<String>> tagsPaged = tagsCache.getTagsPaged(tagsPageSize);
        if (tagsPaged.size() < pageNumber) {
            pageNumber = 1;
        }
        List<String> tagsPage = tagsPaged.get(pageNumber - 1);
        // Tags need to be returned as <title>=<payload> pairs. For example: Title 1=PAYLOAD_1, Title 2=PAYLOAD_2
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < tagsPage.size(); i++) {
            String title = tagsPage.get(i);
            String payload = "RANDOM_QUOTE_WITH_TAG_" + title;
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append(title).append("=").append(payload);
        }
        if (pageNumber < tagsPaged.size()) {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append("More...").append("=").append("TAGS_MORE_" + (pageNumber + 1));
        }
        return sb.toString();
    }
}
