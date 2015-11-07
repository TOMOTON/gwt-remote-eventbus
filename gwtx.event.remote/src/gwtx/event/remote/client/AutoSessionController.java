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

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.RepeatingCommand;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.web.bindery.event.shared.EventBus;

/**
 * Controlls remote event bus sessions in case of failure.
 * 
 * @author Dann Martens
 */
public class AutoSessionController implements ConnectionTimeoutEvent.Handler, BufferOverflowEvent.Handler, InvalidSessionEvent.Handler, FailureHandler {
	
	private EventBus eventBus;
	
	private RemoteEventBus remoteEventBus;

	private boolean inSession = false;

	public AutoSessionController(EventBus eventBus, RemoteEventBus remoteEventBus) {
		this.eventBus = eventBus;
		this.remoteEventBus = remoteEventBus;
	}

	public void start() {
		remoteEventBus.setFailureHandler(this);
		remoteEventBus.addBufferOverflowHandler(this);
		remoteEventBus.addConnectionTimeoutHandler(this);
		remoteEventBus.addInvalidSessionHandler(this);
		newSession();
	}
	
	private void scheduleResumeSession() {
		Console.log("[AutoSessionManager] Scheduled session resume 3000 ms from now.");
		Scheduler.get().scheduleFixedDelay(new RepeatingCommand() {
			@Override
			public boolean execute() {
				Console.log("[AutoSessionManager] RepeatingCommand.execute()");
				try {
					remoteEventBus.resumeSession(new AsyncCallback<Void>() {
						@Override
						public void onFailure(Throwable caught) {
							Console.log("[AutoSessionManager] Resume failed " + caught);
						}
						@Override
						public void onSuccess(Void result) {
							eventBus.fireEvent(new AutoSessionEvent(true));
						}
					});
				} catch (Exception e) {
					Window.alert("Illegal condition: resume failed!");
				}
				return false;
			}
		}, 3000);
	}

	private void scheduleNewSession() {
		Console.log("[AutoSessionManager] Scheduled new session 3000 ms from now.");
		Scheduler.get().scheduleFixedDelay(new RepeatingCommand() {
			@Override
			public boolean execute() {
				newSession();
				return false;
			}
		}, 3000);
	}
	
	private void newSession() {
		Console.log("[AutoSessionManager] .newSession()");
		remoteEventBus.newSession(new AsyncCallback<Void>() {
			@Override
			public void onFailure(Throwable caught) {
				Console.log("[AutoSessionManager] Couldn't start remote event bus session!");
				scheduleResumeSession();
			}
			@Override
			public void onSuccess(Void result) {
				Console.log("[AutoSessionManager] Started new session.");
				inSession = true;
				eventBus.fireEvent(new AutoSessionEvent(inSession));
			}
		});
	}
	
	@Override
	public void onConnectionTimeout(ConnectionTimeoutEvent event) {
		Console.log("[AutoSessionManager] Connection timed out!");
		inSession = false;
		eventBus.fireEvent(new AutoSessionEvent(inSession));
		scheduleResumeSession();
	}

	@Override
	public void onBufferOverflow(BufferOverflowEvent event) {
		Console.log("[AutoSessionManager] Buffer overflowed!");
		inSession = false;
		eventBus.fireEvent(new AutoSessionEvent(inSession));
		scheduleResumeSession();
	}

	@Override
	public void onInvalidSession(InvalidSessionEvent event) {
		Console.log("[AutoSessionManager] Invalid remote event bus session!");
		inSession = false;		
		eventBus.fireEvent(new AutoSessionEvent(inSession));
		scheduleNewSession();
	}
	
	@Override
	public void onFailure(Throwable caught) {
		Console.log("[AutoSessionManager] Remote event bus failed! (" + caught.getClass().getName() + ')');
		eventBus.fireEvent(new AutoSessionEvent(false));
		scheduleResumeSession();
	}
	
}
