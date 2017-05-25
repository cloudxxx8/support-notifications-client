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

import static org.junit.Assert.assertNotNull;

import java.lang.reflect.Field;
import java.util.List;

import org.edgexfoundry.controller.NotificationClient;
import org.edgexfoundry.controller.NotificationClientImpl;
import org.edgexfoundry.controller.TransmissionClient;
import org.edgexfoundry.controller.TransmissionClientImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import org.edgexfoundry.support.domain.notifications.Notification;
import org.edgexfoundry.support.domain.notifications.NotificationCategory;
import org.edgexfoundry.support.domain.notifications.NotificationSeverity;
import org.edgexfoundry.support.domain.notifications.Transmission;
import org.edgexfoundry.test.category.RequiresMongoDB;
import org.edgexfoundry.test.category.RequiresSupportNotificationsRunning;

@Category({ RequiresMongoDB.class, RequiresSupportNotificationsRunning.class })
public class TransmissionClientTest {

	private static final String NOTIFICATION_ENDPT = "http://localhost:48060/api/v1/notification";
	private static final String TRANSMISSION_ENDPT = "http://localhost:48060/api/v1/transmission";

	private static final String TEST_SLUG = "TEST4TRANSMISSION_SLUG.NAME";
	private static final String TEST_SENDER = "edgex_some_service";
	private static final NotificationCategory TEST_CATEGORY = NotificationCategory.SW_HEALTH;
	private static final NotificationSeverity TEST_SEVERITY = NotificationSeverity.NORMAL;
	private static final String TEST_CONTENT = "test content";
	private static final String TEST_DESCRIPTION = "test description";
	private static final String[] TEST_LABELS = { "test", "edgex", "normal" };
	
	private static final int TEST_LIMIT = 10;
	private static final long TEST_START = 1469175494521L;
	private static final long TEST_END = 1472439915731L;

	private NotificationClient notificationClient;
	private TransmissionClient transmissionClient;

	@Before
	public void setup() throws Exception {
		notificationClient = new NotificationClientImpl();
		transmissionClient = new TransmissionClientImpl();
		setURL();
		Notification notification = getNotification();
		notificationClient.receiveNotification(notification);
	}

	@After
	public void cleanup() {
		notificationClient.deleteBySlug(TEST_SLUG);
	}

	private void setURL() throws Exception {
		Class<?> nClientClass = notificationClient.getClass();
		Field nURLField = nClientClass.getDeclaredField("url");
		nURLField.setAccessible(true);
		nURLField.set(notificationClient, NOTIFICATION_ENDPT);
		
		Class<?> tClientClass = transmissionClient.getClass();
		Field tURLField = tClientClass.getDeclaredField("url");
		tURLField.setAccessible(true);
		tURLField.set(transmissionClient, TRANSMISSION_ENDPT);
	}

	@Test
	public void testFindByNotificationSlug() {
		List<Transmission> searchResult = transmissionClient.findByNotificationSlug(TEST_SLUG, TEST_LIMIT);
		assertNotNull("Find transmissions by notification slug cannot be reached", searchResult);
	}
	
	@Test
	public void testFindByCreatedDuration() {
		List<Transmission> searchResult = transmissionClient.findByCreatedDuration(TEST_START, TEST_END, TEST_LIMIT);
		assertNotNull("Find transmissions by created duration cannot be reached", searchResult);
	}

	@Test
	public void testFindByCreatedAfter() {
		List<Transmission> searchResult = transmissionClient.findByCreatedAfter(TEST_START, TEST_LIMIT);
		assertNotNull("Find transmissions by start time cannot be reached", searchResult);
	}
	
	@Test
	public void testFindByCreatedBefore() {
		List<Transmission> searchResult = transmissionClient.findByCreatedBefore(TEST_END, TEST_LIMIT);
		assertNotNull("Find transmissions by end time cannot be reached", searchResult);
	}
	
	@Test
	public void testFindEscalatedTransmissions() {
		List<Transmission> searchResult = transmissionClient.findEscalatedTransmissions(TEST_LIMIT);
		assertNotNull("Find escalated transmissions slug cannot be reached", searchResult);
	}
	
	@Test
	public void testFindFailedTransmissions() {
		List<Transmission> searchResult = transmissionClient.findFailedTransmissions(TEST_LIMIT);
		assertNotNull("Find failed transmissions cannot be reached", searchResult);
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
