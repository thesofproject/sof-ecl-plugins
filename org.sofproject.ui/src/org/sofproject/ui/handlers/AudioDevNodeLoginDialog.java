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

package org.sofproject.ui.handlers;

import org.eclipse.jface.dialogs.TrayDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class AudioDevNodeLoginDialog extends TrayDialog {

	private Text projectField;
	private Text nodeAddressField;

	private String project;
	private String nodeAddress;

	private Text loginField;
	private Text passField;

	private String login = null;
	private String pass = null;

	public AudioDevNodeLoginDialog(Shell parentShell, String project, String nodeAddress) {
		super(parentShell);
		this.project = project;
		this.nodeAddress = nodeAddress;
	}

	@Override
	protected void configureShell(Shell shell) {
		super.configureShell(shell);
		shell.setText("Enter your credentials");
	}

	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		Composite infoGroup = new Composite(container, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		infoGroup.setLayout(layout);
		infoGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		projectField = new Text(infoGroup, SWT.READ_ONLY | SWT.BOLD);
		projectField.setText(project);

		nodeAddressField = new Text(infoGroup, SWT.READ_ONLY);
		nodeAddressField.setText("Connecting to: " + nodeAddress);

		Composite loginGroup = new Composite(container, SWT.NONE);
		layout = new GridLayout();
		layout.numColumns = 2;
		loginGroup.setLayout(layout);
		loginGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		new Label(loginGroup, SWT.NONE).setText("Login");
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		data.grabExcessHorizontalSpace = true;
		loginField = new Text(loginGroup, SWT.BORDER);
		loginField.setLayoutData(data);

		new Label(loginGroup, SWT.NONE).setText("Password");
		data = new GridData(GridData.FILL_HORIZONTAL);
		data.grabExcessHorizontalSpace = true;
		passField = new Text(loginGroup, SWT.PASSWORD | SWT.BORDER);
		passField.setLayoutData(data);

		loginField.setFocus();

		return container;
	}

	protected void okPressed() {
		login = loginField.getText();
		pass = passField.getText();
		super.okPressed();
	}

	public String getLogin() {
		return login;
	}

	public String getPass() {
		return pass;
	}

}
