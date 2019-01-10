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

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

public class SofSshRunCmdOperation extends SofRemoteOperation {
	private String chType;
	private String cmd;

	public SofSshRunCmdOperation(String chType, String cmd) {
		this.chType = chType;
		this.cmd = cmd;
	}

	// TODO: check monitor.isCanceled() and throw InterruptedException then...
	@Override
	public void run(IProgressMonitor monitor)
			throws InvocationTargetException, InterruptedException {

		try {
			if (!conn.isConnected()) {
				throw new InvocationTargetException(
						new IllegalStateException("Node not connected"));
			}
			if (conn.hasChannelOpened(chType)) {
				monitor.done();
				return;
			}
			Session session = conn.getSession();
			ChannelExec channel = (ChannelExec) session.openChannel("exec");
			channel.setCommand(cmd);
			channel.setInputStream(null);
			channel.setOutputStream(os); // set to the super class before run()
			channel.connect();

			conn.setExecChannel(chType, channel); // store channel back
		} catch (JSchException e) {
			throw new InvocationTargetException(e);
		}
	}

}
