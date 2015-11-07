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

import gwtx.event.remote.shared.HasRemoteHandlers;
import gwtx.event.remote.shared.RemoteEventBusException;
import gwtx.event.remote.shared.RemoteGwtEvent;
import gwtx.event.remote.shared.RemoteGwtEvent.Type;
import gwtx.event.remote.shared.RemoteSessionId;
import gwtx.event.remote.shared.SourceId;

import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;


/**
 * RPC interface for the communication system which manages the remote event
 * bus across the network. 
 * 
 * @author Dann Martens
 */
@RemoteServiceRelativePath("remote-event")
public abstract interface RemoteEventService extends RemoteService, HasRemoteHandlers {

	RemoteSessionId newSession();
	
	void invalidateSession();
	
	<H> boolean addSubscription(Type<H> type);

	<H> boolean removeSubscription(Type<H> type);
	
	List<RemoteGwtEvent<?>> getAvailableEvents() throws RemoteEventBusException;

	void fireEvent(RemoteGwtEvent<?> event);
	
}
