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
 * An object which identifies a remote event bus client source.
 * 
 * @author Dann Martens
 */
public class SourceId implements Serializable {

	/* Managed UID. */
	private static final long serialVersionUID = 1L;
	
	private int id;
	
	@SuppressWarnings("unused")
	private SourceId() {
		//? Mandatory bean constructor.
	}
	
	public SourceId(int id) {
		this.id = id;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
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
		SourceId other = (SourceId) obj;
		if (id != other.id)
			return false;
		return true;
	}
	
	public String asString() {
		return String.valueOf(id);
	}

}
