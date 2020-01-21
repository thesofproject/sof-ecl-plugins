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

package org.sofproject.gst.topo.ui.wizards;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.sofproject.gst.topo.GstNodeExtension;
import org.sofproject.ui.wizards.INewNodeExtensionPage;

public class GstNodeNewPage extends WizardPage implements INewNodeExtensionPage {

	private Text gstInspectToolCmd;
	private Text gstLaunchToolCmd;

	private GstNodeExtension gstNode;

	private Listener confModifyListener = e -> {
		boolean valid = validatePage();
		setPageComplete(valid);

	};

	public GstNodeNewPage(GstNodeExtension gstNode) {
		super("GstNode");
		this.gstNode = gstNode;
		setTitle("GStreamer Node Configuration");
		setDescription("Configure GStreamer Development node");
	}

	@Override
	public void createControl(Composite parent) {
		Composite control = new Composite(parent, SWT.NULL);
		initializeDialogUnits(parent);
		control.setLayout(new GridLayout());
		control.setLayoutData(new GridData(GridData.FILL_BOTH));

		Composite inspectGroup = new Composite(control, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		inspectGroup.setLayout(layout);
		inspectGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		new Label(inspectGroup, SWT.NONE).setText("gst-inspect command");
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		data.grabExcessHorizontalSpace = true;
		gstInspectToolCmd = new Text(inspectGroup, SWT.BORDER);
		gstInspectToolCmd.setLayoutData(data);
		gstInspectToolCmd.setText(gstNode.getGstInspectToolCmd());
		gstInspectToolCmd.addListener(SWT.Modify, confModifyListener);

		Composite launchGroup = new Composite(control, SWT.NONE);
		layout = new GridLayout();
		layout.numColumns = 2;
		launchGroup.setLayout(layout);
		launchGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		new Label(launchGroup, SWT.NONE).setText("gst-launch command");
		data = new GridData(GridData.FILL_HORIZONTAL);
		data.grabExcessHorizontalSpace = true;
		gstLaunchToolCmd = new Text(launchGroup, SWT.BORDER);
		gstLaunchToolCmd.setLayoutData(data);
		gstLaunchToolCmd.setText(gstNode.getGstLaunchToolCmd());
		gstLaunchToolCmd.addListener(SWT.Modify, confModifyListener);

		setControl(control);
		Dialog.applyDialogFont(control);
	}

	protected boolean validatePage() {
		if (getGstInspectToolCmd().equals("")) {
			setErrorMessage(null);
			setMessage("gst-inspect command cannot be empty");
			return false;
		}
		if (getGstLaunchToolCmd().equals("")) {
			setErrorMessage(null);
			setMessage("gst-launch command cannot be empty");
			return false;
		}
		return true;
	}

	public String getGstInspectToolCmd() {
		if (gstInspectToolCmd == null)
			return "";
		return gstInspectToolCmd.getText().trim();
	}

	public String getGstLaunchToolCmd() {
		if (gstLaunchToolCmd == null)
			return "";
		return gstLaunchToolCmd.getText().trim();
	}

	@Override
	public void commitSettings() {
		gstNode.setGstInspectToolCmd(getGstInspectToolCmd());
		gstNode.setGstLaunchToolCmd(getGstLaunchToolCmd());
	}
}
