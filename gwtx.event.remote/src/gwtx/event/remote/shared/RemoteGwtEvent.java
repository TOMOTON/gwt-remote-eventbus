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

import com.google.gwt.event.shared.GwtEvent;


/**
 * Base transfer object interface for remote events. To implement a remote 
 * event, a <code>AbstractRemoteGwtEvent</code should be used, which already
 * implements this interface.
 * 
 * @author Dann Martens
 */
public interface RemoteGwtEvent<H> extends Serializable {

	public class Type<H> extends GwtEvent.Type<H> implements Serializable {

		/* Managed UID. */
		private static final long serialVersionUID = 1L;

		private String typeId;
		
		@SuppressWarnings("unused")
		private Type() {
			//? Mandatory bean constructor.
		}
		
		public Type(Class<?> class_) {
			this.typeId = class_.getName();
		}

		public String getTypeId() {
			return typeId;
		}
	
	};
	
	public GwtEvent.Type<H> getAssociatedType();
	
}
