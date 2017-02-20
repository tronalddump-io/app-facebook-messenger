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

package io.tronalddump.app.facebook.messenger.callback;

import com.github.marsbits.restfbmessenger.Messenger;
import com.github.marsbits.restfbmessenger.webhook.AbstractCallbackHandler;
import com.restfb.types.send.ButtonTemplatePayload;
import com.restfb.types.send.IdMessageRecipient;
import com.restfb.types.send.PostbackButton;
import com.restfb.types.send.QuickReply;
import com.restfb.types.webhook.messaging.MessageItem;
import com.restfb.types.webhook.messaging.MessagingItem;
import com.restfb.types.webhook.messaging.PostbackItem;
import com.rivescript.RiveScript;
import io.tronalddump.client.TronaldClient;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;

/**
 * The Tronald Dump IO {@code CallbackHandler}.
 *
 * @author Marcel Overdijk
 */
public class TronaldDumpCallbackHandler extends AbstractCallbackHandler {

    private static final Logger logger = Logger.getLogger(TronaldDumpCallbackHandler.class.getName());

    private final TronaldClient tronaldClient;
    private final RiveScript bot;

    public TronaldDumpCallbackHandler(final TronaldClient tronaldClient, final RiveScript bot) {
        this.tronaldClient = requireNonNull(tronaldClient, "'tronaldClient' must not be null");
        this.bot = requireNonNull(bot, "'bot' must not be null");
    }

    @Override
    public void onMessage(Messenger messenger, MessagingItem messaging) {
        String senderId = messaging.getSender().getId();
        IdMessageRecipient recipient = new IdMessageRecipient(senderId);
        MessageItem message = messaging.getMessage();
        logger.info(format("Message received from %s: %s", senderId, message));
        messenger.send().markSeen(recipient);
        sendReply(messenger, senderId, message.getQuickReply() != null ? message.getQuickReply().getPayload() : message.getText());
    }

    @Override
    public void onPostback(Messenger messenger, MessagingItem messaging) {
        String senderId = messaging.getSender().getId();
        IdMessageRecipient recipient = new IdMessageRecipient(senderId);
        PostbackItem postback = messaging.getPostback();
        logger.info(format("Postback received from %s: %s", senderId, postback));
        messenger.send().markSeen(recipient);
        sendReply(messenger, senderId, postback.getPayload());
    }

    private void sendReply(Messenger messenger, String senderId, String message) {
        IdMessageRecipient recipient = new IdMessageRecipient(senderId);
        messenger.send().typingOn(recipient);
        try {
            // Get a reply from the RiveScript engine.
            String reply = bot.reply(senderId, message);
            logger.info("Got reply: " + reply);
            // If the reply contains QUICK_REPLIES send them to the user.
            if (reply.contains("QUICK_REPLIES:")) {
                Pattern p = Pattern.compile("(.*)QUICK_REPLIES:(.+)", Pattern.DOTALL);
                Matcher m = p.matcher(reply);
                if (m.find()) {
                    String text = m.group(1);
                    List<QuickReply> quickReplies = new ArrayList<>();
                    Map<String, String> payloads = getPayloadMappings(m.group(2));
                    for (Map.Entry<String, String> entry : payloads.entrySet()) {
                        String title = entry.getKey();
                        String payload = entry.getValue();
                        QuickReply quickReply = new QuickReply(title, payload);
                        quickReplies.add(quickReply);
                    }
                    messenger.send().quickReplies(recipient, text, quickReplies);
                }
                // If the reply contains BUTTONS send them to the user.
            } else if (reply.contains("BUTTONS:")) {
                Pattern p = Pattern.compile("(.*)BUTTONS:(.+)", Pattern.DOTALL);
                Matcher m = p.matcher(reply);
                if (m.find()) {
                    String text = m.group(1);
                    ButtonTemplatePayload buttonTemplate = new ButtonTemplatePayload(text);
                    Map<String, String> payloads = getPayloadMappings(m.group(2));
                    for (Map.Entry<String, String> entry : payloads.entrySet()) {
                        String title = entry.getKey();
                        String postbackPayload = entry.getValue();
                        PostbackButton button = new PostbackButton(title, postbackPayload);
                        buttonTemplate.addButton(button);
                    }
                    messenger.send().buttonTemplate(recipient, buttonTemplate);
                }
                // Otherwise simple send back the reply to the user.
            } else {
                messenger.send().textMessage(recipient, reply);
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Something went wrong:", e);
            messenger.send().textMessage(recipient, "Something went wrong. If it is your fault, and probably it is, Chuck Norris will find you and roundhouse kick your butt!");
        }
        messenger.send().typingOff(recipient);
    }

    /**
     * Converts a {@link String} with payload mappings ({@code title=payload, title=payload, ..}) to a {@link Map<String, String>}.
     * E.g. {@code Random Quote=RANDOM_QUOTE, Tags=TAGS} is converted to map like
     * {@code ["Random Quote": "RANDOM_QUOTE", "Tags": "TAGS"]}.
     */
    private Map<String, String> getPayloadMappings(String str) {
        LinkedHashMap<String, String> payloads = new LinkedHashMap<>();
        String[] mappings = str.split(",");
        for (String mapping : mappings) {
            String[] temp = mapping.split("=", 2);
            String title = temp[0].trim();
            String payload = temp[1].trim();
            payloads.put(title, payload);
        }
        return payloads;
    }
}
