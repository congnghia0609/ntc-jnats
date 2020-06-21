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
package com.ntc.jnats.nreq;

import com.ntc.configer.NConfig;
import com.ntc.jnats.NConnection;
import io.nats.client.Connection;
import io.nats.client.Message;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
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
public class NReq {

    private static final Logger log = LoggerFactory.getLogger(NReq.class);
    private static Map<String, NReq> mapNRequest = new ConcurrentHashMap<>();
    private static Lock lock = new ReentrantLock();
    private NConnection nConn;
    private Duration timeout = Duration.ofSeconds(60); // Default 60s.

    private NReq() {
    }

    public NReq(String name) throws IOException, InterruptedException {
        long timeout = NConfig.getConfig().getLong(name + ".timeout", 0L);
        if (timeout > 0) {
            this.timeout = Duration.ofSeconds(timeout);
        }
        this.nConn = new NConnection(name);
    }

    public static NReq getInstance(String name) {
        if (name == null || name.isEmpty()) {
            return null;
        }
        NReq instance = mapNRequest.containsKey(name) ? mapNRequest.get(name) : null;
        if (instance == null || !instance.isOpen()) {
            lock.lock();
            try {
                instance = mapNRequest.containsKey(name) ? mapNRequest.get(name) : null;
                if (instance == null) {
                    instance = new NReq(name);
                    mapNRequest.put(name, instance);
                } else if (!instance.isOpen()) {
                    instance.close();
                    instance = new NReq(name);
                    mapNRequest.put(name, instance);
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            } finally {
                lock.unlock();
            }
        }
        return instance;
    }

    public NConnection getNConn() {
        return nConn;
    }

    public Duration getTimeout() {
        return timeout;
    }

    public boolean isOpen() {
        return this.nConn.getConnection().getStatus() == Connection.Status.CONNECTED;
    }

    public void close() {
        try {
            this.nConn.close();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    public Message publish(String subject, String msg) {
        Message ret = null;
        try {
            if (!subject.isEmpty() && !msg.isEmpty()) {
                ret = this.nConn.getConnection().request(subject, msg.getBytes(StandardCharsets.UTF_8), this.timeout);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            return ret;
        }
    }

    public Message publish(String subject, String msg, Duration timeout) {
        Message ret = null;
        try {
            if (!subject.isEmpty() && !msg.isEmpty()) {
                ret = this.nConn.getConnection().request(subject, msg.getBytes(StandardCharsets.UTF_8), timeout);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            return ret;
        }
    }

    public Message publish(String subject, byte[] msg) {
        Message ret = null;
        try {
            if (!subject.isEmpty() && msg != null) {
                ret = this.nConn.getConnection().request(subject, msg, this.timeout);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            return ret;
        }
    }

    public Message publish(String subject, byte[] msg, Duration timeout) {
        Message ret = null;
        try {
            if (!subject.isEmpty() && msg != null) {
                ret = this.nConn.getConnection().request(subject, msg, timeout);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            return ret;
        }
    }

    public CompletableFuture<Message> publishAsyn(String subject, String msg) {
        CompletableFuture<Message> ret = null;
        try {
            if (!subject.isEmpty() && !msg.isEmpty()) {
                ret = this.nConn.getConnection().request(subject, msg.getBytes(StandardCharsets.UTF_8));
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            return ret;
        }
    }

    public CompletableFuture<Message> publishAsyn(String subject, byte[] msg) {
        CompletableFuture<Message> ret = null;
        try {
            if (!subject.isEmpty() && msg != null) {
                ret = this.nConn.getConnection().request(subject, msg);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            return ret;
        }
    }
}
