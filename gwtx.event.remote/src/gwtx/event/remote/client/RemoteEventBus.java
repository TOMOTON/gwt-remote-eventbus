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

import gwtx.event.remote.shared.AbstractRemoteGwtEvent;
import gwtx.event.remote.shared.BufferOverflowException;
import gwtx.event.remote.shared.InvalidSessionException;
import gwtx.event.remote.shared.RemoteEventBusException;
import gwtx.event.remote.shared.RemoteGwtEvent;
import gwtx.event.remote.shared.RemoteSessionId;
import gwtx.event.remote.shared.SourceId;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.RequestTimeoutException;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.RpcRequestBuilder;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.rpc.StatusCodeException;
import com.google.web.bindery.event.shared.Event;


/**
 * Dispatches {@link Event}s to interested parties over the network. Eases 
 * decoupling by allowing objects to interact without having direct dependencies
 * upon one another, and without requiring event sources to deal with
 * maintaining handler lists. There will typically be one RemoteEventBus per
 * application, broadcasting events that may be of general interest. Events
 * can be fired both at the client-side, as well as at the server-side.
 * 
 * @see com.google.gwt.event.shared.EventBus
 * 
 * @author Dann Martens
 */
public class RemoteEventBus implements ConnectionTimeoutEvent.HasHandlers, BufferOverflowEvent.HasHandlers {

	public static final int DEFAULT_TIMEOUT_IN_MILLIS = 30000;
	
	private static final Variant VARIANT = GWT.create(Variant.class);
	
    private static class SilentAsyncCallback implements AsyncCallback<Void> {
    	
        public void onFailure(Throwable caught) {
        	Console.log("SilentAsyncCallback.onFailure (" + caught + ')');
        }

        public void onSuccess(Void result) {
        	Console.log("SilentAsyncCallback.onSuccess ");
        }
        
    }
	
	private static AsyncCallback<Void> DEFAULT_CALLBACK = new SilentAsyncCallback();
	
	private class RemoteHandlerRegistrationAdapter implements HandlerRegistration {

		private RemoteGwtEvent.Type<?> type;
		
		private HandlerRegistration registration;
		
		private boolean autoUnsubscribe = false;
		
		public RemoteHandlerRegistrationAdapter(RemoteGwtEvent.Type<?> type, HandlerRegistration registration, boolean autoUnsubscribe) {
			this.type = type;
			this.registration = registration;
			this.autoUnsubscribe = autoUnsubscribe;
		}

		public void removeHandler(final AsyncCallback<Void> callback) {
			registration.removeHandler();
			if(autoUnsubscribe && !handlerManager.isEventHandled(type)) {
				remoteEventService.removeSubscription(type, new AsyncCallback<Boolean>() {
					@Override
					public void onFailure(Throwable caught) {
						callback.onFailure(caught);
					}
					@Override
					public void onSuccess(Boolean result) {
						callback.onSuccess(null);
					}				
				});
			}
		}
		
		@Override
		public void removeHandler() {
			removeHandler(DEFAULT_CALLBACK);
		}

	}

	private RemoteEventServiceAsync remoteEventService;
	
	private SourceId sourceId;
	
	private AsyncCallback<Void> defaultCallback = DEFAULT_CALLBACK;

	private FailureHandler failureHandler = new FailureHandler() {
		@Override
		public void onFailure(Throwable caught) {
			if(caught instanceof StatusCodeException) {
				StatusCodeException sce = (StatusCodeException) caught;
				if(sce.getStatusCode() == 0) {
					Console.log("Silently closing with HTTP Status 0!");
					Console.log(String.valueOf(sce.getCause()));
				}
			} else {
				Window.alert("Getting events failed! " + caught);
			}
		}
	};
	
	private boolean autoUnsubscribe = true;
	
	private boolean scheduling = false;
	
	private int timeoutInMillis = DEFAULT_TIMEOUT_IN_MILLIS;
	
	private HandlerManager handlerManager = new HandlerManager(this);
	
	//private Set<RemoteGwtEvent.Type<?>> autoSubscribedSet = new HashSet<RemoteGwtEvent.Type<?>>();

	public RemoteEventBus() {
		RemoteEventServiceAsync rawService = GWT.create(RemoteEventService.class);
		remoteEventService = wrapRPCService(rawService);
	}
	
	public RemoteEventBus(AsyncCallback<Void> defaultCallback) {
		this();
		this.defaultCallback = defaultCallback;
	}

	public RemoteEventBus(FailureHandler failureHandler) {
		this();
		this.failureHandler = failureHandler;
	}

	public RemoteEventBus(AsyncCallback<Void> defaultCallback, FailureHandler failureHandler) {
		this();
		this.defaultCallback = defaultCallback;		
		this.failureHandler = failureHandler;
	}
	
	private <T> T wrapRPCService(T service) {
		if (service == null) 
			throw new NullPointerException("An initialized service object is required!");
		ServiceDefTarget target = (ServiceDefTarget) service;
		RpcRequestBuilder rpcRequestBuilder = new RpcRequestBuilder() {
			@Override
			protected RequestBuilder doCreate(String serviceEntryPoint) {
				RequestBuilder requestBuilder = super.doCreate(serviceEntryPoint);
				if(sourceId != null) {
					requestBuilder.setHeader("X-GWT-RemoteEventSource", sourceId.asString());
				}
				requestBuilder.setTimeoutMillis(timeoutInMillis);
				return requestBuilder;
			}
		};
		target.setRpcRequestBuilder(rpcRequestBuilder);
		return service;
	}
	
	public int getTimeout() {
		return timeoutInMillis;
	}

	public void setTimeout(int timeoutInMillis) {
		this.timeoutInMillis = timeoutInMillis;
	}
	
	public boolean isAutoUnsubscribe() {
		return autoUnsubscribe;
	}

	public void setAutoUnsubscribe(boolean autoUnsubscribe) {
		this.autoUnsubscribe = autoUnsubscribe;
	}

	public void newSession(final AsyncCallback<Void> callback) {
		if(scheduling) {
			throw new IllegalStateException("An existing session is still underway, invalidate existing session first!");
		}
		remoteEventService.newSession(new AsyncCallback<RemoteSessionId>() {
			@Override
			public void onFailure(Throwable caught) {
				callback.onFailure(caught);
			}

			@Override
			public void onSuccess(RemoteSessionId result) {
				sourceId = result.getSourceId();
				startScheduling();
				callback.onSuccess(null);
			}
		});
	}

	public void resumeSession(final AsyncCallback<Void> callback) {
		String location = GWT.getModuleBaseURL() + VARIANT.getPath() + VARIANT.getValue();
		Console.log("HTTP-HEAD " + location);
		RequestBuilder builder = new RequestBuilder(RequestBuilder.HEAD, URL.encode(location));
		try {
			builder.sendRequest(null, new RequestCallback() {
				public void onError(Request request, Throwable exception) {
					Console.log("HEAD request encountered an error: " + exception);
					callback.onFailure(exception);
				}
				public void onResponseReceived(Request request, Response response) {
					Console.log("HEAD response received " + response.getStatusCode());
					if (200 == response.getStatusCode()) {
						Console.log("Verified variant.");
						if (scheduling)
							scheduleGetAvailableEvents();
						callback.onSuccess(null);
					} else 
					if (404 == response.getStatusCode()) {						
						Console.log("Variant not found.");
						callback.onFailure(new VariantNotFoundException());
					} else {
						callback.onFailure(new RemoteEventBusException("An unknown problem occured checking the variant!"));
					}
				}
			});
		} catch (RequestException re) {
			callback.onFailure(re);
		}
	}
	
	public void invalidateSession(final AsyncCallback<Void> callback) {
		if(!scheduling) {
			throw new IllegalStateException("There is no ongoing session!");
		}
		stopScheduling();
		remoteEventService.invalidateSession(new AsyncCallback<Void>() {
			@Override
			public void onFailure(Throwable caught) {
				callback.onFailure(caught);
			}
			@Override
			public void onSuccess(Void result) {
				Console.log("Invalidated session with source id " + sourceId.asString() + ".");
				callback.onSuccess(null);
			}
		});
	}
	
	public <H extends EventHandler> void subscribe(final RemoteGwtEvent.Type<H> type, final AsyncCallback<Void> callback) {
		remoteEventService.addSubscription(type, new AsyncCallback<Boolean>() {
			@Override
			public void onFailure(Throwable caught) {
				callback.onFailure(caught);
			}

			@Override
			public void onSuccess(Boolean result) {
				callback.onSuccess(null);
			}
		});
	}
	
	public <H extends EventHandler> void unsubscribe(final RemoteGwtEvent.Type<H> type, final AsyncCallback<Boolean> callback) {
		remoteEventService.removeSubscription(type, callback);
	}
	
	public <H extends EventHandler> HandlerRegistration addHandler(final RemoteGwtEvent.Type<H> type, final H handler) {
		return handlerManager.addHandler(type, handler);
	}
	
	public <H extends EventHandler> void addHandler(final RemoteGwtEvent.Type<H> type, final H handler, final AsyncCallback<HandlerRegistration> callback) {
		if(!scheduling)
			startScheduling();
		if( handlerManager.getHandlerCount(type) == 0) {
			remoteEventService.addSubscription(type, new AsyncCallback<Boolean>() {
				@Override
				public void onFailure(Throwable caught) {
					callback.onFailure(caught);
				}
				@Override
				public void onSuccess(Boolean result) {
					HandlerRegistration registration = handlerManager.addHandler(type, handler);
					RemoteHandlerRegistrationAdapter handlerRegistration = new RemoteHandlerRegistrationAdapter(type, registration, autoUnsubscribe);
					callback.onSuccess(handlerRegistration);
				}
			});
		} else {
			HandlerRegistration registration = handlerManager.addHandler(type, handler);
			RemoteHandlerRegistrationAdapter handlerRegistration = new RemoteHandlerRegistrationAdapter(type, registration, autoUnsubscribe);
			callback.onSuccess(handlerRegistration);
		}
	}
	
	public void fireEvent(AbstractRemoteGwtEvent<?> event) {
		remoteEventService.fireEvent(event, defaultCallback);
	}
	
	public void fireEvent(AbstractRemoteGwtEvent<?> event, AsyncCallback<Void> callback) {
		remoteEventService.fireEvent(event, callback);
	}

	private void startScheduling() {
		if(scheduling) {
			throw new IllegalStateException("Scheduling has already started!");
		}
		scheduling = true;
		scheduleGetAvailableEvents();
		Console.log("Started scheduling.");
	}
	
	private void stopScheduling() {
		if(!scheduling) {
			throw new IllegalStateException("Scheduling has already stopped!");
		}
		scheduling = false;
		Console.log("Stopped scheduling.");
	}
	
	private void scheduleGetAvailableEvents() {
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
				if(scheduling) {
					getAvailableEvents();
				}
			}
		});
	}	

	private void getAvailableEvents() {
		Console.log("Getting available events...");
		remoteEventService.getAvailableEvents(new AsyncCallback<List<RemoteGwtEvent<?>>>() {
			@Override
			public void onSuccess(List<RemoteGwtEvent<?>> result) {
				Console.log("Received events " + result.size());
				for(RemoteGwtEvent<?> event: result) {
					Console.log("Forwarding remote event locally ...");
					if(event instanceof AbstractRemoteGwtEvent) {
						handlerManager.fireEvent((AbstractRemoteGwtEvent<?>) event);
					} else {
						Console.log("Unknown event type! " + event.getClass());
					}
				}
				if(scheduling) {
					scheduleGetAvailableEvents();
				}
			}
			@Override
			public void onFailure(Throwable caught) {
				if(caught instanceof RequestTimeoutException) {
					handlerManager.fireEvent(new ConnectionTimeoutEvent(RemoteEventBus.this));
				} else
				if(caught instanceof BufferOverflowException) {
					handlerManager.fireEvent(new BufferOverflowEvent(RemoteEventBus.this));
				} else
				if(caught instanceof InvalidSessionException) {
					handlerManager.fireEvent(new InvalidSessionEvent(RemoteEventBus.this));
				} else {
					failureHandler.onFailure(caught);
				}					
			}
		});				
	}
	
	@Override
	public HandlerRegistration addConnectionTimeoutHandler(ConnectionTimeoutEvent.Handler handler) {
		return handlerManager.addHandler(ConnectionTimeoutEvent.TYPE, handler);
	}

	@Override
	public HandlerRegistration addBufferOverflowHandler(BufferOverflowEvent.Handler handler) {
		return handlerManager.addHandler(BufferOverflowEvent.TYPE, handler);
	}

	public HandlerRegistration addInvalidSessionHandler(InvalidSessionEvent.Handler handler) {
		return handlerManager.addHandler(InvalidSessionEvent.TYPE, handler);
	}
	
	public void setFailureHandler(FailureHandler failureHandler) {
		this.failureHandler = failureHandler;
	}

}
