/*******************************************************************************
 * Copyright 2016-2017 Dell Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * @microservice:  support-notifications-client library
 * @author: Cloud Tsai, Dell
 * @version: 1.0.0
 *******************************************************************************/
package org.edgexfoundry.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Field;

import org.edgexfoundry.controller.SubscriptionClient;
import org.edgexfoundry.controller.SubscriptionClientImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import org.edgexfoundry.support.domain.notifications.Channel;
import org.edgexfoundry.support.domain.notifications.NotificationCategory;
import org.edgexfoundry.support.domain.notifications.Subscription;
import org.edgexfoundry.test.category.RequiresMongoDB;
import org.edgexfoundry.test.category.RequiresSupportNotificationsRunning;

@Category({ RequiresMongoDB.class, RequiresSupportNotificationsRunning.class })
public class SubscriptionClientTest {

	private static final String ENDPT = "http://localhost:48060/api/v1/subscription";

	private static final String TEST_SLUG = "TEST_SLUG.SUBSCRIPTION.NAME";
	private static final String TEST_RECEIVER = "edgex_some_receiver";
	private static final String TEST_DESCRIPTION = "test description";
	private static final NotificationCategory[] TEST_SUBSCRIBED_CATEGORIES = {NotificationCategory.SW_HEALTH, NotificationCategory.HW_HEALTH};
	private static final String[] TEST_SUBSCRIBED_LABELS = { "test", "edgex", "normal" };
	private static final Channel[] TEST_CHANNELS = {};	

	private SubscriptionClient client;

	@Before
	public void setup() throws Exception {
		client = new SubscriptionClientImpl();
		setURL();
	}

	@After
	public void cleanup() {
		// delete all slugs
	}

	private void setURL() throws Exception {
		Class<?> clientClass = client.getClass();
		Field temp = clientClass.getDeclaredField("url");
		temp.setAccessible(true);
		temp.set(client, ENDPT);
	}

	@Test
	public void testAddSubscription() {
		Subscription subscription = getSubscription();
		String newslug = client.createSubscription(subscription);
		assertNotNull("Add of subscription did not return a slug", newslug);
		assertEquals("Slug returned does not equal slug name provided", TEST_SLUG, newslug);
		
		Subscription newSubscription = client.findBySlug(newslug);
		assertNotNull("The created subscription cannot be found", newSubscription);
		assertEquals("The description of created subscription is not correct", TEST_DESCRIPTION, newSubscription.getDescription());
		
		assertTrue("Delete of subscription did not happen", client.deleteBySlug(TEST_SLUG));
	}

	private Subscription getSubscription() {
		Subscription s = new Subscription();
		s.setSlug(TEST_SLUG);
		s.setReceiver(TEST_RECEIVER);
		s.setDescription(TEST_DESCRIPTION);
		s.setSubscribedCategories(TEST_SUBSCRIBED_CATEGORIES);
		s.setSubscribedLabels(TEST_SUBSCRIBED_LABELS);
		s.setChannels(TEST_CHANNELS);
		return s;
	}

}
