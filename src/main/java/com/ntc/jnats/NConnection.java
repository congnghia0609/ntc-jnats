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

package com.ntc.jnats;

import com.ntc.configer.NConfig;
import io.nats.client.Connection;
import io.nats.client.Connection.Status;
import io.nats.client.ConnectionListener;
import io.nats.client.Consumer;
import io.nats.client.ErrorListener;
import io.nats.client.Nats;
import io.nats.client.Options;
import java.io.IOException;
import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeoutException;

/**
 *
 * @author nghiatc
 * @since Dec 19, 2019
 */
public class NConnection {
    private String url;
    private Options opt;
    private Connection connection;

    public NConnection() throws IOException, InterruptedException {
        this.url = Options.DEFAULT_URL;
        this.opt = new Options.Builder().server(this.url)
                                .connectionName("NConnection_" + UUID.randomUUID().toString())
                                .maxReconnects(-1).build();
        this.connection = Nats.connect(this.opt);
    }

    public NConnection(String url, Options opt) throws IOException, InterruptedException {
        this.url = url;
        this.opt = opt;
        this.connection = Nats.connect(this.opt);
    }
    
    public NConnection(String name) throws IOException, InterruptedException {
        this.url = NConfig.getConfig().getString(name+".url", Options.DEFAULT_URL);
        String auth = NConfig.getConfig().getString(name+".auth", "");
        String username = "";
        String password = "";
        if (!auth.isEmpty()) {
            String[] arrauth = auth.split(":");
            if (arrauth.length == 2) {
                username = arrauth[0];
                password = arrauth[1];
            }
        }
        long timeout = NConfig.getConfig().getLong(name+".timeout", 0);
        Duration connnectTimeout = timeout > 0 ? Duration.ofSeconds(timeout) : Options.DEFAULT_CONNECTION_TIMEOUT;
        Options.Builder b = new Options.Builder()
                                .server(this.url)
                                .connectionName(name + "_" + UUID.randomUUID().toString())
                                .connectionTimeout(connnectTimeout) // Set the timeout for connection attempts.
                                .maxReconnects(-1) // Use -1 to turn on infinite reconnects.
                                .errorListener(new ErrorListener(){
                                    @Override
                                    public void exceptionOccurred(Connection conn, Exception exp) {
                                        System.out.println("Exception " + exp.getMessage());
                                    }

                                    @Override
                                    public void errorOccurred(Connection conn, String type) {
                                        System.out.println("Error " + type);
                                    }

                                    @Override
                                    public void slowConsumerDetected(Connection conn, Consumer consumer) {
                                        System.out.println("Slow consumer");
                                    }
                                })
                                .connectionListener(new ConnectionListener(){
                                    @Override
                                    public void connectionEvent(Connection conn, Events type) {
                                        System.out.println("Status change "+type);
                                    }
                                });
        if (!username.isEmpty() && !password.isEmpty()) {
            b = b.userInfo(username.toCharArray(), password.toCharArray());
        }
        this.opt = b.build();
        this.connection = Nats.connect(this.opt);
    }

    public String getUrl() {
        return url;
    }

    public Options getOpt() {
        return opt;
    }

    public Connection getConnection() {
        return connection;
    }
    
    public void close() throws InterruptedException {
        this.connection.close();
    }
    
    public CompletableFuture<Boolean> drain() throws TimeoutException, InterruptedException {
        return this.connection.drain(Duration.ZERO); // The time to wait for the drain to succeed, pass 0 to wait forever.
    }
    
    public void flush() throws TimeoutException, InterruptedException {
        this.connection.flush(Duration.ZERO); // The time to wait for the flush to succeed, pass 0 to wait forever.
    }
    
    public Status status() {
        return this.connection.getStatus();
    }
}
