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

package com.ntc.jnats.npub;


import com.ntc.jnats.NConnection;
import io.nats.client.Connection.Status;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author nghiatc
 * @since Dec 19, 2019
 */
public class NPub {
    private static final Logger log = LoggerFactory.getLogger(NPub.class);
    private static Map<String, NPub> mapNPublisher = new ConcurrentHashMap<>();
    private static Lock lock = new ReentrantLock();
    private NConnection nConn;

    private NPub() {
    }
    
    public NPub(String name) throws IOException, InterruptedException {
        this.nConn = new NConnection(name);
    }
    
    public static NPub getInstance(String name) {
        if(name == null || name.isEmpty()) {
            return null;
        }
        NPub instance = mapNPublisher.containsKey(name) ? mapNPublisher.get(name) : null;
		if(instance == null) {
			lock.lock();
			try {
                instance = mapNPublisher.containsKey(name) ? mapNPublisher.get(name) : null;
				if(instance == null) {
					instance = new NPub(name);
                    mapNPublisher.put(name, instance);
				} else {
                    if (!instance.isOpen()) {
                        instance.close();
                        instance = new NPub(name);
                        mapNPublisher.put(name, instance);
                    }
                }
			} catch (Exception e) {
                log.error(e.getMessage(), e);
            } finally {
				lock.unlock();
			}
		} else {
            if (!instance.isOpen()) {
                lock.lock();
                try {
                    instance.close();
                    instance = new NPub(name);
                    mapNPublisher.put(name, instance);
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                } finally {
                    lock.unlock();
                }
            }
        }
		return instance;
	}

    public NConnection getNConn() {
        return nConn;
    }
    
    public boolean isOpen() {
        return this.nConn.getConnection().getStatus() == Status.CONNECTED;
    }
    
    public void close() throws InterruptedException {
        this.nConn.close();
    }
    
    public void publish(String subject, String msg) {
        this.nConn.getConnection().publish(subject, msg.getBytes(StandardCharsets.UTF_8));
    }
}
