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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * Interface which is implemented by session managers.
 * 
 * @author Dann Martens
 */
public interface SessionManager {

	SourceId service(HttpServletRequest threadLocalRequest, HttpServletResponse threadLocalResponse);

	Session getSession(SourceId sourceId);

	SourceId newSession(ServerId serverId);

	void invalidate(SourceId sourceId);
	
	void addSessionListener(SessionListener listener);

	void removeSessionListener(SessionListener listener);
	
}
