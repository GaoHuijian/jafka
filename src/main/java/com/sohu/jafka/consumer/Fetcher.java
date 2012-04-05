/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sohu.jafka.consumer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

import org.apache.log4j.Logger;

import com.github.zkclient.ZkClient;
import com.sohu.jafka.cluster.Cluster;
import com.sohu.jafka.common.annotations.ClientSide;



/**
 * @author adyliu (imxylz@gmail.com)
 * @since 2012-4-5
 */
@ClientSide
public class Fetcher {

    private final ConsumerConfig config;
    private final ZkClient zkClient;
    
    private final Logger logger = Logger.getLogger(Fetcher.class);

    private volatile List<FetcherRunnable> fetcherThreads = new ArrayList<FetcherRunnable>(0);
    public Fetcher(ConsumerConfig config, ZkClient zkClient) {
        super();
        this.config = config;
        this.zkClient = zkClient;
    }
    
    public void stopConnectionsToAllBrokers() {
        // shutdown the old fetcher threads, if any
        List<FetcherRunnable> threads = this.fetcherThreads;
        for (FetcherRunnable fetcherThread : threads) {
            try {
                fetcherThread.shutdown();
            } catch (InterruptedException e) {
                logger.warn(e.getMessage(),e);
            }
        }
        this.fetcherThreads = new ArrayList<FetcherRunnable>(0);
    }

    
    public <T> void startConnections(Iterable<PartitionTopicInfo> topicInfos,Cluster cluster,//
            Map<String,List<MessageStream<T>>> messageStreams){
        if(topicInfos == null) {
            return;
        }
        
        //re-arrange by broker id
        Map<Integer, List<PartitionTopicInfo>> m = new HashMap<Integer, List<PartitionTopicInfo>>();
        for (PartitionTopicInfo info : topicInfos) {
            if (cluster.getBroker(info.brokerId) == null) {
                throw new IllegalStateException("Broker " + info.brokerId + " is unavailable, fetchers could not be started");
            }
            List<PartitionTopicInfo> list = m.get(info.brokerId);
            if (list == null) {
                list = new ArrayList<PartitionTopicInfo>();
                m.put(info.brokerId, list);
            }
            list.add(info);
        }
        //
        final List<FetcherRunnable> fetcherThreads = new ArrayList<FetcherRunnable>();
        for(Map.Entry<Integer, List<PartitionTopicInfo>> e:m.entrySet()) {
            FetcherRunnable fetcherThread = new FetcherRunnable("FetchRunnable-", //
                    zkClient, //
                    config, //
                    cluster.getBroker(e.getKey().intValue()), //
                    e.getValue());
            fetcherThreads.add(fetcherThread);
            fetcherThread.start();
        }
        //
        this.fetcherThreads = fetcherThreads;
    }
    
    public <T> void clearFetcherQueues(Collection<BlockingQueue<FetchedDataChunk>> queuesToBeCleared, Collection<List<MessageStream<T>>> messageStreamsList) {
        for (BlockingQueue<FetchedDataChunk> q : queuesToBeCleared) {
            q.clear();
        }
        //
        for (List<MessageStream<T>> messageStreams : messageStreamsList) {
            for (MessageStream<T> ms : messageStreams) {
                ms.clear();
            }
        }
    }
}
