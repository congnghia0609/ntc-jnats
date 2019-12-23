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

package com.ntc.jnats.nres;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author nghiatc
 * @since Dec 23, 2019
 */
public class NResGroup {
    private final Logger log = LoggerFactory.getLogger(NResGroup.class);
    private List<NRes> responces = new LinkedList<>();

    public NResGroup() {
    }

    public List<NRes> getResponces() {
        return responces;
    }
    
    public void add(NRes res) {
        responces.add(res);
    }
    
    public int start(){
        try {
            for(NRes res : responces){
                startNRes(res);
            }
        } catch (Exception e) {
            log.error("NResGroup.start: " + e.getMessage(), e);
            System.out.println("NResGroup start error !!!");
            return -1;
        }
        System.out.println("NResGroup start successfully !!!");
        return 0;
    }
    
    private int startNRes(NRes res){
        try {
            ExecutorService executor = Executors.newFixedThreadPool(1);
            executor.execute(res);
        } catch (Exception e) {
            log.error("NResGroup.startNRes fail...", e);
            return -1;
        }
        return 0;
    }
}
