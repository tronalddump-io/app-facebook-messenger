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

import com.github.marsbits.restfbmessenger.Messenger;
import com.github.marsbits.restfbmessenger.webhook.AbstractCallbackHandler;
import com.restfb.types.User;
import com.restfb.types.send.ButtonTemplatePayload;
import com.restfb.types.send.IdMessageRecipient;
import com.restfb.types.send.PostbackButton;
import com.restfb.types.send.QuickReply;
import com.restfb.types.webhook.messaging.MessageItem;
import com.restfb.types.webhook.messaging.MessagingItem;
import com.restfb.types.webhook.messaging.PostbackItem;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.StrSubstitutor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.tronalddump.client.Quote;
import io.tronalddump.client.TronaldClient;

import static java.lang.String.format;
import static org.apache.commons.lang3.StringUtils.EMPTY;

/**
 * The Tronald Dump IO {@code CallbackHandler}.
 *
 * @author Marcel Overdijk
 */
public class TronaldDumpCallbackHandler extends AbstractCallbackHandler {

    private static final Logger logger = Logger.getLogger(TronaldDumpCallbackHandler.class.getName());

    public static final String PAYLOAD_GET_STARTED = "GET_STARTED";
    public static final String PAYLOAD_RANDOM_QUOTE = "RANDOM_QUOTE";
    public static final String PAYLOAD_RANDOM_QUOTE_WITH_TAG = "RANDOM_QUOTE_WITH_TAG";
    public static final String PAYLOAD_TAGS = "TAGS";
    public static final String PAYLOAD_TAGS_MORE = "TAGS_MORE";
    public static final String PAYLOAD_HELP = "HELP";
    public static final String PAYLOAD_SEPARATOR = ":";

    public static final int TAGS_PAGE_SIZE = 6;

    private TronaldClient tronaldClient = new TronaldClient();

    private TagsCache tagsCache = new TagsCache();

    private MessageTextMatcher messageTextMatcher = new MessageTextMatcher();

    @Override
    public void onMessage(Messenger messenger, MessagingItem messaging) {
        String senderId = messaging.getSender().getId();
        IdMessageRecipient recipient = new IdMessageRecipient(senderId);
        MessageItem message = messaging.getMessage();

        logger.info(format("Message received from %s: %s", senderId, message));

        messenger.send().markSeen(recipient);

        if (message.getQuickReply() != null) {
            String payload = message.getQuickReply().getPayload();
            handlePayload(messenger, senderId, payload);
        } else {
            String messageText = message.getText();
            if (StringUtils.isNotBlank(messageText)) {
                handleMessageText(messenger, senderId, messageText);
            } else {
                sendMisunderstand(messenger, senderId);
            }
        }
    }

    @Override
    public void onPostback(Messenger messenger, MessagingItem messaging) {
        String senderId = messaging.getSender().getId();
        IdMessageRecipient recipient = new IdMessageRecipient(senderId);
        PostbackItem postback = messaging.getPostback();

        logger.info(format("Postback received from %s: %s", senderId, postback));

        messenger.send().markSeen(recipient);
        handlePayload(messenger, senderId, postback.getPayload());
    }

    private void handleMessageText(Messenger messenger, String senderId, String messageText) {
        Matcher match = messageTextMatcher.match(messageText);
        if (match == null) {
            if (tagsCache.containsIgnoreCase(messageText)) {
                sendRandomQuote(messenger, senderId, tagsCache.getTag(messageText));
            } else {
                sendMisunderstand(messenger, senderId);
            }
        } else {
            Pattern pattern = match.pattern();
            if (pattern == MessageTextMatcher.PATTERN_HI) {
                sendHi(messenger, senderId);
            } else if (pattern == MessageTextMatcher.PATTERN_WHAT_IS_YOUR_NAME) {
                sendMyName(messenger, senderId);
            } else if (pattern == MessageTextMatcher.PATTERN_HOW_ARE_YOU) {
                sendFeelingGreat(messenger, senderId);
            } else if (pattern == MessageTextMatcher.PATTERN_LOL) {
                sendFunny(messenger, senderId);
            } else if (pattern == MessageTextMatcher.PATTERN_HELP) {
                sendHelp(messenger, senderId);
            } else if (pattern == MessageTextMatcher.PATTERN_RANDOM_QUOTE) {
                sendRandomQuote(messenger, senderId);
            } else if (pattern == MessageTextMatcher.PATTERN_RANDOM_QUOTE_WITH_TAG) {
                String tag = match.group("tag");
                sendRandomQuote(messenger, senderId, tag);
            } else if (pattern == MessageTextMatcher.PATTERN_SEARCH_QUOTE) {
                String query = match.group("query");
                sendSearchQuote(messenger, senderId, query);
            } else if (pattern == MessageTextMatcher.PATTERN_ANOTHER_QUOTE) {
                sendRandomQuote(messenger, senderId);
            } else if (pattern == MessageTextMatcher.PATTERN_TAGS) {
                sendTags(messenger, senderId);
            }
        }
    }

    private void handlePayload(Messenger messenger, String senderId, String payload) {
        switch (payload) {

            // first handle exact payloads

            case PAYLOAD_GET_STARTED:
                sendGetStarted(messenger, senderId);
                break;

            case PAYLOAD_RANDOM_QUOTE:
                sendRandomQuote(messenger, senderId);
                break;

            case PAYLOAD_TAGS:
                sendTags(messenger, senderId);
                break;

            case PAYLOAD_HELP:
                sendHelp(messenger, senderId);
                break;

            default:

                // handle dynamic payloads

                if (payload.startsWith(PAYLOAD_RANDOM_QUOTE_WITH_TAG + PAYLOAD_SEPARATOR)) {

                    String tag = StringUtils.replaceOnce(payload, PAYLOAD_RANDOM_QUOTE_WITH_TAG + PAYLOAD_SEPARATOR, EMPTY);
                    sendRandomQuote(messenger, senderId, tag);

                } else if (payload.startsWith(PAYLOAD_TAGS_MORE + PAYLOAD_SEPARATOR)) {

                    int pageNumber = 1;
                    try {
                        pageNumber = Integer.parseInt(StringUtils.replaceOnce(payload, PAYLOAD_TAGS_MORE + PAYLOAD_SEPARATOR, EMPTY));
                    } catch (Exception e) {
                        logger.severe(format("Unexpected exception parsing tags page number: ", e.getMessage()));
                    }
                    sendTags(messenger, senderId, pageNumber);

                } else {

                    logger.warning(format("Unknown payload received: %s", payload));
                    sendError(messenger, senderId);
                }
                break;
        }
    }

    private void sendMisunderstand(Messenger messenger, String senderId) {
        IdMessageRecipient recipient = new IdMessageRecipient(senderId);
        messenger.send().typingOn(recipient);
        messenger.send().textMessage(recipient, "I say many stupid things myself, but I don't understand your request either. Ask me something else or type 'help'.");
        messenger.send().typingOff(recipient);
    }

    private void sendGetStarted(Messenger messenger, String senderId) {
        IdMessageRecipient recipient = new IdMessageRecipient(senderId);
        messenger.send().typingOn(recipient);

        User user = messenger.getUserProfile(senderId);

        Map<String, Object> values = new HashMap<>();
        values.put("user_first_name", user.getFirstName());

        String textMessage = StrSubstitutor.replace("Hi ${user_first_name}, what would you like to hear?", values);

        messenger.send().quickReplies(recipient, textMessage, Arrays.asList(
                new QuickReply("Random Quote", PAYLOAD_RANDOM_QUOTE),
                new QuickReply("Tags", PAYLOAD_TAGS)));
        messenger.send().typingOff(recipient);
    }

    private void sendHi(Messenger messenger, String senderId) {
        IdMessageRecipient recipient = new IdMessageRecipient(senderId);
        messenger.send().typingOn(recipient);
        messenger.send().textMessage(recipient, "Hi there!");
        messenger.send().typingOff(recipient);
    }

    private void sendMyName(Messenger messenger, String senderId) {
        IdMessageRecipient recipient = new IdMessageRecipient(senderId);
        messenger.send().typingOn(recipient);
        messenger.send().textMessage(recipient, "Donald Trump is the name - the most stupid president ever!");
        messenger.send().typingOff(recipient);
    }

    private void sendFeelingGreat(Messenger messenger, String senderId) {
        IdMessageRecipient recipient = new IdMessageRecipient(senderId);
        messenger.send().typingOn(recipient);
        messenger.send().textMessage(recipient, "I'm feeling great - I will be the next president!");
        messenger.send().typingOff(recipient);
    }

    private void sendFunny(Messenger messenger, String senderId) {
        IdMessageRecipient recipient = new IdMessageRecipient(senderId);
        messenger.send().typingOn(recipient);
        messenger.send().textMessage(recipient, "I'm laughing my ass off!");
        messenger.send().typingOff(recipient);
    }

    private void sendRandomQuote(Messenger messenger, String senderId) {
        sendRandomQuote(messenger, senderId, null);
    }

    private void sendRandomQuote(Messenger messenger, String senderId, String tag) {
        IdMessageRecipient recipient = new IdMessageRecipient(senderId);
        messenger.send().typingOn(recipient);
        String textMessage;
        if (tag != null) {
            List<String> tags = tagsCache.getTags();
            if (tags.contains(tag)) {
                textMessage = tronaldClient.getRandomQuote(tag).getValue();
            } else {
                textMessage = format("I said so many stupid things but there is no tag '%s' yet. Type 'tags' to see available tags.", tag);
            }
        } else {
            textMessage = tronaldClient.getRandomQuote().getValue();
        }
        messenger.send().textMessage(recipient, textMessage);
        messenger.send().typingOff(recipient);
    }

    private void sendSearchQuote(Messenger messenger, String senderId, String query) {
        IdMessageRecipient recipient = new IdMessageRecipient(senderId);
        messenger.send().typingOn(recipient);
        String textMessage;
        List<Quote> quotes = tronaldClient.search(query).getContent();
        if (quotes != null && quotes.size() > 0) {
            int i = new Random().nextInt(quotes.size());
            textMessage = quotes.get(i).getValue();
        } else {
            textMessage = format("Yeah I know I said so many stupids things but your search for '%s' did not match any quote. Type 'tags' to see available tags or just search again with different keywords.", query);
        }
        messenger.send().textMessage(recipient, textMessage);
        messenger.send().typingOff(recipient);
    }

    private void sendTags(Messenger messenger, String senderId) {
        sendTags(messenger, senderId, 1);
    }

    private void sendTags(Messenger messenger, String senderId, int pageNumber) {
        IdMessageRecipient recipient = new IdMessageRecipient(senderId);
        messenger.send().typingOn(recipient);
        List<QuickReply> quickReplies = new ArrayList<>();
        List<List<String>> tagsPaged = tagsCache.getTagsPaged(TAGS_PAGE_SIZE);
        if (tagsPaged.size() < pageNumber) {
            pageNumber = 1;
        }
        List<String> tagsPage = tagsPaged.get(pageNumber - 1);
        for (int i = 0; i < tagsPage.size(); i++) {
            String tag = tagsPage.get(i);
            String title = tag;
            String payload = PAYLOAD_RANDOM_QUOTE_WITH_TAG + PAYLOAD_SEPARATOR + tag;
            quickReplies.add(new QuickReply(title, payload));
        }
        if (pageNumber < tagsPaged.size()) {
            quickReplies.add(new QuickReply("More...", PAYLOAD_TAGS_MORE + PAYLOAD_SEPARATOR + (pageNumber + 1)));
        }
        messenger.send().quickReplies(recipient, "Choose a tag:", quickReplies);
        messenger.send().typingOff(recipient);
    }

    private void sendHelp(Messenger messenger, String senderId) {
        IdMessageRecipient recipient = new IdMessageRecipient(senderId);
        messenger.send().typingOn(recipient);
        ButtonTemplatePayload buttonTemplate = new ButtonTemplatePayload(
                "Hi there. I can tell you random quotes of Donald Trump. Ask me things like the following:"
                        + "\n"
                        + "\n  • Tell me a quote"
                        + "\n  • Tell me a quote tagged with hillary clinton"
                        + "\n  • Search quote containing money"
                        + "\n  • List available tags"
                        + "\n  • Are you stupid"
                        + "\n"
                        + "\nOr choose a command below.");
        buttonTemplate.addButton(new PostbackButton("Random Quote", PAYLOAD_RANDOM_QUOTE));
        buttonTemplate.addButton(new PostbackButton("Tags", PAYLOAD_TAGS));
        messenger.send().buttonTemplate(recipient, buttonTemplate);
        messenger.send().typingOff(recipient);
    }

    private void sendError(Messenger messenger, String senderId) {
        IdMessageRecipient recipient = new IdMessageRecipient(senderId);
        messenger.send().typingOn(recipient);
        messenger.send().textMessage(recipient, "Probably because one of my stupid decisions something went wrong here. Try again.");
        messenger.send().typingOff(recipient);
    }
}
