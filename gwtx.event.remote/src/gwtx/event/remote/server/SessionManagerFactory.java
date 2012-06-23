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

import gwtx.event.remote.shared.ServerId;
import gwtx.event.remote.shared.SourceId;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;


/**
 * A factory for remote event bus sessions. This factory is able to create 
 * only one type: a standalone session manager. The
 * standalone manager caches and expires sessons using a separate thread. 
 * 
 * @author Dann Martens
 */
public class SessionManagerFactory {
	
	private static final long DEFAULT_EXPIRATION_DURATION = 30;
	
	private static final TimeUnit DEFAULT_EXPIRATION_TIME_UNIT = TimeUnit.SECONDS;
		
	private static class Standalone implements SessionManager {
		
		private int sourceIdCounter = 1;
		
		private Cache<SourceId, Session> cache;
		
		private RemovalListener<SourceId, Session> listener;
		
		private CopyOnWriteArrayList<SessionListener> sessionListenerList = new CopyOnWriteArrayList<SessionListener>();
		
		private Standalone() { 
			listener = new RemovalListener<SourceId, Session>() {
				@Override
				public void onRemoval(RemovalNotification<SourceId, Session> notification) {
					for(SessionListener sessionListener: sessionListenerList) {
						if(!notification.getValue().isInvalidated()) {
							sessionListener.onSessionExpire(new SessionEvent(notification.getKey()));
						}
					}					
				}
			};
			cache = CacheBuilder.newBuilder()
					    .expireAfterAccess(DEFAULT_EXPIRATION_DURATION, DEFAULT_EXPIRATION_TIME_UNIT)
					    .removalListener(listener)					    
					    .build();
		}

		@Override
		public SourceId service(HttpServletRequest request, HttpServletResponse response) {
			String header = request.getHeader("X-GWT-RemoteEventSource");
			//! System.err.println("Request Header is " + header);
			SourceId result = null;
			try {
				int value = Integer.parseInt(header);
				result = new SourceId(value);
			} catch (Exception ignore) {}
			return result;
		}

		@Override
		public Session getSession(SourceId sourceId) {
			if(sourceId == null)
				throw new NullPointerException();
			cache.cleanUp();
			return cache.getIfPresent(sourceId);
		}

		@Override
		public SourceId newSession(ServerId serverId) {
			SourceId result = new SourceId(++sourceIdCounter);
			Session session = new RemoteEventBusSession();
			cache.put(result, session);
			for(SessionListener sessionListener: sessionListenerList) {
				sessionListener.onSessionNew(new SessionEvent(result));
			}
			return result;
		}

		@Override
		public void invalidate(SourceId sourceId) {
			Session session = getSession(sourceId);
			session.invalidate();
			cache.invalidate(sourceId);
			for(SessionListener sessionListener: sessionListenerList) {
				sessionListener.onSessionInvalidate(new SessionEvent(sourceId));
			}
		}

		@Override
		public void addSessionListener(SessionListener listener) {
			sessionListenerList.add(listener);
		}

		@Override
		public void removeSessionListener(SessionListener listener) {
			sessionListenerList.remove(listener);
		}

		@Override
		public String toString() {
			return "Standalone*SessionManager:{size=" + cache.size() + '}';
		}
		
	}

	public static SessionManager newDefaultInstance() {
		return newSystemManaged();
	}
	
	public static SessionManager newSystemManaged() {
		return new Standalone();
	}
		
}
