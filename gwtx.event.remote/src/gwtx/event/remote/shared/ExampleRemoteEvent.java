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

import gwtx.event.remote.shared.AbstractRemoteGwtEvent;
import gwtx.event.remote.shared.RemoteGwtEvent;

import com.google.gwt.event.shared.EventHandler;


/**
 * Example remote event.
 * 
 * @author Dann Martens
 */
public class ExampleRemoteEvent extends AbstractRemoteGwtEvent<ExampleRemoteEvent.Handler> {
	
	private static final long serialVersionUID = 1L;
	
	public static final RemoteGwtEvent.Type<Handler> TYPE = new RemoteGwtEvent.Type<Handler>(ExampleRemoteEvent.class);
	
	public abstract interface Handler extends EventHandler {
		
		public abstract void onExample(ExampleRemoteEvent remoteEvent);
		
	}
	
	private String message;

	@SuppressWarnings("unused")
	private ExampleRemoteEvent() {
		//? Mandatory RPC constructor.
	}

	public ExampleRemoteEvent(String message) {
		if (message == null) {
			throw new NullPointerException("An message required!");
		}
		this.message = message;
	}

	public RemoteGwtEvent.Type<ExampleRemoteEvent.Handler> getAssociatedType() {
		return TYPE;
	}

	protected void dispatch(Handler handler) {
		handler.onExample(this);
	}

	public String getMessage() {
		return this.message;
	}

}
