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

package org.sofproject.fw;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.core.runtime.Status;
import org.sofproject.core.IAudioDevExtension;

/**
 * Custom attributes of Sof extension to Audio-dev-node project.
 */
public class SofNodeExtension implements IAudioDevExtension {

	/**
	 * Reference to a generic project resource.
	 */
	private IProject project;

	/**
	 * Name of the project with source code.
	 */
	private String srcProjName;

	/**
	 * Path to the fw binaries at the remote node.
	 */
	private String resPathFw;

	/**
	 * Path to the fw topologies at the remote node.
	 */
	private String resPathTplg;

	private String resPathLogger;

	private String resPathFwLdc;

	/**
	 * 'bin' folder inside the project, containing fw binaries.
	 */
	private IFolder binFolder;

	/**
	 * 'tplg' folder inside the project, containing topology binaries.
	 */
	private IFolder tplgFolder;

	/**
	 * Creates a new extension with no project assigned yet, init-ed with defaults.
	 */
	public SofNodeExtension() {
		srcProjName = ISofNodeConst.SOF_PROJ_DEFAULT_SRC_PROJ;
		resPathFw = ISofNodeConst.SOF_PROJ_DEFAULT_REMOTE_PATH_FW;
		resPathTplg = ISofNodeConst.SOF_PROJ_DEFAULT_REMOTE_PATH_TPLG;
		resPathLogger = ISofNodeConst.SOF_PROJ_DEFAULT_REMOTE_PATH_LOGGER;
		resPathFwLdc = ISofNodeConst.SOF_PROJ_DEFAULT_REMOTE_PATH_FW_LDC;
	}

	/**
	 * Creates a new sof project object attached to a generic project.
	 *
	 * @param project Generic project.
	 * @throws CoreException See operations on resources.
	 */
	public SofNodeExtension(IProject project) throws CoreException {
		this.project = project;
		srcProjName = project.getPersistentProperty(
				new QualifiedName(ISofNodeConst.SOF_NODE_CORE_ID, ISofNodeConst.SOF_PROJ_PROP_SRC_PROJ_NAME));
		if (srcProjName == null) // extension might not be fully created
			throw new CoreException(new Status(IStatus.ERROR, ISofNodeConst.SOF_NODE_CORE_ID, "Properties not set"));
		resPathFw = project.getPersistentProperty(
				new QualifiedName(ISofNodeConst.SOF_NODE_CORE_ID, ISofNodeConst.SOF_PROJ_PROP_REMOTE_RES_PATH_FW));
		resPathTplg = project.getPersistentProperty(
				new QualifiedName(ISofNodeConst.SOF_NODE_CORE_ID, ISofNodeConst.SOF_PROJ_PROP_REMOTE_RES_PATH_TPLG));
		resPathLogger = project.getPersistentProperty(
				new QualifiedName(ISofNodeConst.SOF_NODE_CORE_ID, ISofNodeConst.SOF_PROJ_PROP_REMOTE_RES_PATH_LOGGER));
		resPathFwLdc = project.getPersistentProperty(
				new QualifiedName(ISofNodeConst.SOF_NODE_CORE_ID, ISofNodeConst.SOF_PROJ_PROP_REMOTE_RES_PATH_FW_LDC));
	}

	public IProject getProject() {
		return project;
	}

	public String getSrcProjName() {
		return srcProjName;
	}

	public void setSrcProjName(String srcProjName) {
		this.srcProjName = srcProjName;
	}

	public String getResPathFw() {
		return resPathFw;
	}

	public void setResPathFw(String resPathFw) {
		this.resPathFw = resPathFw;
	}

	public String getResPathTplg() {
		return resPathTplg;
	}

	public void setResPathTplg(String resPathTplg) {
		this.resPathTplg = resPathTplg;
	}

	public String getResPathLogger() {
		return resPathLogger;
	}

	public void setResPathLogger(String resPathLogger) {
		this.resPathLogger = resPathLogger;
	}

	public String getResPathFwLdc() {
		return resPathFwLdc;
	}

	public void setResPathFwLdc(String resPathFwLdc) {
		this.resPathFwLdc = resPathFwLdc;
	}

	public IFolder getBinFolder() {
		if (binFolder == null) {
			binFolder = project.getFolder(ISofNodeConst.BIN_FOLDER);
		}
		return binFolder;
	}

	public IFolder getTplgFolder() {
		if (tplgFolder == null) {
			tplgFolder = project.getFolder(ISofNodeConst.TPLG_FOLDER);
		}
		return tplgFolder;
	}

	@Override
	public void createInProject(IProject project) throws CoreException {

		project.setPersistentProperty(
				new QualifiedName(ISofNodeConst.SOF_NODE_CORE_ID, ISofNodeConst.SOF_PROJ_PROP_SRC_PROJ_NAME),
				srcProjName);
		project.setPersistentProperty(
				new QualifiedName(ISofNodeConst.SOF_NODE_CORE_ID, ISofNodeConst.SOF_PROJ_PROP_REMOTE_RES_PATH_FW),
				resPathFw);
		project.setPersistentProperty(
				new QualifiedName(ISofNodeConst.SOF_NODE_CORE_ID, ISofNodeConst.SOF_PROJ_PROP_REMOTE_RES_PATH_TPLG),
				resPathTplg);
		project.setPersistentProperty(
				new QualifiedName(ISofNodeConst.SOF_NODE_CORE_ID, ISofNodeConst.SOF_PROJ_PROP_REMOTE_RES_PATH_LOGGER),
				resPathLogger);
		project.setPersistentProperty(
				new QualifiedName(ISofNodeConst.SOF_NODE_CORE_ID, ISofNodeConst.SOF_PROJ_PROP_REMOTE_RES_PATH_FW_LDC),
				resPathFwLdc);

		binFolder = project.getFolder(ISofNodeConst.BIN_FOLDER);
		if (!binFolder.exists()) {
			binFolder.create(false, true, null);
		}

		tplgFolder = project.getFolder(ISofNodeConst.TPLG_FOLDER);
		if (!tplgFolder.exists()) {
			tplgFolder.create(false, true, null);
		}
	}
}
