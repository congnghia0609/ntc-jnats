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

package com.ntc.app;

import com.ntc.jnats.nworker.NWorker;
import com.ntc.jnats.nworker.NWorkerGroup;
import io.nats.client.Message;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author nghiatc
 * @since Dec 20, 2019
 */
public class Worker {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            NWorkerGroup workerGroup = new NWorkerGroup();
            for (int i=0; i<2; i++) {
                NWorkerEmail nw = new NWorkerEmail("worker-email");
                workerGroup.add(nw);
            }
            workerGroup.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static class NWorkerEmail extends NWorker {
        private final Logger log = LoggerFactory.getLogger(NWorkerEmail.class);

        public NWorkerEmail(String name) throws IOException, InterruptedException {
            super(name);
        }
        
        @Override
        public void execute(Message msg) {
            try {
                String data = new String(msg.getData(), StandardCharsets.UTF_8);
                log.info("NWorkerEmail["+getGroup()+"] received on QueueWorker["+getSubject()+"]: '"+data+"'");
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
    }
}
