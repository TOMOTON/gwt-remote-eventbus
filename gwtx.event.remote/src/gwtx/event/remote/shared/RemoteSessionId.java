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
package gwtx.event.remote.shared;

import java.io.Serializable;


/**
 * An object which identifies a remote event bus session for a particular 
 * source.
 * 
 * @author Dann Martens
 */
public class RemoteSessionId implements Serializable {

	/* Managed UID. */
	private static final long serialVersionUID = 1L;
	
	private ServerId serverId;
	
	private SourceId sourceId;
	
	@SuppressWarnings("unused")
	private RemoteSessionId() {
		//? Manadatory bean constructor;
	}
	
	public RemoteSessionId(ServerId serverId, SourceId sourceId) {
		this.serverId = serverId;
		this.sourceId = sourceId;
	}

	public ServerId getServerId() {
		return serverId;
	}

	public SourceId getSourceId() {
		return sourceId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((serverId == null) ? 0 : serverId.hashCode());
		result = prime * result + ((sourceId == null) ? 0 : sourceId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RemoteSessionId other = (RemoteSessionId) obj;
		if (serverId == null) {
			if (other.serverId != null)
				return false;
		} else if (!serverId.equals(other.serverId))
			return false;
		if (sourceId == null) {
			if (other.sourceId != null)
				return false;
		} else if (!sourceId.equals(other.sourceId))
			return false;
		return true;
	}

}
