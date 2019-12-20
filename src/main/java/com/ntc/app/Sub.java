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

import com.ntc.jnats.nsub.NSub;
import com.ntc.jnats.nsub.NSubGroup;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author nghiatc
 * @since Dec 20, 2019
 */
public class Sub {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            NSubGroup nsubGroup = new NSubGroup();
            for (int i=0; i< 2; i++) {
                NSubscriber ns = new NSubscriber("sub-notify");
                nsubGroup.add(ns);
            }
            nsubGroup.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static class NSubscriber extends NSub {
        private final Logger log = LoggerFactory.getLogger(NSubscriber.class);

        public NSubscriber(String name) throws IOException, InterruptedException {
            super(name);
        }
        
        @Override
        public void execute(byte[] data) {
            try {
                String msg = new String(data, StandardCharsets.UTF_8);
                log.info("NSubscriber received on PubSub ["+getSubject()+"]: '"+msg+"'");
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
        
    }

}
