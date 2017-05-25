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

import org.edgexfoundry.controller.NotificationClient;
import org.edgexfoundry.controller.NotificationClientImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import org.edgexfoundry.support.domain.notifications.Notification;
import org.edgexfoundry.support.domain.notifications.NotificationCategory;
import org.edgexfoundry.support.domain.notifications.NotificationSeverity;
import org.edgexfoundry.test.category.RequiresMongoDB;
import org.edgexfoundry.test.category.RequiresSupportNotificationsRunning;

@Category({ RequiresMongoDB.class, RequiresSupportNotificationsRunning.class })
public class NotificationsClientTest {

	private static final String ENDPT = "http://localhost:48060/api/v1/notification";

	private static final String TEST_SLUG = "TEST_SLUG.NAME";
	private static final String TEST_SENDER = "edgex_some_service";
	private static final NotificationCategory TEST_CATEGORY = NotificationCategory.SW_HEALTH;
	private static final NotificationSeverity TEST_SEVERITY = NotificationSeverity.NORMAL;
	private static final String TEST_CONTENT = "test content";
	private static final String TEST_DESCRIPTION = "test description";
	private static final String[] TEST_LABELS = { "test", "edgex", "normal" };

	private NotificationClient client;

	@Before
	public void setup() throws Exception {
		client = new NotificationClientImpl();
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
	public void testAddNotification() {
		Notification notification = getNotification();
		String newslug = client.receiveNotification(notification);
		assertNotNull("Add of notification did not return a slug", newslug);
		assertEquals("Slug returned does not equal slug name provided", TEST_SLUG, newslug);
		assertTrue("Delete of notification did not happen", client.deleteBySlug(TEST_SLUG));
	}

	private Notification getNotification() {
		Notification n = new Notification();
		n.setSlug(TEST_SLUG);
		n.setCategory(TEST_CATEGORY);
		n.setContent(TEST_CONTENT);
		n.setDescription(TEST_DESCRIPTION);
		n.setLabels(TEST_LABELS);
		n.setSender(TEST_SENDER);
		n.setSeverity(TEST_SEVERITY);
		return n;
	}

}
