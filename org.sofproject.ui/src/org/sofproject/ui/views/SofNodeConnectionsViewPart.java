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

import java.lang.reflect.InvocationTargetException;
import java.util.Observable;
import java.util.Observer;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.console.MessageConsoleStream;
import org.eclipse.ui.part.ViewPart;
import org.sofproject.core.connection.SofNodeConnection;
import org.sofproject.core.connection.SofNodeConnectionManager;
import org.sofproject.core.connection.SofRemoteOperation;
import org.sofproject.ui.console.SofConsole;
import org.sofproject.ui.handlers.SofNodeLoginDialog;

public class SofNodeConnectionsViewPart extends ViewPart {

	private TableViewer viewer;

	// TODO: should re-pack the layout on change?

	// TODO: connection is duplicated for the first time a first project is created?

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
//		IMenuManager menuManager = actionBars.getMenuManager();
		IToolBarManager toolbarManager = actionBars.getToolBarManager();

		toolbarManager.add(new Action("Import Files") {
			@Override
			public void run() {
				runOp(SofNodeConnection.createImportResourcesOp(), null, null);
			}
		});

		toolbarManager.add(new Action("Open dmesg") {
			@Override
			public void run() {
				runOp(SofNodeConnection.createConnectDmesgOp(), "dmesg", SofConsole.TYPE_DMESG);
			}
		});

		toolbarManager.add(new Action("Open logger") {
			@Override
			public void run() {
				runOp(SofNodeConnection.createConnectLoggerOp(), "logger", SofConsole.TYPE_LOG);
			}
		});

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

	private void runOp(SofRemoteOperation op, String consNameExt, String consType) {
		Object el = viewer.getStructuredSelection().getFirstElement();
		if (el != null) {
			SofNodeConnection conn = (SofNodeConnection) el;
			try {
				connectNode(conn);
				if (!conn.isConnected()) {
					/* aborted or authentication failure */
					return;
				}
				op.setConnection(conn);

				// now, when the connection is established, let's create an output console
				MessageConsoleStream mcs = null;
				if (consNameExt != null) {
					mcs = SofConsole.getConsoleStream(conn.getProject().getProject().getName() + "." + consNameExt,
							consType, conn.getProject());
				}

				op.setOutputStream(mcs);

				new ProgressMonitorDialog(null).run(true, false, new IRunnableWithProgress() {

					@Override
					public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
						op.run(monitor);
					}
				});
			} catch (CoreException | InvocationTargetException e) {
				MessageDialog.openError(null, "Operation failed: ", e.getMessage());
			} catch (InterruptedException e) {
				MessageDialog.openInformation(null, "Operation canceled", "Operation canceled");
			}
			viewer.refresh();
		}
	}

	private void connectNode(SofNodeConnection conn) throws CoreException {
		if (conn.isConnected())
			return;

		SofNodeLoginDialog dlg = new SofNodeLoginDialog(null, conn.getProject().getProject().getName(),
				conn.getNodeDescriptor().getAddr());
		if (dlg.open() == Window.OK) {
			conn.connect(dlg.getLogin(), dlg.getPass());
		}
		return;
	}

	@Override
	public void setFocus() {
		viewer.getControl().setFocus();
	}

}
