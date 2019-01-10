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

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.QualifiedName;
import org.sofproject.core.connection.SofNodeConnectionManager;
import org.sofproject.core.connection.SofNodeDescriptor;

/**
 * Custom attributes of Sof projects.
 */
public class SofNodeProject {

	/**
	 * Reference to a generic project resource.
	 */
	private IProject proj;

	/**
	 * Node (DUT) attributes.
	 */
	private SofNodeDescriptor nodeDesc;

	/**
	 * 'bin' folder inside the project, containing fw binaries.
	 */
	private IFolder binFolder;

	/**
	 * 'tplg' folder inside the project, containing topology binaries.
	 */
	private IFolder tplgFolder;

	/**
	 * Restores a sof project object attached to a generic project. This on is used
	 * to restore a sof project on restart.
	 * 
	 * @param proj Generic project.
	 * @return Sof project
	 * @throws CoreException See operations on resources.
	 */
	public static SofNodeProject fromProject(IProject proj) throws CoreException {
		String address = proj.getPersistentProperty(new QualifiedName(
				ISofNodeConst.SOF_NODE_CORE_ID, ISofNodeConst.SOF_PROJ_PROP_NODE_ADDRESS));
		String resPath = proj.getPersistentProperty(new QualifiedName(
				ISofNodeConst.SOF_NODE_CORE_ID, ISofNodeConst.SOF_PROJ_PROP_REMOTE_RES_PATH));
		SofNodeDescriptor nodeDesc = new SofNodeDescriptor(address, SofNodeDescriptor.DEFAULT_PORT,
				resPath);
		SofNodeProject sofNodeProject = new SofNodeProject(proj, nodeDesc);
		return sofNodeProject;
	}

	/**
	 * Creates a new sof project object attached to a generic project.
	 * 
	 * @param proj     Generic project.
	 * @param nodeDesc Node (DUT) descriptor.
	 * @throws CoreException See operations on resources.
	 */
	public SofNodeProject(IProject proj, SofNodeDescriptor nodeDesc) throws CoreException {
		this.proj = proj;
		this.nodeDesc = nodeDesc;

		proj.setPersistentProperty(new QualifiedName(ISofNodeConst.SOF_NODE_CORE_ID,
				ISofNodeConst.SOF_PROJ_PROP_NODE_ADDRESS), nodeDesc.getAddr());
		proj.setPersistentProperty(new QualifiedName(ISofNodeConst.SOF_NODE_CORE_ID,
				ISofNodeConst.SOF_PROJ_PROP_REMOTE_RES_PATH), nodeDesc.getResPath());

		binFolder = proj.getFolder(ISofNodeConst.BIN_FOLDER);
		if (!binFolder.exists()) {
			binFolder.create(false, true, null);
		}

		tplgFolder = proj.getFolder(ISofNodeConst.TPLG_FOLDER);
		if (!tplgFolder.exists()) {
			tplgFolder.create(false, true, null);
		}
	}

	public SofNodeDescriptor getNodeDescriptor() {
		return nodeDesc;
	}

	public IProject getProject() {
		return proj;
	}

	public IFolder getBinFolder() {
		return binFolder;
	}

	public IFolder getTplgFolder() {
		return tplgFolder;
	}

	public static IProject create(String projName, IPath projPath, String nodeAddress,
			String remoteResPath, IProgressMonitor monitor) throws CoreException {
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IProject proj = root.getProject(projName);
		IProjectDescription desc = ResourcesPlugin.getWorkspace().newProjectDescription(projName);
		desc.setNatureIds(new String[] { ISofNodeConst.SOF_NODE_NATURE_ID });
		proj.create(desc, monitor);
		proj.open(monitor);

		// TODO: add port configuration
		int port = SofNodeDescriptor.DEFAULT_PORT;
		SofNodeDescriptor nodeDesc = new SofNodeDescriptor(nodeAddress, port, remoteResPath);
		// TODO: need to force the connection or by an event handler?
		SofNodeConnectionManager.getInstance().createConnection(new SofNodeProject(proj, nodeDesc));

		return proj;
	}
}
