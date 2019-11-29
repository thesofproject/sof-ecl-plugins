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

package org.sofproject.gst.json;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.sofproject.topo.ui.graph.ITopoGraph;
import org.sofproject.gst.json.JsonUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.sofproject.gst.topo.model.GstTopoGraph;

public class JsonCustomOptionPane {

	ITopoGraph graph;

	private static boolean isInteger(String s) {
		try {
			Integer.parseInt(s);
		} catch (NumberFormatException e) {
			return false;
		} catch (NullPointerException e) {
			return false;
		}
		return true;
	}

	public JsonCustomOptionPane(Display display, ITopoGraph graph) {
		this.graph = graph;
		Shell shell = new Shell(display, SWT.CLOSE | SWT.TITLE | SWT.MIN);

		shell.setText("Serialize JSON");

		GridLayout gridLayout = new GridLayout(4, false);
		gridLayout.verticalSpacing = 8;
		shell.setLayout(gridLayout);

		new Label(shell, SWT.NULL).setText("Name:");
		Text nameText = new Text(shell, SWT.SINGLE | SWT.BORDER);
		GridData nameGridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		nameGridData.horizontalSpan = 3;
		nameText.setLayoutData(nameGridData);

		new Label(shell, SWT.NULL).setText("Version:");
		Text versionText = new Text(shell, SWT.BORDER);
		GridData versionGridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		versionGridData.horizontalSpan = 3;
		versionText.setLayoutData(versionGridData);

		new Label(shell, SWT.NULL).setText("Description:");
		Text descriptionText = new Text(shell, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL);
		GridData descriptionGridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		descriptionGridData.horizontalSpan = 3;
		descriptionGridData.heightHint = 100;
		descriptionGridData.widthHint = 200;
		descriptionText.setLayoutData(descriptionGridData);

		new Label(shell, SWT.NULL).setText("Type:");
		Combo typeCombo = new Combo(shell, SWT.READ_ONLY);
		typeCombo.setBounds(50, 50, 200, 65);
		String items[] = { "Gstreamer", "Ffmpeg" };
		typeCombo.setItems(items);
		typeCombo.setText(items[0]);
		GridData typeGridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		typeGridData.horizontalSpan = 3;
		typeCombo.setLayoutData(typeGridData);

		Button okButton = new Button(shell, SWT.PUSH);
		okButton.setText("Ok");
		GridData buttonGridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		okButton.setLayoutData(buttonGridData);
		okButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				if (nameText.getText().isEmpty()) {
					MessageBox messageBox = new MessageBox(shell, SWT.ERROR);
					messageBox.setMessage("Name cannot be empty!");
					messageBox.open();
				} else if (!isInteger(versionText.getText())) {
					MessageBox messageBox = new MessageBox(shell, SWT.ERROR);
					messageBox.setMessage("Version number should be an integer!");
					messageBox.open();
				} else {
					try {
						JsonProperty jsonProperty = new JsonProperty(nameText.getText(), descriptionText.getText(),
								versionText.getText(), typeCombo.getItem(typeCombo.getSelectionIndex()));
						new JsonUtils().serializeJson(jsonProperty, graph.getPipelineString());
						shell.close();
					} catch (CoreException | IOException error) {
						error.printStackTrace(); // TODO:
					}
				}
			}
		});

		Button cancelButton = new Button(shell, SWT.PUSH);
		cancelButton.setText("Cancel");
		GridData cancelbuttonGridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		cancelButton.setLayoutData(cancelbuttonGridData);
		cancelButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				shell.close();
			}
		});

		Monitor primary = display.getPrimaryMonitor();
		Rectangle bounds = primary.getBounds();
		Rectangle rect = shell.getBounds();
		int x = bounds.x + (bounds.width - rect.width) / 2;
		int y = bounds.y + (bounds.height - rect.height) / 2;
		shell.setLocation(x, y);

		shell.pack();
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
	}

}
