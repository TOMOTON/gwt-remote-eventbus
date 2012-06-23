/**
 * Licensed to TOMOTON nv under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  TOMOTON nv licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package gwtx.event.remote.server;

import gwtx.event.remote.shared.RemoteGwtEvent;

import java.io.Serializable;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;


/**
 * Implementation of a server-side remote event bus session.
 * 
 * @author Dann Martens
 */
class RemoteEventBusSession implements Session, Serializable {

	/* Managed UID. */
	private static final long serialVersionUID = 1L;

	private long lastSequence = -1;
	
	private volatile boolean invalidated = false;
	
	private Set<String> typeIdSet = Collections.newSetFromMap(new ConcurrentHashMap<String, Boolean>());
	
	public long getLastSequence() {
		return lastSequence;
	}

	public boolean isSubscribed(RemoteGwtEvent.Type<?> type) {
		return typeIdSet.contains(type.getTypeId());
	}

	public void updateSequence(long lastSequence) {
		this.lastSequence = lastSequence;
	}
	
	public boolean subscribe(RemoteGwtEvent.Type<?> type) {
		return typeIdSet.add(type.getTypeId());
	}

	public boolean unsubscribe(RemoteGwtEvent.Type<?> type) {
		return typeIdSet.remove(type.getTypeId());
	}
	
	public void invalidate() {
		invalidated = true;
	}
	
	public boolean isInvalidated() {
		return invalidated;
	}

}