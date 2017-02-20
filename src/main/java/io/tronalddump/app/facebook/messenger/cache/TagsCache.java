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

package io.tronalddump.app.facebook.messenger.cache;

import io.tronalddump.client.TronaldClient;
import org.apache.commons.collections4.ListUtils;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;

/**
 * The Tronald Dump IO Tags Cache.
 *
 * @author Marcel Overdijk
 */
public class TagsCache {

    private static final Logger logger = Logger.getLogger(TagsCache.class.getName());

    private final TronaldClient tronaldClient;

    private List<String> tags;
    private long refreshInterval;
    private long refreshTimestamp = 0;

    public TagsCache(final TronaldClient tronaldClient) {
        this(tronaldClient, TimeUnit.DAYS.toMillis(1));
    }

    public TagsCache(final TronaldClient tronaldClient, long refreshInterval) {
        this.tronaldClient = requireNonNull(tronaldClient, "'tronaldClient' must not be null");
        this.refreshInterval = refreshInterval;
    }

    public List<String> getTags() {
        long currentTimeMillis = System.currentTimeMillis();
        if (tags == null || refreshTimestamp < currentTimeMillis) {
            if (tags == null) {
                logger.info("Tags cache not yet initialized");
            } else if (refreshTimestamp < currentTimeMillis) {
                logger.info("Tags cache expired");
            }
            synchronized (TagsCache.class) {
                logger.info("Retrieving tags");
                if (tags == null || refreshTimestamp < System.currentTimeMillis()) {
                    tags = tronaldClient.getTags();
                    refreshTimestamp = System.currentTimeMillis() + refreshInterval;
                }
            }
        } else {
            logger.fine(format("Tags cache not expired yet (%d millis until refresh)", (refreshTimestamp - currentTimeMillis)));
        }
        return tags;
    }

    public List<List<String>> getTagsPaged(int pageSize) {
        return ListUtils.partition(getTags(), pageSize);
    }

    public boolean containsIgnoreCase(String tag) {
        for (String t : getTags()) {
            if (t.equalsIgnoreCase(tag)) {
                return true;
            }
        }
        return false;
    }

    public String getTag(String tag) {
        for (String t : getTags()) {
            if (t.equalsIgnoreCase(tag)) {
                return t;
            }
        }
        return null;
    }
}
