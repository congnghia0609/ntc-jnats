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

import com.ntc.jnats.npub.NPub;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author nghiatc
 * @since Dec 19, 2019
 */
public class Pub {
    private static final Logger log = LoggerFactory.getLogger(Pub.class);

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
//        // Case 1: PubSub.
//        String subj = "msg.test";
//        for (int i=0; i<10; i++) {
//            String msg = "hello " + i;
//            NPub.getInstance("pub-notify").publish(subj, msg);
//            log.info("Published PubSub ["+subj+"] : '"+msg+"'");
//        }
        
        // Case 2: Worker Queue Group.
        String subj = "worker.email";
        for (int i=0; i<10; i++) {
            String msg = "hello " + i;
            NPub.getInstance("pub-notify").publish(subj, msg);
            log.info("Published PubSub ["+subj+"] : '"+msg+"'");
        }
    }
}
