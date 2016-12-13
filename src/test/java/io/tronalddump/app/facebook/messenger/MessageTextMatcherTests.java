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

import org.junit.Before;
import org.junit.Test;

import static io.tronalddump.app.facebook.messenger.MessageTextMatcher.PATTERN_ANOTHER_QUOTE;
import static io.tronalddump.app.facebook.messenger.MessageTextMatcher.PATTERN_HELP;
import static io.tronalddump.app.facebook.messenger.MessageTextMatcher.PATTERN_HI;
import static io.tronalddump.app.facebook.messenger.MessageTextMatcher.PATTERN_HOW_ARE_YOU;
import static io.tronalddump.app.facebook.messenger.MessageTextMatcher.PATTERN_LOL;
import static io.tronalddump.app.facebook.messenger.MessageTextMatcher.PATTERN_RANDOM_QUOTE;
import static io.tronalddump.app.facebook.messenger.MessageTextMatcher.PATTERN_RANDOM_QUOTE_WITH_TAG;
import static io.tronalddump.app.facebook.messenger.MessageTextMatcher.PATTERN_SEARCH_QUOTE;
import static io.tronalddump.app.facebook.messenger.MessageTextMatcher.PATTERN_TAGS;
import static io.tronalddump.app.facebook.messenger.MessageTextMatcher.PATTERN_WHAT_IS_YOUR_NAME;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;

/**
 * Tests for {@link MessageTextMatcher}.
 *
 * @author Marcel Overdijk
 */
public class MessageTextMatcherTests {

    private MessageTextMatcher matcher;

    @Before
    public void setUp() {
        matcher = new MessageTextMatcher();
    }

    @Test
    public void testPatternHi() {
        assertThat(matcher.match("hi").pattern(), is(sameInstance(PATTERN_HI)));
        assertThat(matcher.match("hi donald!").pattern(), is(sameInstance(PATTERN_HI)));
        assertThat(matcher.match("hello").pattern(), is(sameInstance(PATTERN_HI)));
        assertThat(matcher.match("hello donald!").pattern(), is(sameInstance(PATTERN_HI)));
    }

    @Test
    public void testPatternWhatIsYourName() {
        assertThat(matcher.match("your name").pattern(), is(sameInstance(PATTERN_WHAT_IS_YOUR_NAME)));
        assertThat(matcher.match("your name?").pattern(), is(sameInstance(PATTERN_WHAT_IS_YOUR_NAME)));
        assertThat(matcher.match("what is your name").pattern(), is(sameInstance(PATTERN_WHAT_IS_YOUR_NAME)));
        assertThat(matcher.match("hi, what is your name?").pattern(), is(sameInstance(PATTERN_WHAT_IS_YOUR_NAME)));
    }

    @Test
    public void testPatternHowAreYou() {
        assertThat(matcher.match("how are you").pattern(), is(sameInstance(PATTERN_HOW_ARE_YOU)));
        assertThat(matcher.match("how are you?").pattern(), is(sameInstance(PATTERN_HOW_ARE_YOU)));
        assertThat(matcher.match("hi, how are you?").pattern(), is(sameInstance(PATTERN_HOW_ARE_YOU)));
        assertThat(matcher.match("hi, how are you today?").pattern(), is(sameInstance(PATTERN_HOW_ARE_YOU)));
    }

    @Test
    public void testPatternLol() {
        assertThat(matcher.match("lol").pattern(), is(sameInstance(PATTERN_LOL)));
        assertThat(matcher.match("lol laughing my ass off").pattern(), is(sameInstance(PATTERN_LOL)));
        assertThat(matcher.match("haha").pattern(), is(sameInstance(PATTERN_LOL)));
        assertThat(matcher.match("hahaha").pattern(), is(sameInstance(PATTERN_LOL)));
        assertThat(matcher.match("hahahaha that was funny").pattern(), is(sameInstance(PATTERN_LOL)));
        assertThat(matcher.match(":)").pattern(), is(sameInstance(PATTERN_LOL)));
        assertThat(matcher.match(":-)").pattern(), is(sameInstance(PATTERN_LOL)));
        assertThat(matcher.match(":D").pattern(), is(sameInstance(PATTERN_LOL)));
        assertThat(matcher.match(":-D").pattern(), is(sameInstance(PATTERN_LOL)));
    }

    @Test
    public void testPatternHelp() {
        assertThat(matcher.match("help").pattern(), is(sameInstance(PATTERN_HELP)));
        assertThat(matcher.match("hi, I need help").pattern(), is(sameInstance(PATTERN_HELP)));
        assertThat(matcher.match("hi, I need help!").pattern(), is(sameInstance(PATTERN_HELP)));
    }

    @Test
    public void testPatternRandomQuote() {
        assertThat(matcher.match("quote").pattern(), is(sameInstance(PATTERN_RANDOM_QUOTE)));
        assertThat(matcher.match("random quote").pattern(), is(sameInstance(PATTERN_RANDOM_QUOTE)));
        assertThat(matcher.match("tell quote").pattern(), is(sameInstance(PATTERN_RANDOM_QUOTE)));
        assertThat(matcher.match("tell me a quote").pattern(), is(sameInstance(PATTERN_RANDOM_QUOTE)));
        assertThat(matcher.match("tell me a quote!").pattern(), is(sameInstance(PATTERN_RANDOM_QUOTE)));
        assertThat(matcher.match("tell me a quote bot").pattern(), is(sameInstance(PATTERN_RANDOM_QUOTE)));
        assertThat(matcher.match("hi bot, tell me a quote").pattern(), is(sameInstance(PATTERN_RANDOM_QUOTE)));
    }

    @Test
    public void testPatternRandomQuoteWithTag() {
        assertThat(matcher.match("quote tag hillary clinton").pattern(), is(sameInstance(PATTERN_RANDOM_QUOTE_WITH_TAG)));
        assertThat(matcher.match("quote tag hillary clinton").group("tag"), is(equalTo("hillary clinton")));
        assertThat(matcher.match("quote with tag hillary clinton").pattern(), is(sameInstance(PATTERN_RANDOM_QUOTE_WITH_TAG)));
        assertThat(matcher.match("quote with tag hillary clinton").group("tag"), is(equalTo("hillary clinton")));
        assertThat(matcher.match("hi, tell me a quote with tag hillary clinton").pattern(), is(sameInstance(PATTERN_RANDOM_QUOTE_WITH_TAG)));
        assertThat(matcher.match("hi, tell me a quote with tag hillary clinton").group("tag"), is(equalTo("hillary clinton")));
        assertThat(matcher.match("hi, tell me a quote tagged hillary clinton").pattern(), is(sameInstance(PATTERN_RANDOM_QUOTE_WITH_TAG)));
        assertThat(matcher.match("hi, tell me a quote tagged hillary clinton").group("tag"), is(equalTo("hillary clinton")));
        assertThat(matcher.match("hi, tell me a quote tagged with hillary clinton").pattern(), is(sameInstance(PATTERN_RANDOM_QUOTE_WITH_TAG)));
        assertThat(matcher.match("hi, tell me a quote tagged with hillary clinton").group("tag"), is(equalTo("hillary clinton")));
        assertThat(matcher.match("hi, tell me a quote about hillary clinton").pattern(), is(sameInstance(PATTERN_RANDOM_QUOTE_WITH_TAG)));
        assertThat(matcher.match("hi, tell me a quote about hillary clinton").group("tag"), is(equalTo("hillary clinton")));
    }

    @Test
    public void testPatternSearchQuote() {
        assertThat(matcher.match("search quote containing money").pattern(), is(sameInstance(PATTERN_SEARCH_QUOTE)));
        assertThat(matcher.match("search quote containing money").group("query"), is(equalTo("money")));
        assertThat(matcher.match("search quote with money").pattern(), is(sameInstance(PATTERN_SEARCH_QUOTE)));
        assertThat(matcher.match("search quote with money").group("query"), is(equalTo("money")));
        assertThat(matcher.match("hi, find me a quote with money").pattern(), is(sameInstance(PATTERN_SEARCH_QUOTE)));
        assertThat(matcher.match("hi, find me a quote with money").group("query"), is(equalTo("money")));
        assertThat(matcher.match("hi, find me a quote about money").pattern(), is(sameInstance(PATTERN_SEARCH_QUOTE)));
        assertThat(matcher.match("hi, find me a quote about money").group("query"), is(equalTo("money")));
    }

    @Test
    public void testPatternAnotherQuote() {
        assertThat(matcher.match("another").pattern(), is(sameInstance(PATTERN_ANOTHER_QUOTE)));
        assertThat(matcher.match("again").pattern(), is(sameInstance(PATTERN_ANOTHER_QUOTE)));
        assertThat(matcher.match("tell me another one").pattern(), is(sameInstance(PATTERN_ANOTHER_QUOTE)));
        assertThat(matcher.match("haha, tell me another!").pattern(), is(sameInstance(PATTERN_ANOTHER_QUOTE)));
    }

    @Test
    public void testPatternTags() {
        assertThat(matcher.match("tags").pattern(), is(sameInstance(PATTERN_TAGS)));
        assertThat(matcher.match("list tags").pattern(), is(sameInstance(PATTERN_TAGS)));
        assertThat(matcher.match("hi, show me the tags!").pattern(), is(sameInstance(PATTERN_TAGS)));
    }
}
