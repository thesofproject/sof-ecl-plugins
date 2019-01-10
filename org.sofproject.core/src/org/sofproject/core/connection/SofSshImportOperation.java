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
import java.util.Vector;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.sofproject.core.ISofNodeConst;
import org.sofproject.core.SofNodeProject;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

public class SofSshImportOperation extends SofRemoteOperation {

	public SofSshImportOperation() {
	}

	// TODO: check monitor.isCanceled() and throw InterruptedException then...
	@Override
	public void run(IProgressMonitor monitor)
			throws InvocationTargetException, InterruptedException {
		monitor.beginTask("Importing files", 1000);

		try {
			if (!conn.isConnected()) {
				throw new InvocationTargetException(
						new IllegalStateException("Node not connected"));
			}

			SofNodeDescriptor nodeDesc = conn.getNodeDescriptor();
			SofNodeProject proj = conn.getProject();

			monitor.worked(100);

			Session session = conn.getSession();
			Channel channel = session.openChannel("sftp");
			channel.connect();
			ChannelSftp c = (ChannelSftp) channel;

			monitor.subTask("Listing files");
			Vector<?> remoteFiles = c.ls(nodeDesc.getResPath());
			
			for (int i = 0; i < remoteFiles.size(); i++) {
				Object en = remoteFiles.elementAt(i);
				if (en instanceof ChannelSftp.LsEntry) {
					ChannelSftp.LsEntry entry = (ChannelSftp.LsEntry) en;
					Path remotePath = new Path(entry.getFilename());
					// filter out files other than fw binaries and topology files
					IFile localFile = null;
					if (remotePath.getFileExtension().equals(ISofNodeConst.FW_BIN_FILE_EXT)) {
						localFile = proj.getBinFolder().getFile(entry.getFilename());
					} else if (remotePath.getFileExtension().equals(ISofNodeConst.TPLG_FILE_EXT)) {
						localFile = proj.getTplgFolder().getFile(entry.getFilename());
					}
					if (localFile != null) {
						monitor.subTask("Downloading " + entry.getFilename());
						localFile.create(c.get(nodeDesc.getResPath() + "/" + entry.getFilename()),
								false, null);
					}
				}
				monitor.worked(900 / remoteFiles.size());
			}

			channel.disconnect();

			proj.getProject().refreshLocal(IResource.DEPTH_INFINITE, null);
		} catch (JSchException | SftpException | CoreException e) {
			throw new InvocationTargetException(e);
		}

		monitor.done();
	}
}
