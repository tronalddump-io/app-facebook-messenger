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

import com.github.marsbits.restfbmessenger.Messenger;
import com.github.marsbits.restfbmessenger.send.SendOperations;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.util.Modules;
import com.restfb.types.send.ButtonTemplatePayload;
import com.restfb.types.send.IdMessageRecipient;
import com.restfb.types.send.QuickReply;
import com.restfb.types.webhook.messaging.MessageItem;
import com.restfb.types.webhook.messaging.MessagingItem;
import com.restfb.types.webhook.messaging.MessagingParticipant;
import com.restfb.types.webhook.messaging.PostbackItem;
import io.tronalddump.app.facebook.messenger.cache.TagsCache;
import io.tronalddump.app.facebook.messenger.callback.TronaldDumpCallbackHandler;
import io.tronalddump.app.facebook.messenger.inject.AppModule;
import io.tronalddump.client.Page;
import io.tronalddump.client.Pageable;
import io.tronalddump.client.Quote;
import io.tronalddump.client.TronaldClient;
import org.apache.commons.collections4.ListUtils;
import org.hamcrest.Matcher;
import org.junit.Before;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.hamcrest.MockitoHamcrest.argThat;

/**
 * Abstract helper class for {@link TronaldDumpCallbackHandler} integration tests.
 *
 * @author Marcel Overdijk
 */
public abstract class AbstractTronaldDumpCallbackHandlerIntegrationTests {

	protected TagsCache tagsCache;
	protected TronaldClient tronaldClient;
	protected Messenger messenger;
	protected SendOperations sendOperations;

	protected TronaldDumpCallbackHandler callbackHandler;

	protected String senderId;
	protected IdMessageRecipient recipient;

	@Before
	public void setUp() {

		this.senderId = "12345";
		this.recipient = new IdMessageRecipient(senderId);

		this.tagsCache = mock(TagsCache.class);
		this.tronaldClient = mock(TronaldClient.class);
		this.messenger = mock(Messenger.class);
		this.sendOperations = mock(SendOperations.class);

		Injector injector = Guice.createInjector(Modules
				.override(new AppModule())
				.with(new AbstractModule() {

					@Override
					protected void configure() {
						bind(TagsCache.class).toInstance(tagsCache);
						bind(TronaldClient.class).toInstance(tronaldClient);
						bind(Messenger.class).toInstance(messenger);
					}
				}));
		this.callbackHandler = injector.getInstance(TronaldDumpCallbackHandler.class);

		when(messenger.send()).thenReturn(sendOperations);

		List<String> tags;
		tags = new ArrayList<>();
		for (int i = 1; i <= 20; i++) {
			tags.add("tag" + i);
		}
		when(tagsCache.getTags()).thenReturn(tags);

		when(tagsCache.getTagsPaged(6)).thenReturn(
				ListUtils.partition(tags, 6));

		Quote quote = new Quote();
		quote.setValue("An 'extremely credible source' has called my office and told me that Barack Obama's birth certificate is a fraud.");
		when(tronaldClient.getRandomQuote()).thenReturn(quote);

		Quote quoteWithTag = new Quote();
		quoteWithTag.setValue(
				"I don't think Ivanka would do that inside the magazine. Although she does have a very nice figure. I've said that if Ivanka weren't my daughter, perhaps I would be dating her.");
		when(tronaldClient.getRandomQuote("tag1")).thenReturn(quoteWithTag);

		Quote quoteFromSearch = new Quote();
		quoteFromSearch.setValue("Money was never a big motivation for me, except as a way to keep score.");
		Page<Quote> page = new Page<>(Arrays.asList(quoteFromSearch), new Pageable(1, 1), 1);
		when(tronaldClient.search("money")).thenReturn(page);
	}

	protected void onMessage(String message) {
		MessagingItem messaging = createMessagingItemWithText(senderId, message);
		callbackHandler.onMessage(messenger, messaging);
	}

	protected void onPostback(String payload) {
		MessagingItem messaging = createMessagingItemWithPostback(senderId, payload);
		callbackHandler.onPostback(messenger, messaging);
	}

	protected MessagingItem createMessagingItemWithPostback(String senderId, String payload) {
		MessagingParticipant sender = new MessagingParticipant();
		sender.setId(senderId);
		PostbackItem postback = new PostbackItem();
		postback.setPayload(payload);
		MessagingItem messagingItem = new MessagingItem();
		messagingItem.setSender(sender);
		messagingItem.setPostback(postback);
		return messagingItem;
	}

	protected MessagingItem createMessagingItemWithText(String senderId, String text) {
		MessagingParticipant sender = new MessagingParticipant();
		sender.setId(senderId);
		MessageItem message = new MessageItem();
		message.setText(text);
		MessagingItem messagingItem = new MessagingItem();
		messagingItem.setSender(sender);
		messagingItem.setMessage(message);
		return messagingItem;
	}

	protected void verifyButtonTemplate(Matcher<ButtonTemplatePayload> matcher) {
		verify(sendOperations).buttonTemplate(eq(recipient), argThat(matcher));
	}

	protected void verifyQuickReplies(String text, Matcher<List<QuickReply>> matcher) {
		verify(sendOperations).quickReplies(eq(recipient), eq(text), argThat(matcher));
	}

	protected void verifyTextMessage(Matcher<String> matcher) {
		verify(sendOperations).textMessage(eq(recipient), argThat(matcher));
	}
}
