/*
 * COPIED FROM APACHE LUCENE 4.7.2
 *
 * Git URL: git@github.com:apache/lucene.git, tag: releases/lucene-solr/4.7.2, path: lucene/core/src/java
 *
 * (see https://issues.apache.org/jira/browse/OAK-10786 for details)
 */

package org.apache.lucene.index;

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.IOException;

/** A {@link MergeScheduler} that simply does each merge
 *  sequentially, using the current thread. */
public class SerialMergeScheduler extends MergeScheduler {

  /** Sole constructor. */
  public SerialMergeScheduler() {
  }

  /** Just do the merges in sequence. We do this
   * "synchronized" so that even if the application is using
   * multiple threads, only one merge may run at a time. */
  @Override
  synchronized public void merge(IndexWriter writer) throws IOException {

    while(true) {
      MergePolicy.OneMerge merge = writer.getNextMerge();
      if (merge == null)
        break;
      writer.merge(merge);
    }
  }

  @Override
  public void close() {}
}
