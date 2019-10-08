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

package org.sofproject.ui.views;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.part.ViewPart;
import org.sofproject.core.connection.SofNodeConnection;
import org.sofproject.core.connection.SofNodeConnectionManager;
import org.sofproject.core.ops.IRemoteOpsProvider;
import org.sofproject.ui.ops.SofOpRunner;
import org.sofproject.ui.ops.SofRemoteOpsProvider;
import org.sofproject.ui.ops.StdRemoteOpsProvider;

public class SofNodeConnectionsViewPart extends ViewPart {

	private TableViewer viewer;
	private List<IRemoteOpsProvider> opsProviders = new ArrayList<>();

	// TODO: should re-pack the layout on change?

	@Override
	public void createPartControl(Composite parent) {
		viewer = new TableViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);

		TableViewerColumn colProjName = new TableViewerColumn(viewer, SWT.NONE);
		colProjName.getColumn().setWidth(200);
		colProjName.getColumn().setText("Project");
		colProjName.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				SofNodeConnection conn = (SofNodeConnection) element;
				return conn.getProject().getProject().getName();
			}
		});

		TableViewerColumn colNodeAddr = new TableViewerColumn(viewer, SWT.NONE);
		colNodeAddr.getColumn().setWidth(200);
		colNodeAddr.getColumn().setText("Address");
		colNodeAddr.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				SofNodeConnection conn = (SofNodeConnection) element;
				return conn.getNodeDescriptor().getAddr();
			}
		});

		TableViewerColumn colResPath = new TableViewerColumn(viewer, SWT.NONE);
		colResPath.getColumn().setWidth(200);
		colResPath.getColumn().setText("Resource Path");
		colResPath.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				SofNodeConnection conn = (SofNodeConnection) element;
				return conn.getProject().getNodeDescriptor().getResPath();
			}
		});

		TableViewerColumn colConnected = new TableViewerColumn(viewer, SWT.NONE);
		colConnected.getColumn().setWidth(100);
		colConnected.getColumn().setText("Connected");
		colConnected.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				SofNodeConnection conn = (SofNodeConnection) element;
				return conn.isConnected() ? "yes" : "no";
			}
		});

		viewer.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
//				IStructuredSelection selection = viewer.getStructuredSelection();
//				Object sel = selection.getFirstElement();
				// TODO: update state of toolbar items
			}
		});
		viewer.setContentProvider(new ArrayContentProvider());

		viewer.getTable().setHeaderVisible(true);
		viewer.getTable().setLinesVisible(true);

		IActionBars actionBars = getViewSite().getActionBars();
		IMenuManager menuManager = actionBars.getMenuManager();

		opsProviders.add(new StdRemoteOpsProvider());
		opsProviders.add(new SofRemoteOpsProvider());

		for (IConfigurationElement cfg : Platform.getExtensionRegistry()
				.getConfigurationElementsFor("org.sofproject.core.remoteopsproviders")) {
			try {
				Object provider = cfg.createExecutableExtension("class");
				if (provider instanceof IRemoteOpsProvider) {
					opsProviders.add((IRemoteOpsProvider) provider);
				}
			} catch (CoreException e) {
				e.printStackTrace();
			}
		}


		for (IRemoteOpsProvider prov : opsProviders) {
			for (String opId : prov.getRemoteOpsIds()) {
				menuManager.add(new Action(prov.getRemoteOpDisplayName(opId)) {
					@Override
					public void run() {
						runOp(prov, opId);
					}
				});
			}
		}

		SofNodeConnectionManager connMgr = SofNodeConnectionManager.getInstance();
		viewer.setInput(connMgr.getConnections());
		connMgr.addObserver(new Observer() {
			@Override
			public void update(Observable o, Object arg) {
				Display.getDefault().asyncExec(new Runnable() {
					@Override
					public void run() {
						viewer.refresh();
					}
				});
			}
		});
	}

	private void runOp(IRemoteOpsProvider prov, String opId) {
		Object el = viewer.getStructuredSelection().getFirstElement();
		if (el != null) {
			SofNodeConnection conn = (SofNodeConnection) el;
			SofOpRunner.runOp(prov.createRemoteOp(opId, conn));
			viewer.refresh();
		}
	}

	@Override
	public void setFocus() {
		viewer.getControl().setFocus();
	}

}
