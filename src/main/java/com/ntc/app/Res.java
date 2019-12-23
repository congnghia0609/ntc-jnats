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

import com.ntc.jnats.nres.NRes;
import com.ntc.jnats.nres.NResGroup;
import io.nats.client.Message;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author nghiatc
 * @since Dec 23, 2019
 */
public class Res {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            NResGroup resGroup = new NResGroup();
            for (int i=0; i<2; i++) {
                NResQueryDB res = new NResQueryDB("res-db");
                resGroup.add(res);
            }
            resGroup.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static class NResQueryDB extends NRes {
        private final Logger log = LoggerFactory.getLogger(NResQueryDB.class);
        private String reply = "this is response ==> ";

        public NResQueryDB(String name) throws IOException, InterruptedException {
            super(name);
        }

        @Override
        public void execute(Message msg) {
            try {
                String data = new String(msg.getData(), StandardCharsets.UTF_8);
                log.info("NRes["+getGroup()+"] Received on QueueNRes["+getSubject()+"]: '"+data+"'");
                String datares = reply + data;
                reply(msg, datares);
                log.info("NRes["+getGroup()+"] Reply on QueueNRes["+getSubject()+"]: '"+datares+"'");
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
    }
}
