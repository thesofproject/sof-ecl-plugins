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

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.WizardNewProjectCreationPage;
import org.sofproject.core.AudioDevNodeProject;
import org.sofproject.gst.topo.IGstNodeConst;

public class NewAudioDevNodeCreationPage extends WizardNewProjectCreationPage {

	private Text addr;

	private Listener confModifyListener = e -> {
		boolean valid = validatePage();
		setPageComplete(valid);

	};

	public NewAudioDevNodeCreationPage(String pageName, AudioDevNodeProject devNodeProject) {
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
		addr.setText(Platform.getPreferencesService().getString(IGstNodeConst.GST_NODE_PREFERENCES_ID,
				IGstNodeConst.CONN_ADDR_PREF_NAME, "", null));
	}

	@Override
	public boolean isPageComplete() {
		if (!super.isPageComplete())
			return false;
		return !addr.getText().isEmpty();
	}

	public String getAddress() {
		return addr.getText();
	}
}
