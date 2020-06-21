/*
 * Copyright 2019 nghiatc.
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
package com.ntc.jnats.nsub;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author nghiatc
 * @since Dec 20, 2019
 */
public class NSubGroup {

    private final Logger log = LoggerFactory.getLogger(NSubGroup.class);
    private List<NSub> subscribers = new LinkedList<NSub>();

    public NSubGroup() {
    }

    public List<NSub> getSubscribers() {
        return subscribers;
    }

    public void add(NSub subscriber) {
        subscribers.add(subscriber);
    }

    public int start() {
        try {
            for (NSub sub : subscribers) {
                startNSub(sub);
            }
        } catch (Exception e) {
            log.error("NSubGroup.start: " + e.getMessage(), e);
            System.out.println("NSubGroup start error !!!");
            return -1;
        }
        System.out.println("NSubGroup start successfully !!!");
        return 0;
    }

    private int startNSub(NSub sub) {
        try {
            ExecutorService executor = Executors.newFixedThreadPool(1);
            executor.execute(sub);
        } catch (Exception e) {
            log.error("NSubGroup.startNSub fail...", e);
            return -1;
        }
        return 0;
    }
}
