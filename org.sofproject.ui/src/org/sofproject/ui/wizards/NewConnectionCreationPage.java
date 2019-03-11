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

package org.sofproject.ui.wizards;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.WizardNewProjectCreationPage;
import org.sofproject.core.ISofNodeConst;

public class NewConnectionCreationPage extends WizardNewProjectCreationPage {

	private Text addr;
	private Text remoteResPath;
	private Text srcProjName; // name of the project with the source code

	private Listener confModifyListener = e -> {
		boolean valid = validatePage();
		setPageComplete(valid);

	};

	public NewConnectionCreationPage(String pageName) {
		super(pageName);
	}

	public void init(IStructuredSelection selection) {
		// TODO: use working sets? setWorkingSets(selection)
	}

	@Override
	public void createControl(Composite parent) {
		super.createControl(parent);
		Composite control = (Composite) getControl();

		Composite addrGroup = new Composite(control, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		addrGroup.setLayout(layout);
		addrGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		new Label(addrGroup, SWT.NONE).setText("Node Address");
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		data.grabExcessHorizontalSpace = true;
		addr = new Text(addrGroup, SWT.BORDER);
		addr.setLayoutData(data);
		addr.addListener(SWT.Modify, confModifyListener);

		Composite pathGroup = new Composite(control, SWT.NONE);
		layout = new GridLayout();
		layout.numColumns = 2;
		pathGroup.setLayout(layout);
		pathGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		new Label(pathGroup, SWT.NONE).setText("Remote Resource Path");
		data = new GridData(GridData.FILL_HORIZONTAL);
		data.grabExcessHorizontalSpace = true;
		remoteResPath = new Text(pathGroup, SWT.BORDER);
		remoteResPath.setLayoutData(data);
		remoteResPath.setText(ISofNodeConst.SOF_PROJ_DEFAULT_REMOTE_PATH);
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
		srcProjName.setText(ISofNodeConst.SOF_PROJ_DEFAULT_SRC_PROJ);
		srcProjName.addListener(SWT.Modify, confModifyListener);
	}

	@Override
	public boolean isPageComplete() {
		if (!super.isPageComplete())
			return false;
		return !addr.getText().isEmpty() && !remoteResPath.getText().isEmpty();
	}

	public String getAddress() {
		return addr.getText();
	}

	public String getRemoteResPath() {
		return remoteResPath.getText();
	}

	public String getSrcProjName() {
		return srcProjName.getText();
	}
}
