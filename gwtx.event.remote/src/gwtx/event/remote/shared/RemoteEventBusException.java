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


/**
 * Thrown to indicate that a problem has occurred with the remote event bus. 
 * This class is the general class of exceptions produced by failed or 
 * interrupted remote event operations.
 * 
 * @author Dann Martens
 */
public class RemoteEventBusException extends Exception {

	/* Managed UID. */
	private static final long serialVersionUID = 1L;

	public RemoteEventBusException() {
		super();
	}
	
	public RemoteEventBusException(String message) {
		super(message);
	}

}
