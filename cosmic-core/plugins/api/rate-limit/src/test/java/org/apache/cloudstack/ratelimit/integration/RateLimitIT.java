// Licensed to the Apache Software Foundation (ASF) under one
// or more contributor license agreements.  See the NOTICE file
// distributed with this work for additional information
// regarding copyright ownership.  The ASF licenses this file
// to you under the Apache License, Version 2.0 (the
// "License"); you may not use this file except in compliance
// the License.  You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// KIND, either express or implied.  See the License for the
// specific language governing permissions and limitations
// under the License.
package org.apache.cloudstack.ratelimit.integration;

import com.cloud.utils.exception.CloudRuntimeException;
import org.apache.cloudstack.api.response.ApiLimitResponse;
import org.apache.cloudstack.api.response.SuccessResponse;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Test fixture to do integration rate limit test.
 * Currently we commented out this test suite since it requires a real MS and Db running.
 */
public class RateLimitIT extends APITest {

    private static final int apiMax = 25;         // assuming ApiRateLimitService set api.throttling.max = 25

    @Before
    public void setup() {
        // always reset count for each testcase
        login("admin", "password");

        // issue reset api limit calls
        final HashMap<String, String> params = new HashMap<>();
        params.put("response", "json");
        params.put("sessionkey", sessionKey);
        final String resetResult = sendRequest("resetApiLimit", params);
        assertNotNull("Reset count failed!", fromSerializedString(resetResult, SuccessResponse.class));

    }

    @Test
    public void testNoApiLimitOnRootAdmin() throws Exception {
        // issue list Accounts calls
        final HashMap<String, String> params = new HashMap<>();
        params.put("response", "json");
        params.put("listAll", "true");
        params.put("sessionkey", sessionKey);
        // assuming ApiRateLimitService set api.throttling.max = 25
        final int clientCount = 26;
        final Runnable[] clients = new Runnable[clientCount];
        final boolean[] isUsable = new boolean[clientCount];

        final CountDownLatch startGate = new CountDownLatch(1);

        final CountDownLatch endGate = new CountDownLatch(clientCount);

        for (int i = 0; i < isUsable.length; ++i) {
            final int j = i;
            clients[j] = new Runnable() {

                /**
                 * {@inheritDoc}
                 */
                @Override
                public void run() {
                    try {
                        startGate.await();

                        sendRequest("listAccounts", params);

                        isUsable[j] = true;

                    } catch (final CloudRuntimeException e) {
                        isUsable[j] = false;
                        e.printStackTrace();
                    } catch (final InterruptedException e) {
                        e.printStackTrace();
                    } finally {
                        endGate.countDown();
                    }
                }
            };
        }

        final ExecutorService executor = Executors.newFixedThreadPool(clientCount);

        for (final Runnable runnable : clients) {
            executor.execute(runnable);
        }

        startGate.countDown();

        endGate.await();

        int rejectCount = 0;
        for (int i = 0; i < isUsable.length; ++i) {
            if (!isUsable[i])
                rejectCount++;
        }

        assertEquals("No request should be rejected!", 0, rejectCount);

    }

    @Test
    public void testApiLimitOnUser() throws Exception {
        // log in using normal user
        login("demo", "password");
        // issue list Accounts calls
        final HashMap<String, String> params = new HashMap<>();
        params.put("response", "json");
        params.put("listAll", "true");
        params.put("sessionkey", sessionKey);

        final int clientCount = apiMax + 1;
        final Runnable[] clients = new Runnable[clientCount];
        final boolean[] isUsable = new boolean[clientCount];

        final CountDownLatch startGate = new CountDownLatch(1);

        final CountDownLatch endGate = new CountDownLatch(clientCount);

        for (int i = 0; i < isUsable.length; ++i) {
            final int j = i;
            clients[j] = new Runnable() {

                /**
                 * {@inheritDoc}
                 */
                @Override
                public void run() {
                    try {
                        startGate.await();

                        sendRequest("listAccounts", params);

                        isUsable[j] = true;

                    } catch (final CloudRuntimeException e) {
                        isUsable[j] = false;
                        e.printStackTrace();
                    } catch (final InterruptedException e) {
                        e.printStackTrace();
                    } finally {
                        endGate.countDown();
                    }
                }
            };
        }

        final ExecutorService executor = Executors.newFixedThreadPool(clientCount);

        for (final Runnable runnable : clients) {
            executor.execute(runnable);
        }

        startGate.countDown();

        endGate.await();

        int rejectCount = 0;
        for (int i = 0; i < isUsable.length; ++i) {
            if (!isUsable[i])
                rejectCount++;
        }

        assertEquals("Only one request should be rejected!", 1, rejectCount);

    }

    @Test
    public void testGetApiLimitOnUser() throws Exception {
        // log in using normal user
        login("demo", "password");

        // issue an api call
        final HashMap<String, String> params = new HashMap<>();
        params.put("response", "json");
        params.put("listAll", "true");
        params.put("sessionkey", sessionKey);
        sendRequest("listAccounts", params);

        // issue get api limit calls
        final HashMap<String, String> params2 = new HashMap<>();
        params2.put("response", "json");
        params2.put("sessionkey", sessionKey);
        final String getResult = sendRequest("getApiLimit", params2);
        final ApiLimitResponse getLimitResp = (ApiLimitResponse) fromSerializedString(getResult, ApiLimitResponse.class);
        assertEquals("Issued api count is incorrect!", 2, getLimitResp.getApiIssued()); // should be 2 apis issues plus this getlimit api
        assertEquals("Allowed api count is incorrect!", apiMax - 2, getLimitResp.getApiAllowed());
    }
}
