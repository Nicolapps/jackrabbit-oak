/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.jackrabbit.oak.plugins.document.prefetch;

import java.util.LinkedList;

import org.apache.jackrabbit.oak.plugins.document.Collection;
import org.apache.jackrabbit.oak.plugins.document.DocumentNodeState;
import org.apache.jackrabbit.oak.plugins.document.DocumentNodeStore;
import org.apache.jackrabbit.oak.plugins.document.util.Utils;

public class CacheWarming {

    private final DocumentNodeStore documentNodeStore;

    public CacheWarming(DocumentNodeStore documentNodeStore) {
        this.documentNodeStore = documentNodeStore;
    }

    public void prefetch(java.util.Collection<String> paths, DocumentNodeState rootState) {
        if ( (paths == null) || paths.isEmpty()) {
            return;
        }
        prefetchDocumentStore(paths, rootState);
    }

    /**
     * This would be about pre-warming the DocumentStore
     * @param paths
     */
    private void prefetchDocumentStore(java.util.Collection<String> paths, DocumentNodeState rootState) {
        java.util.Collection<String> ids = new LinkedList<>();
        for (String aPath : paths) {
            if (!documentNodeStore.isCached(aPath, rootState)) {
                String id = Utils.getIdFromPath(aPath);
                ids.add(id);
            }
        }
        documentNodeStore.getDocumentStore().prefetch(Collection.NODES, ids);
    }

    /* caches
     *
     *  DocumentNodeStore:
        nodeStore.getDiffCache().invalidateAll();
        nodeStore.getNodeCache().invalidateAll();
        nodeStore.getNodeChildrenCache().invalidateAll();
     *
     *
     *  MongoDocumentStore:
     *      private final NodeDocumentCache nodesCache;
     */

    /*
    two parts:
        1. fill the MongoDocumentStore.nodesCache
        2. fill the DocumentNodeStore.nodeState cache
     */
/*    private DocumentNodeState readNode(Path path, RevisionVector readRevision) {
        String id = Utils.getIdFromPath(path);
        Revision lastRevision = getPendingModifications().get(path);
        NodeDocument doc = store.find(Collection.NODES, id);
        if (doc == null) {
            return null;
        }
        final DocumentNodeState result = doc.getNodeAtRevision(this, readRevision, lastRevision);
        return result;
    }*/
}
