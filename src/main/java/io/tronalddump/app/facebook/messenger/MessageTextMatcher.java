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

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.CASE_INSENSITIVE;

/**
 * Basic message text matcher / interpreter.
 *
 * Might reimplement this later using e.g. RiveScript.
 *
 * @author Marcel Overdijk
 */
public class MessageTextMatcher {

    public static final Pattern PATTERN_HI = compilePattern("(hi|hello)(.*)?");

    public static final Pattern PATTERN_WHAT_IS_YOUR_NAME = compilePattern("(.*)?your name(.*)?");

    public static final Pattern PATTERN_HOW_ARE_YOU = compilePattern("(.*)?how are you(.*)?");

    public static final Pattern PATTERN_LOL = compilePattern("(lol|funny|haha*|hilarious|:\\)|:-\\)|:D|:-D)(.*)?");

    public static final Pattern PATTERN_HELP = compilePattern("(.*)?help(.*)?");

    public static final Pattern PATTERN_RANDOM_QUOTE = compilePattern("(.* )?quote(.*)?");

    public static final Pattern PATTERN_RANDOM_QUOTE_WITH_TAG = compilePattern("(.* )?quote (.* )?(about|tag|tagged with|tagged) (?<tag>.*)");

    public static final Pattern PATTERN_SEARCH_QUOTE = compilePattern("(.* )?(search|find) (.* )?quote (about|with|containing) (?<query>.*)");

    public static final Pattern PATTERN_ANOTHER_QUOTE = compilePattern("(.* )?(another|again)(.*)?");

    public static final Pattern PATTERN_TAGS = compilePattern("(.* )?tags(.*)?");

    private static final List<Pattern> PATTERNS = Arrays.asList(
            // do not change the order of the patterns
            PATTERN_TAGS,
            PATTERN_SEARCH_QUOTE,
            PATTERN_RANDOM_QUOTE_WITH_TAG,
            PATTERN_RANDOM_QUOTE,
            PATTERN_ANOTHER_QUOTE,
            PATTERN_HELP,
            PATTERN_HOW_ARE_YOU,
            PATTERN_WHAT_IS_YOUR_NAME,
            PATTERN_HI,
            PATTERN_LOL
            );

    public Matcher match(String messageText) {
        for (Pattern pattern : PATTERNS) {
            Matcher matcher = pattern.matcher(messageText);
            if (matcher.matches()) {
                return matcher;
            }
        }
        return null;
    }

    private static Pattern compilePattern(String pattern) {
        return Pattern.compile(pattern, CASE_INSENSITIVE);
    }
}
