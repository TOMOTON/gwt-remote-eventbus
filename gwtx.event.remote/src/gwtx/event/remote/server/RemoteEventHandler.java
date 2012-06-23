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

import gwtx.event.remote.client.RemoteEventService;

import java.util.concurrent.ConcurrentHashMap;


/**
 * Static entry point for external classes which need to fire an event through
 * the remote event bus from the server-side.
 * 
 * @author Dann Martens
 */
public class RemoteEventHandler {

	private static class Singleton {
	
		private static final RemoteEventHandler INSTANCE = new RemoteEventHandler();
		
	}

	static RemoteEventHandler getInstance() {
		return Singleton.INSTANCE;
	}
	
	private ConcurrentHashMap<String, RemoteEventServiceImpl> serviceMap = new ConcurrentHashMap<String, RemoteEventServiceImpl>();
	
	public void register(String name, RemoteEventServiceImpl remoteEventService) {
		if(serviceMap.putIfAbsent(name, remoteEventService) != null) {
			throw new IllegalStateException("A remote event service with name '" + name + "' has already been registered!");
		}
	}
	
	public RemoteEventService get() {
		return get(RemoteEventServiceImpl.DEFAULT_NAME);
	}

	public RemoteEventService get(String name) {
		RemoteEventService result = serviceMap.get(name);
		if(result == null)
			throw new NullPointerException("No remote event service with name '" + name + "' has been registered!");
		return result;
	}
	
}
