/*
 * Copyright (c) 2018, Intel Corporation
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *   * Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *   * Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *   * Neither the name of the Intel Corporation nor the
 *     names of its contributors may be used to endorse or promote products
 *     derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 */

package org.sofproject.core.connection;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Status;
import org.sofproject.core.ISofNodeConst;
import org.sofproject.core.SofNodeProject;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

public class SofNodeConnection {

	private SofNodeProject project;
	private Session session = null;

	public static final String DMESG_CH = "dmesg";
	public static final String LOGGER_CH = "logger";
	private Map<String, ChannelExec> channels = new HashMap<>();

	public SofNodeConnection(SofNodeProject project) {
		this.project = project;
	}

	public SofNodeDescriptor getNodeDescriptor() {
		return project.getNodeDescriptor();
	}

	public SofNodeProject getProject() {
		return project;
	}

	public Session getSession() {
		return session;
	}

	public void connect(String user, String pass) throws CoreException {
		if (isConnected())
			return;
		try {
			JSch.setConfig("StrictHostKeyChecking", "no");
			JSch jsch = new JSch();
//			jsch.setKnownHosts("~/.ssh/known_hosts");
//			jsch.addIdentity("~/.ssh/id_rsa", "passphrase");
			SofNodeDescriptor nodeDesc = getNodeDescriptor();

			try {
				session = jsch.getSession(user, nodeDesc.getAddr(), nodeDesc.getPort());

				session.setPassword(pass);
				session.connect();
			} catch (JSchException e) {
				session = null;
				throw e;
			}
		} catch (JSchException e) {
			throw new CoreException(
					new Status(Status.ERROR, ISofNodeConst.SOF_NODE_CORE_ID, e.getMessage()));
		}
	}

	public boolean isConnected() {
		return session != null && session.isConnected();
	}

	public void setExecChannel(String type, ChannelExec ce) {
		channels.put(type, ce);
	}

	public boolean hasChannelOpened(String chType) {
		ChannelExec ce = channels.get(chType);
		return ce != null && ce.isConnected();
	}

	public static SofRemoteOperation createImportResourcesOp() {
		return new SofSshImportOperation();
	}

	public static SofRemoteOperation createConnectDmesgOp() {
		return new SofSshRunCmdOperation(DMESG_CH, "dmesg -w");
	}

	public static SofRemoteOperation createConnectLoggerOp() {
		// TODO: run the logger from the default location
		return new SofSshRunCmdOperation(LOGGER_CH, "./run-logger.sh");
	}

	public void close() {
		for (ChannelExec ch : channels.values()) {
			ch.disconnect();
		}
		channels.clear();

		if (isConnected()) {
			session.disconnect();
			session = null;
		}
	}

}
