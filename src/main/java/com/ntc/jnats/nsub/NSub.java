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

import com.ntc.configer.NConfig;
import com.ntc.jnats.NConnection;
import io.nats.client.Dispatcher;
import io.nats.client.Message;
import io.nats.client.MessageHandler;
import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author nghiatc
 * @since Dec 19, 2019
 */
public abstract class NSub implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(NSub.class);
    private String name;
    private String subject;
    private NConnection nconn;
    private Dispatcher dispatcher;

    public NSub(String name) throws IOException, InterruptedException {
        this.name = name;
        this.subject = NConfig.getConfig().getString(name+".subject", name);
        this.nconn = new NConnection(name);
    }

    public String getName() {
        return name;
    }

    public String getSubject() {
        return subject;
    }

    public NConnection getNconn() {
        return nconn;
    }

    public Dispatcher getDispatcher() {
        return dispatcher;
    }
    
    // Stop receive message SAFE.
    public CompletableFuture<Boolean> drain() throws InterruptedException {
        if (dispatcher != null) {
            return dispatcher.drain(Duration.ZERO); // The time to wait for the drain to succeed, pass 0 to wait forever.
        }
        return null;
    }
    
    // Stop receive message UNSAFE.
    public void unsubscribe() {
        if (dispatcher != null) {
            dispatcher.unsubscribe(subject);
        }
    }

    public abstract void execute(byte[] data);
    
    @Override
    public void run() {
        try {
            dispatcher = nconn.getConnection().createDispatcher(new MessageHandler() {
                @Override
                public void onMessage(Message msg) throws InterruptedException {
                    execute(msg.getData());
                }
            });
            dispatcher.subscribe(subject);
            nconn.getConnection().flush(Duration.ZERO);
            log.info("NSub["+subject+"] run successfully.");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
