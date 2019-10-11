/*
 * Copyright (c) 2019, Intel Corporation
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

package org.sofproject.gst.topo.ops;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.sofproject.core.connection.AudioDevNodeConnection;
import org.sofproject.core.ops.SimpleRemoteOp;
import org.sofproject.gst.topo.model.GstTopoGraph;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

public class GstTopoTestOperation extends SimpleRemoteOp {

	private GstTopoGraph graph;

	public GstTopoTestOperation(GstTopoGraph graph, AudioDevNodeConnection conn) {
		super(conn);
		this.graph = graph;
	}

	@Override
	public boolean isCancelable() {
		return true;
	}

	@Override
	public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
		monitor.beginTask("Running topology test", 1000);

		try {
			if (!conn.isConnected()) {
				throw new InvocationTargetException(new IllegalStateException("Node not connected"));
			}

			String pplString = graph.getPipelineString();
			monitor.subTask("Running: " + pplString);

			Session session = conn.getSession();
			ChannelExec channel = (ChannelExec) session.openChannel("exec");

			channel.setPty(true); // for ctrl+c sending
			channel.setCommand("gst-launch-1.0 " + pplString);

			channel.setInputStream(null);
			OutputStream os = channel.getOutputStream();
			channel.connect();

			int cnt = 0;
			while (!channel.isEOF()) {
				Thread.sleep(300);
				cnt += 100;
				if (cnt == 1000) {
					monitor.beginTask("Running topology test", 1000);
					cnt = 0;
				}
				monitor.worked(cnt);
				if (monitor.isCanceled()) {
					try {
						os.write(3);
						os.flush();
					} catch (Exception e) {
						e.printStackTrace();
					}
					channel.disconnect();
					return;
				}
			}
			channel.disconnect();

		} catch (JSchException | IOException e) {
			throw new InvocationTargetException(e);
		}
	}

}
