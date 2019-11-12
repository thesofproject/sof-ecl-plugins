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

	private Text remoteResPath;
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

		Composite pathGroup = new Composite(control, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		pathGroup.setLayout(layout);
		pathGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		new Label(pathGroup, SWT.NONE).setText("Remote Resource Path");
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		data.grabExcessHorizontalSpace = true;
		remoteResPath = new Text(pathGroup, SWT.BORDER);
		remoteResPath.setLayoutData(data);
		remoteResPath.setText(sofNode.getResPath());
		remoteResPath.addListener(SWT.Modify, confModifyListener);

		Composite srcProjGroup = new Composite(control, SWT.NONE);
		layout = new GridLayout();
		layout.numColumns = 2;
		srcProjGroup.setLayout(layout);
		srcProjGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		new Label(srcProjGroup, SWT.NONE).setText("Source Code Project Name");
		data = new GridData(GridData.FILL_HORIZONTAL);
		data.grabExcessHorizontalSpace = true;
		srcProjName = new Text(srcProjGroup, SWT.BORDER);
		srcProjName.setLayoutData(data);
		srcProjName.setText(sofNode.getSrcProjName());
		srcProjName.addListener(SWT.Modify, confModifyListener);

		setControl(control);
        Dialog.applyDialogFont(control);
	}

	protected boolean validatePage() {
		if (getRemoteResPath().equals("")) {
			setErrorMessage(null);
			setMessage("Remote resource path cannot be empty");
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

	public String getRemoteResPath() {
		if (remoteResPath == null)
			return "";
		return remoteResPath.getText().trim();
	}

	public String getSrcProjName() {
		if (srcProjName == null)
			return "";
		return srcProjName.getText().trim();
	}

	@Override
	public void commitSettings() {
		sofNode.setResPath(getRemoteResPath());
		sofNode.setSrcProjName(getSrcProjName());
	}
}
