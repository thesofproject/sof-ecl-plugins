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

package org.sofproject.core;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.core.runtime.Status;
import org.sofproject.core.connection.AudioDevNodeConnectionManager;

/**
 * Custom attributes of Audio-dev-node projects.
 */
public class AudioDevNodeProject {

	/**
	 * Reference to a generic project resource.
	 */
	private IProject proj;

	// node attributes

	public static int DEFAULT_REMOTE_SSH_PORT = 22;

	private String address;
	private int port;

	private Map<String, IAudioDevExtension> extensions = new HashMap<>();

	/**
	 * Restores an audio-dev-node project object attached to a generic project. This
	 * on is used to restore an audio-dev-node project on restart.
	 *
	 * @param proj Generic project.
	 * @return Audio-dev-node project
	 * @throws CoreException See operations on resources.
	 */
	public AudioDevNodeProject(IProject proj) throws CoreException {
		this.proj = proj;
		address = proj.getPersistentProperty(new QualifiedName(IAudioDevNodeConst.AUDIO_DEV_NODE_CORE_ID,
				IAudioDevNodeConst.AUDIO_DEV_PROJ_PROP_NODE_ADDRESS));
		if (address == null) {
			// project might be partially created
			throw new CoreException(
					new Status(IStatus.ERROR, IAudioDevNodeConst.AUDIO_DEV_NODE_CORE_ID, "Properties not configured"));
		}
		port = Integer.parseInt(proj.getPersistentProperty(new QualifiedName(IAudioDevNodeConst.AUDIO_DEV_NODE_CORE_ID,
				IAudioDevNodeConst.AUDIO_DEV_PROJ_PROP_NODE_PORT)));

		for (IAudioDevExtensionProvider extProvider : AudioDevExtensions.getInstance().getProviders()) {
			IAudioDevExtension ext = extProvider.readFromProject(proj);
			extensions.put(extProvider.getExtensionId(), ext);
		}
	}

	public AudioDevNodeProject() {
		address = "";
		port = DEFAULT_REMOTE_SSH_PORT;
		for (IAudioDevExtensionProvider extProvider : AudioDevExtensions.getInstance().getProviders()) {
			IAudioDevExtension ext = extProvider.createExtension();
			extensions.put(extProvider.getExtensionId(), ext);
		}
	}

	public IProject getProject() {
		return proj;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public IAudioDevExtension getExtension(String id) {
		return extensions.get(id);
	}

	public IProject create(String projName, IPath projPath, IProgressMonitor monitor) throws CoreException {
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		proj = root.getProject(projName);
		IProjectDescription desc = ResourcesPlugin.getWorkspace().newProjectDescription(projName);
		desc.setNatureIds(new String[] { IAudioDevNodeConst.AUDIO_DEV_NODE_NATURE_ID });
		proj.create(desc, monitor);
		proj.open(monitor);

		proj.setPersistentProperty(new QualifiedName(IAudioDevNodeConst.AUDIO_DEV_NODE_CORE_ID,
				IAudioDevNodeConst.AUDIO_DEV_PROJ_PROP_NODE_ADDRESS), address);
		proj.setPersistentProperty(new QualifiedName(IAudioDevNodeConst.AUDIO_DEV_NODE_CORE_ID,
				IAudioDevNodeConst.AUDIO_DEV_PROJ_PROP_NODE_PORT), Integer.toString(port));

		for (IAudioDevExtension ext : extensions.values()) {
			ext.createInProject(proj);
		}

		AudioDevNodeConnectionManager.getInstance().createConnection(this);
		return proj;
	}
}
