/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.jackrabbit.oak.index.indexer.document.tree.store.utils;

import java.util.LinkedHashMap;
import java.util.Map;

public class MemoryBoundCache<K, V extends MemoryBoundCache.MemoryObject>
    extends LinkedHashMap<K, V> {

    private static final long serialVersionUID = 1L;
    private long maxMemoryBytes;
    private long memoryUsed;

    public MemoryBoundCache(long maxMemoryBytes) {
        super(16, 0.75f, true);
        this.maxMemoryBytes = maxMemoryBytes;
    }

    public void setSize(int maxMemoryBytes) {
        this.maxMemoryBytes = maxMemoryBytes;
    }

    @Override
    public V put(K key, V value) {
        V old = super.put(key, value);
        if (old != null) {
            memoryUsed -= old.estimatedMemory();
        }
        if (value != null) {
            memoryUsed += value.estimatedMemory();
        }
        return old;
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> map) {
        for(Map.Entry<? extends K, ? extends V> e : map.entrySet()) {
            put(e.getKey(), e.getValue());
        }
    }

    @Override
    public V remove(Object key ) {
        V old = super.remove(key);
        if (old != null) {
            memoryUsed -= old.estimatedMemory();
        }
        return old;
    }

    @Override
    public void clear() {
        super.clear();
        memoryUsed = 0;
    }

    @Override
    public boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        boolean removeEldest = size() > maxMemoryBytes;
        if (removeEldest) {
            memoryUsed -= eldest.getValue().estimatedMemory();
        }
        return removeEldest;
    }

    public interface MemoryObject {
        /**
         * Get the estimate memory size. The value must not change afterwards, otherwise
         * the memory calculation is wrong.
         *
         * @return the memory in bytes
         */
        long estimatedMemory();
    }

}