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

/**
 * Integration tests for {@link TronaldDumpCallbackHandler}.
 *
 * @author Marcel Overdijk
 */
public class PostbackIntegrationTests extends AbstractTronaldDumpCallbackHandlerIntegrationTests {

    @Test
    public void testGetStartedPostback() {
        onPostback("GET_STARTED");
        List<QuickReply> quickReplies = Arrays.asList(
                new QuickReply("Random Quote", "RANDOM_QUOTE"),
                new QuickReply("Tags", "TAGS")
        );
        verifyQuickReplies("Hi, what would you like to hear?", is(quickReplies));
    }

    @Test
    public void testHelpPostback() {
        onPostback("HELP");
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
    public void testTagsPostback() {
        onPostback("TAGS");
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
    public void testTagsMorePostback() {
        onPostback("TAGS_MORE_2");
        List<QuickReply> quickReplies = Arrays.asList(
                new QuickReply("tag7", "RANDOM_QUOTE_WITH_TAG_tag7"),
                new QuickReply("tag8", "RANDOM_QUOTE_WITH_TAG_tag8"),
                new QuickReply("tag9", "RANDOM_QUOTE_WITH_TAG_tag9"),
                new QuickReply("tag10", "RANDOM_QUOTE_WITH_TAG_tag10"),
                new QuickReply("tag11", "RANDOM_QUOTE_WITH_TAG_tag11"),
                new QuickReply("tag12", "RANDOM_QUOTE_WITH_TAG_tag12"),
                new QuickReply("More...", "TAGS_MORE_3")

        );
        verifyQuickReplies("Choose a tag:", is(quickReplies));
    }

    @Test
    public void testRandomQuotePostback() {
        onPostback("RANDOM_QUOTE");
        verifyTextMessage(is("An 'extremely credible source' has called my office and told me that Barack Obama's birth certificate is a fraud."));
    }

    @Test
    public void testRandomQuoteWithTagPostback() {
        onPostback("RANDOM_QUOTE_WITH_TAG_tag1");
        verifyTextMessage(is("I don't think Ivanka would do that inside the magazine. Although she does have a very nice figure. I've said that if Ivanka weren't my daughter, perhaps I would be dating her."));
    }
}
