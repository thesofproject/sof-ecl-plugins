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

package org.sofproject.gst.topo;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.core.runtime.Status;
import org.sofproject.core.IAudioDevExtension;

public class GstNodeExtension implements IAudioDevExtension {
	private IProject project;

	private String gstInspectToolCmd;
	private String gstLaunchToolCmd;

	public GstNodeExtension() {
		gstInspectToolCmd = IGstNodeConst.GST_PROJ_DEFAULT_GST_INSPECT_TOOL_CMD;
		gstLaunchToolCmd = IGstNodeConst.GST_PROJ_DEFAULT_GST_LAUNCH_TOOL_CMD;
	}

	public GstNodeExtension(IProject project) throws CoreException {
		this.project = project;
		gstInspectToolCmd = project.getPersistentProperty(
				new QualifiedName(IGstNodeConst.GST_NODE_CORE_ID, IGstNodeConst.GST_PROJ_PROP_GST_INSPECT_TOOL_CMD));
		if (gstInspectToolCmd == null) // extension might not be fully created
			throw new CoreException(new Status(IStatus.ERROR, IGstNodeConst.GST_NODE_CORE_ID, "Properties not set"));
		gstLaunchToolCmd = project.getPersistentProperty(
				new QualifiedName(IGstNodeConst.GST_NODE_CORE_ID, IGstNodeConst.GST_PROJ_PROP_GST_LAUNCH_TOOL_CMD));
	}

	public IProject getProject() {
		return project;
	}

	public void setGstInspectToolCmd(String gstInspectToolCmd) {
		this.gstInspectToolCmd = gstInspectToolCmd;
	}

	public String getGstInspectToolCmd() {
		return gstInspectToolCmd;
	}

	public void setGstLaunchToolCmd(String gstLaunchToolCmd) {
		this.gstLaunchToolCmd = gstLaunchToolCmd;
	}

	public String getGstLaunchToolCmd() {
		return gstLaunchToolCmd;
	}

	@Override
	public void createInProject(IProject project) throws CoreException {
		project.setPersistentProperty(
				new QualifiedName(IGstNodeConst.GST_NODE_CORE_ID, IGstNodeConst.GST_PROJ_PROP_GST_INSPECT_TOOL_CMD),
				gstInspectToolCmd);
		project.setPersistentProperty(
				new QualifiedName(IGstNodeConst.GST_NODE_CORE_ID, IGstNodeConst.GST_PROJ_PROP_GST_LAUNCH_TOOL_CMD),
				gstLaunchToolCmd);
	}

}
