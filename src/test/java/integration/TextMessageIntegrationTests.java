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

package integration;

import com.restfb.types.send.ButtonTemplatePayload;
import com.restfb.types.send.PostbackButton;
import com.restfb.types.send.QuickReply;
import io.tronalddump.app.facebook.messenger.callback.TronaldDumpCallbackHandler;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.collection.IsIn.isOneOf;

/**
 * Integration tests for {@link TronaldDumpCallbackHandler}.
 *
 * @author Marcel Overdijk
 */
public class TextMessageIntegrationTests extends AbstractTronaldDumpCallbackHandlerIntegrationTests {

    @Test
    public void testHiTextMessages() {
        onMessage("hi");
        verifyTextMessage(is("Hi there!"));
    }

    @Test
    public void testHeyTextMessages() {
        onMessage("hey");
        verifyTextMessage(is("Hi there!"));
    }

    @Test
    public void testWhatIsYourNameTextMessages() {
        onMessage("what is your name");
        verifyTextMessage(is("Donald Trump is the name - the most stupid president ever!"));
    }

    @Test
    public void testHowAreYouTextMessages() {
        onMessage("how are you");
        verifyTextMessage(isOneOf("I'm great!", "I'm doing great!"));
    }

    @Test
    public void testHowAreYouDoingTextMessages() {
        onMessage("how are you doing?");
        verifyTextMessage(isOneOf("I'm great!", "I'm doing great!"));
    }

    @Test
    public void testTellMeAQuoteTextMessages() {
        onMessage("tell me a quote");
        verifyTextMessage(is("An 'extremely credible source' has called my office and told me that Barack Obama's birth certificate is a fraud."));
    }

    @Test
    public void testQuoteTextMessages() {
        onMessage("quote");
        verifyTextMessage(is("An 'extremely credible source' has called my office and told me that Barack Obama's birth certificate is a fraud."));
    }

    @Test
    public void testTellMeAnotherTextMessages() {
        onMessage("tell me another");
        verifyTextMessage(is("An 'extremely credible source' has called my office and told me that Barack Obama's birth certificate is a fraud."));
    }

    @Test
    public void testAnotherTextMessages() {
        onMessage("another");
        verifyTextMessage(is("An 'extremely credible source' has called my office and told me that Barack Obama's birth certificate is a fraud."));
    }

    @Test
    public void testAgainTextMessages() {
        onMessage("again");
        verifyTextMessage(is("An 'extremely credible source' has called my office and told me that Barack Obama's birth certificate is a fraud."));
    }

    @Test
    public void testTellMeAQuoteWithTagTextMessages() {
        onMessage("tell me a quote with tag tag1");
        verifyTextMessage(is("I don't think Ivanka would do that inside the magazine. Although she does have a very nice figure. I've said that if Ivanka weren't my daughter, perhaps I would be dating her."));
    }

    @Test
    public void testQuoteWithTagTextMessages() {
        onMessage("quote with tag tag1");
        verifyTextMessage(is("I don't think Ivanka would do that inside the magazine. Although she does have a very nice figure. I've said that if Ivanka weren't my daughter, perhaps I would be dating her."));
    }

    @Test
    public void testQuoteTagTextMessages() {
        onMessage("quote tag tag1");
        verifyTextMessage(is("I don't think Ivanka would do that inside the magazine. Although she does have a very nice figure. I've said that if Ivanka weren't my daughter, perhaps I would be dating her."));
    }

    @Test
    public void testQuoteTaggedTextMessages() {
        onMessage("quote tagged tag1");
        verifyTextMessage(is("I don't think Ivanka would do that inside the magazine. Although she does have a very nice figure. I've said that if Ivanka weren't my daughter, perhaps I would be dating her."));
    }

    @Test
    public void testQuoteTaggedWithTextMessages() {
        onMessage("quote tagged with tag1");
        verifyTextMessage(is("I don't think Ivanka would do that inside the magazine. Although she does have a very nice figure. I've said that if Ivanka weren't my daughter, perhaps I would be dating her."));
    }

    @Test
    public void testSearchQuoteWithQueryTextMessages() {
        onMessage("search quote with money");
        verifyTextMessage(is("Money was never a big motivation for me, except as a way to keep score."));
    }

    @Test
    public void testSearchMeAQuoteWithQueryTextMessages() {
        onMessage("search me a quote with money");
        verifyTextMessage(is("Money was never a big motivation for me, except as a way to keep score."));
    }

    @Test
    public void testSearchAQuoteWithQueryTextMessages() {
        onMessage("search a quote with money");
        verifyTextMessage(is("Money was never a big motivation for me, except as a way to keep score."));
    }

    @Test
    public void testFindQuoteContainingQueryTextMessages() {
        onMessage("find quote containing money");
        verifyTextMessage(is("Money was never a big motivation for me, except as a way to keep score."));
    }

    @Test
    public void testLolTextMessages() {
        onMessage("lol");
        verifyTextMessage(is("I'm laughing my ass off!"));
    }

    @Test
    public void testFunnyTextMessages() {
        onMessage("funny");
        verifyTextMessage(is("I'm laughing my ass off!"));
    }

    @Test
    public void testLaughingOutLoudFunnyTextMessages() {
        onMessage("laughing out loud");
        verifyTextMessage(is("I'm laughing my ass off!"));
    }

    @Test
    public void testHelpTextMessage() {
        onMessage("help");
        ButtonTemplatePayload buttonTemplate = new ButtonTemplatePayload(
                "Hi there. I can tell you random quotes of Donald Trump. Ask me things like the following:" +
                        "\n" +
                        "\n  - Tell me a quote" +
                        "\n  - Tell me a quote tagged with hillary clinton" +
                        "\n  - Search quote containing money" +
                        "\n  - List available tags" +
                        "\n" +
                        "\nOr choose a command below.");
        buttonTemplate.addButton(new PostbackButton("Random Quote", "RANDOM_QUOTE"));
        buttonTemplate.addButton(new PostbackButton("Tags", "TAGS"));
        verifyButtonTemplate(is(buttonTemplate));
    }

    @Test
    public void testTagsTextMessages() {
        onMessage("tags");
        List<QuickReply> quickReplies = Arrays.asList(
                new QuickReply("tag1", "RANDOM_QUOTE_WITH_TAG_tag1"),
                new QuickReply("tag2", "RANDOM_QUOTE_WITH_TAG_tag2"),
                new QuickReply("tag3", "RANDOM_QUOTE_WITH_TAG_tag3"),
                new QuickReply("tag4", "RANDOM_QUOTE_WITH_TAG_tag4"),
                new QuickReply("tag5", "RANDOM_QUOTE_WITH_TAG_tag5"),
                new QuickReply("tag6", "RANDOM_QUOTE_WITH_TAG_tag6"),
                new QuickReply("More...", "TAGS_MORE_2")

        );
        verifyQuickReplies("Choose a tag:", is(quickReplies));
    }

    @Test
    public void testListTagsTextMessages() {
        onMessage("list tags");
        List<QuickReply> quickReplies = Arrays.asList(
                new QuickReply("tag1", "RANDOM_QUOTE_WITH_TAG_tag1"),
                new QuickReply("tag2", "RANDOM_QUOTE_WITH_TAG_tag2"),
                new QuickReply("tag3", "RANDOM_QUOTE_WITH_TAG_tag3"),
                new QuickReply("tag4", "RANDOM_QUOTE_WITH_TAG_tag4"),
                new QuickReply("tag5", "RANDOM_QUOTE_WITH_TAG_tag5"),
                new QuickReply("tag6", "RANDOM_QUOTE_WITH_TAG_tag6"),
                new QuickReply("More...", "TAGS_MORE_2")

        );
        verifyQuickReplies("Choose a tag:", is(quickReplies));
    }

    @Test
    public void testGobbledygookTextMessage() {
        onMessage("gobbledygook");
        verifyTextMessage(is("OK! Ask me something else or type 'help'."));
    }
}
