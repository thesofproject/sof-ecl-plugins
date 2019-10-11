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

package org.sofproject.gst.topo.ui.handlers;

import org.eclipse.jface.dialogs.TrayDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.sofproject.gst.topo.plugins.GstElement;
import org.sofproject.gst.topo.plugins.GstPluginDb;

public class GstNewElementDialog extends TrayDialog {

	private GstPluginDb plgDb;
	private TableViewer viewer;
	private GstElement selectedElement;

	public GstNewElementDialog(Shell parentShell, GstPluginDb plgDb) {
		super(parentShell);
		this.plgDb = plgDb;
	}

	public GstElement getSelectedElement() {
		return selectedElement;
	}

	@Override
	protected void configureShell(Shell shell) {
		super.configureShell(shell);
		shell.setText("Select New Element");
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		Composite mainGroup = new Composite(container, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		mainGroup.setLayout(layout);
		mainGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		viewer = new TableViewer(mainGroup, SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);

		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		gridData.heightHint = 15 * viewer.getTable().getItemHeight();
		viewer.getTable().setLayoutData(gridData);

		TableViewerColumn colPlugin = new TableViewerColumn(viewer, SWT.NONE);
		colPlugin.getColumn().setWidth(200);
		colPlugin.getColumn().setText("Plugin");
		colPlugin.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return ((GstElement) element).getParent().getName();
			}
		});

		TableViewerColumn colElement = new TableViewerColumn(viewer, SWT.NONE);
		colElement.getColumn().setWidth(200);
		colElement.getColumn().setText("Element");
		colElement.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return ((GstElement) element).getName();
			}
		});

		viewer.setContentProvider(new ArrayContentProvider());
		viewer.getTable().setHeaderVisible(true);
		viewer.getTable().setLinesVisible(true);

		viewer.setInput(plgDb.getAllElements());
		viewer.getControl().setFocus();

		return container;
	}

	@Override
	protected void okPressed() {
		Object el = viewer.getStructuredSelection().getFirstElement();
		if (el instanceof GstElement) {
			selectedElement = (GstElement) el;
		}
		super.okPressed();
	}
}
