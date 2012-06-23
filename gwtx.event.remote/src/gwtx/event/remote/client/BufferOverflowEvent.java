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
package gwtx.event.remote.client;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;


/**
 * Fired when the event source is attached to the browser's document or detached
 * from it.
 * 
 * @author Dann Martens 
 */
public class BufferOverflowEvent extends GwtEvent<BufferOverflowEvent.Handler> {

	/**
	 * Implemented by objects that handle {@link BufferOverflowEvent}.
	 */
	public interface Handler extends EventHandler {

		void onConnectionTimeout(BufferOverflowEvent event);

	}

	public interface HasHandlers {

		/**
		 * Adds a {@link Handler} handler for {@link BufferOverflowEvent}
		 * events.
		 * 
		 * @param handler
		 *            the handler
		 * @return the registration for the event
		 */
		HandlerRegistration addBufferOverflowHandler(Handler handler);
	}

	/**
	 * The event type.
	 */
	static Type<BufferOverflowEvent.Handler> TYPE = new Type<BufferOverflowEvent.Handler>();

	private RemoteEventBus remoteEventBus;

	/**
	 * Construct a new {@link BufferOverflowEvent}.
	 */
	public BufferOverflowEvent(RemoteEventBus remoteEventBus) {
		this.remoteEventBus = remoteEventBus;
	}

	@Override
	public final Type<BufferOverflowEvent.Handler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(BufferOverflowEvent.Handler handler) {
		handler.onConnectionTimeout(this);
	}

}
