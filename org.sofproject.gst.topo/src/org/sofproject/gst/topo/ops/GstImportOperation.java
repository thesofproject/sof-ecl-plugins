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

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.sofproject.core.AudioDevNodeProject;
import org.sofproject.core.connection.AudioDevNodeConnection;
import org.sofproject.core.ops.SimpleRemoteOp;
import org.sofproject.gst.topo.GstNodeExtension;
import org.sofproject.gst.topo.IGstNodeConst;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

public class GstImportOperation extends SimpleRemoteOp {

	public GstImportOperation(AudioDevNodeConnection conn) {
		super(conn);
	}

	@Override
	public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
		monitor.beginTask("Importing GStreamer plugins description", 1000);

		try {
			if (!conn.isConnected()) {
				throw new InvocationTargetException(new IllegalStateException("Node not connected"));
			}

			AudioDevNodeProject proj = conn.getProject();
			GstNodeExtension gstNode = (GstNodeExtension) proj.getExtension(IGstNodeConst.GST_NODE_EXTENSION_ID);

			Session session = conn.getSession();
			ChannelExec channel = (ChannelExec) session.openChannel("exec");
			channel.setCommand(gstNode.getGstInspectToolCmd() + " --plugin");

			channel.setInputStream(null);
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			channel.setOutputStream(os);
			channel.connect();

			while (!channel.isEOF())
				;
			channel.disconnect();

			IFolder plgRoot = proj.getProject().getFolder("gst-model");
			if (!plgRoot.exists()) {
				plgRoot.create(true, false, null);
			}

			ByteArrayInputStream bais = new ByteArrayInputStream(os.toByteArray());
			BufferedReader reader = new BufferedReader(new InputStreamReader(bais));

			int remainingWork = bais.available();
			monitor.beginTask("Importing GStreamer plugins description", remainingWork);

			String lastPlugin = "";
			IFolder plgDir = null;
			while (true) {

				monitor.worked(remainingWork - bais.available());
				remainingWork = bais.available();

				String nextLine = reader.readLine();
				if (nextLine == null)
					break;
				String[] tok = nextLine.split(":");
				if (tok.length < 3)
					continue;
				monitor.subTask(String.format("Importing %s from %s", tok[1], tok[0]));

				tok[1] = tok[1].replace('/', '_');
				if (!tok[0].equals(lastPlugin)) {

					// Check if there are any files created in the previous dir
					// and remove the dir otherwise
					if (plgDir != null && plgDir.members().length == 0) {
						plgDir.delete(true, false, null);
					}

					lastPlugin = tok[0];
					plgDir = plgRoot.getFolder(lastPlugin);
					if (!plgDir.exists()) {
						plgDir.create(true, false, null);
					}

					// TODO: possible to re-use channel?

					ChannelExec chPlgInfo = (ChannelExec) session.openChannel("exec");
					chPlgInfo.setCommand(gstNode.getGstInspectToolCmd() + " --plugin " + lastPlugin);
					ByteArrayOutputStream plgInfoOs = new ByteArrayOutputStream();
					chPlgInfo.setOutputStream(plgInfoOs);
					chPlgInfo.connect();
					while (!chPlgInfo.isEOF())
						;
					chPlgInfo.disconnect();

					byte[] plgInfoBytes = plgInfoOs.toByteArray();
					if (plgInfoBytes.length > 0) { // do not create empty files
						ByteArrayInputStream plgInfoIn = new ByteArrayInputStream(plgInfoBytes);
						IFile plgInfoFile = plgDir.getFile(lastPlugin + ".txt");
						if (plgInfoFile.exists()) {
							plgInfoFile.setContents(plgInfoIn, true, false, null);
						} else {
							plgInfoFile.create(plgInfoIn, false, null);
						}
					}
				}
				String lastElem = tok[1].trim();
				ChannelExec chElemInfo = (ChannelExec) session.openChannel("exec");
				chElemInfo.setCommand(gstNode.getGstInspectToolCmd() + " " + lastElem);
				ByteArrayOutputStream elemInfoOs = new ByteArrayOutputStream();
				chElemInfo.setOutputStream(elemInfoOs);
				chElemInfo.connect();
				while (!chElemInfo.isEOF())
					;
				chElemInfo.disconnect();

				byte[] elemInfoBytes = elemInfoOs.toByteArray();
				if (elemInfoBytes.length > 0) {
					ByteArrayInputStream elemInfoIn = new ByteArrayInputStream(elemInfoBytes);
					IFile elemInfoFile = plgDir.getFile(lastPlugin + "." + lastElem + ".txt");
					if (elemInfoFile.exists()) {
						elemInfoFile.setContents(elemInfoIn, true, false, null);
					} else {
						elemInfoFile.create(elemInfoIn, false, null);
					}
				}
			}

		} catch (JSchException | IOException | CoreException e) {
			throw new InvocationTargetException(e);
		}
	}

}
