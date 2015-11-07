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
import com.google.gwt.event.shared.HasHandlers;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.HandlerRegistration;

public class AutoSessionEvent extends GwtEvent<AutoSessionEvent.Handler> {

	/**
	 * Implemented by objects that handle {@link AutoSessionEvent}.
	 */
	public interface Handler extends EventHandler {
		void onSessionChange(AutoSessionEvent event);
	}

	/**
	 * The event type.
	 */
	static Type<AutoSessionEvent.Handler> TYPE;
	
	/**
     * Register a handler for NavigationEvent events on the eventbus.
     * 
     * @param eventBus the {@link EventBus}
     * @param handler an {@link AutoSessionEvent.Handler} instance
     * @return an {@link HandlerRegistration} instance
     */
    public static HandlerRegistration register(EventBus eventBus, AutoSessionEvent.Handler handler) {
    	return eventBus.addHandler(getType(), handler);
    }    	

	/**
	 * Fires an {@link AutoSessionEvent} on all registered handlers in the handler
	 * source.
	 * 
	 * @param <S> The handler source type
	 * @param source the source of the handlers
	 * @param destination the navigation destination
	 */
	public static <S extends HasHandlers> void fire(S source, boolean connected) {
		if (TYPE != null) {
			AutoSessionEvent event = new AutoSessionEvent(connected);
			source.fireEvent(event);
		}
	}

	/**
	 * Ensures the existence of the handler hook and then returns it.
	 * 
	 * @return returns a handler hook
	 */
	public static Type<AutoSessionEvent.Handler> getType() {
		if (TYPE == null) {
			TYPE = new Type<AutoSessionEvent.Handler>();
		}
		return TYPE;
	}

	private final boolean inSession;
	
	private final boolean new_;

	public AutoSessionEvent(boolean inSession) {
		this(inSession, false);
	}
	
	public AutoSessionEvent(boolean inSession, boolean new_) {
		this.inSession = inSession;
		this.new_ = new_;
	}

	@Override
	public final Type<AutoSessionEvent.Handler> getAssociatedType() {
		return TYPE;
	}

	/**
	 * Gets the session state.
	 */
	public boolean isInSession() {
		return inSession;
	}

	public boolean isNew() {
		return new_;
	}

	@Override
	public String toDebugString() {
		assertLive();
		return super.toDebugString() + " inSession = " + inSession;
	}

	@Override
	protected void dispatch(AutoSessionEvent.Handler handler) {
		handler.onSessionChange(this);
	}

}