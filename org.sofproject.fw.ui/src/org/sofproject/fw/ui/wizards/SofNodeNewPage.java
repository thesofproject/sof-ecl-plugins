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

package org.sofproject.fw.ui.wizards;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.sofproject.fw.SofNodeExtension;
import org.sofproject.ui.wizards.INewNodeExtensionPage;

public class SofNodeNewPage extends WizardPage implements INewNodeExtensionPage {

	private Text remoteResPathFw;
	private Text remoteResPathTplg;
	private Text remoteResPathLogger;
	private Text remoteResPathFwLdc;
	private Text srcProjName; // name of the project with the source code

	private SofNodeExtension sofNode;

	private Listener confModifyListener = e -> {
		boolean valid = validatePage();
		setPageComplete(valid);

	};

	protected SofNodeNewPage(SofNodeExtension sofNode) {
		super("SofNode");
		this.sofNode = sofNode;
		setTitle("SOF Node Configuration");
		setDescription("Configure SOF Development node");
		setImageDescriptor(ImageDescriptor.createFromFile(getClass(), "/icons/sof-logo.png"));
	}

	@Override
	public void createControl(Composite parent) {
		Composite control = new Composite(parent, SWT.NULL);
        initializeDialogUnits(parent);
        control.setLayout(new GridLayout());
        control.setLayoutData(new GridData(GridData.FILL_BOTH));

		remoteResPathFw = createTextField(control, "Remote Path to Fw Binaries", sofNode.getResPathFw());
		remoteResPathTplg = createTextField(control, "Remote Path to Topology Files", sofNode.getResPathTplg());
		remoteResPathLogger = createTextField(control, "Remote Path to Logger", sofNode.getResPathLogger());
		remoteResPathFwLdc = createTextField(control, "Remote Path to FW .ldc", sofNode.getResPathFwLdc());

		srcProjName = createTextField(control, "Name of project with fw sources", sofNode.getSrcProjName());

		setControl(control);
        Dialog.applyDialogFont(control);
	}

	private Text createTextField(Composite parent, String label, String initVal) {
		Composite textGroup = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		textGroup.setLayout(layout);
		textGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		new Label(textGroup, SWT.NONE).setText(label);
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		data.grabExcessHorizontalSpace = true;
		Text text = new Text(textGroup, SWT.BORDER);
		text.setLayoutData(data);
		text.setText(initVal);
		text.addListener(SWT.Modify, confModifyListener);
		return text;
	}

	protected boolean validatePage() {
		if (getRemoteResPathFw().equals("")) {
			setErrorMessage(null);
			setMessage("Remote path to fw binaries cannot be empty");
			return false;
		}
		if (getRemoteResPathTplg().equals("")) {
			setErrorMessage(null);
			setMessage("Remote path to topology files cannot be empty");
			return false;
		}
		if (getRemoteResPathLogger().equals("")) {
			setErrorMessage(null);
			setMessage("Remote path to logger cannot be empty");
			return false;
		}
		if (getRemoteResPathFwLdc().equals("")) {
			setErrorMessage(null);
			setMessage("Remote path to fw .ldc cannot be empty");
			return false;
		}
		if (getSrcProjName().equals("")) {
			setErrorMessage(null);
			setMessage("Name of project with the sources cannot be empty");
			return false;
		}
		// TODO: check if project exists in the workspace
		return true;
	}

	public String getRemoteResPathFw() {
		if (remoteResPathFw == null)
			return "";
		return remoteResPathFw.getText().trim();
	}

	public String getRemoteResPathTplg() {
		if (remoteResPathTplg == null)
			return "";
		return remoteResPathTplg.getText().trim();
	}

	public String getRemoteResPathLogger() {
		if (remoteResPathLogger == null)
			return "";
		return remoteResPathLogger.getText().trim();
	}

	public String getRemoteResPathFwLdc() {
		if (remoteResPathFwLdc == null)
			return "";
		return remoteResPathFwLdc.getText().trim();
	}

	public String getSrcProjName() {
		if (srcProjName == null)
			return "";
		return srcProjName.getText().trim();
	}

	@Override
	public void commitSettings() {
		sofNode.setResPathFw(getRemoteResPathFw());
		sofNode.setResPathTplg(getRemoteResPathTplg());
		sofNode.setResPathLogger(getRemoteResPathLogger());
		sofNode.setResPathFwLdc(getRemoteResPathFwLdc());
		sofNode.setSrcProjName(getSrcProjName());
	}
}
