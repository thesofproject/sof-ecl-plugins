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
package org.sofproject.ui.views;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.debug.ui.console.FileLink;
import org.eclipse.jface.resource.FontDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.ViewPart;
import org.sofproject.core.connection.SofNodeConnection;

public class SofLoggerViewPart extends ViewPart {

	public static final String ID = "org.sofproject.ui.views.SofLoggerView";

	private TableViewer viewer;
	private Text grepText;

	private LogEntryFilter filter;
	private TokenizedLoggerOutput logOutput;

	private Color locationColor;
	private Font locationFont;

	@Override
	public void createPartControl(Composite parent) {
		locationColor = new Color(parent.getFont().getDevice(), 0, 0, 255);

		FontDescriptor fd = JFaceResources.getDefaultFontDescriptor().setStyle(SWT.ITALIC | SWT.UNDERLINE_SINGLE);
		locationFont = fd.createFont(parent.getFont().getDevice());

		GridLayout layout = new GridLayout(2, false);
		parent.setLayout(layout);
		Label grepLabel = new Label(parent, SWT.NONE);
		grepLabel.setText("Grep Comp: ");
		grepText = new Text(parent, SWT.BORDER | SWT.SEARCH);
		GridData gd = new GridData();
		gd.grabExcessHorizontalSpace = true;
		gd.horizontalAlignment = GridData.FILL;
		grepText.setLayoutData(gd);
		createViewer(parent);
		grepText.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent ke) {
				filter.setFilterText(grepText.getText());
				viewer.refresh();
			}
		});
		filter = new LogEntryFilter();
		viewer.addFilter(filter);
	}

	private void createViewer(Composite parent) {
		viewer = new TableViewer(parent,
				/* SWT.MULTI | */ SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);

		TableViewerColumn col = addColumn("Core", 50, 0);
		col.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				return ((String[]) element)[0];
			}

		});
		col = addColumn("Level", 50, 1);
		col.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				return ((String[]) element)[1];
			}

		});
		col = addColumn("Comp ID", 100, 2);
		col.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				return ((String[]) element)[2];
			}

		});
		col = addColumn("Timestamp", 150, 3);
		col.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				return ((String[]) element)[3];
			}

		});
		col = addColumn("TS Delta", 100, 4);
		col.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				return ((String[]) element)[4];
			}

		});
		col = addColumn("Content", 250, 5);
		col.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				String[] s = (String[]) element;
				StringBuffer sb = new StringBuffer();
				for (int i = 6; i < s.length; i++) {
					sb.append(s[i]).append(" ");
				}
				return sb.toString();
			}

		});

		col = addColumn("Location", 250, 6);
		col.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				String[] s = (String[]) element;
				return s[5].substring(1, s[5].length() - 1); // remove ( and )
			}

			@Override
			public Color getForeground(Object element) {
				return locationColor;
			}

			@Override
			public Font getFont(Object element) {
				return locationFont;
			}

		});

		Table table = viewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		viewer.setContentProvider(new ArrayContentProvider());
		viewer.setInput(null);

		GridData gridData = new GridData();
		gridData.verticalAlignment = GridData.FILL;
		gridData.horizontalSpan = 2;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		viewer.getControl().setLayoutData(gridData);

		viewer.addDoubleClickListener(new IDoubleClickListener() {

			@Override
			public void doubleClick(DoubleClickEvent event) {
				IStructuredSelection selection = (IStructuredSelection) event.getSelection();
				if (selection.getFirstElement() instanceof String[]) {
					String[] entry = (String[]) selection.getFirstElement();
					String[] locStr = entry[5].substring(1, entry[5].length() - 1).split(":");
					String fileName = locStr[0];
					int lineNo = Integer.parseInt(locStr[1]);

					IFile file = logOutput.getSrcProj().getFile(fileName);
					FileLink fileLink = new FileLink(file, null, -1, -1, lineNo);
					fileLink.linkActivated();
				}

			}
		});
	}

	private TableViewerColumn addColumn(String title, int width, int index) {
		TableViewerColumn vc = new TableViewerColumn(viewer, SWT.NONE);
		TableColumn tc = vc.getColumn();
		tc.setText(title);
		tc.setWidth(width);
		tc.setResizable(true);
		return vc;
	}

	public OutputStream getOutputStream(SofNodeConnection conn) {
		logOutput = new TokenizedLoggerOutput(conn);
		setPartName("SOF log from " + conn.getNodeDescriptor().getAddr());
		viewer.setInput(logOutput.getLogLines());
		return logOutput;
	}

	@Override
	public void setFocus() {
		viewer.getControl().setFocus();

	}

	private class LogEntryFilter extends ViewerFilter {

		private String filter;

		public void setFilterText(String filter) {
			this.filter = ".*" + filter.toUpperCase() + ".*";
		}

		@Override
		public boolean select(Viewer viewer, Object parentElement, Object element) {
			if (filter == null || filter.isEmpty()) {
				return true;
			}
			String[] entry = (String[]) element;
			if (entry[2].matches(filter)) {
				return true;
			}
			return false;
		}

	}

	private class TokenizedLoggerOutput extends OutputStream {

		private List<String[]> logLines;
		private static final int MAX_LINES = 2000;
		private StringBuffer curLine;

		IProject srcProj;

		public TokenizedLoggerOutput(SofNodeConnection conn) {
			logLines = new ArrayList<>(MAX_LINES);
			curLine = new StringBuffer();

			IWorkspace workspace = ResourcesPlugin.getWorkspace();
			IWorkspaceRoot root = workspace.getRoot();

			srcProj = root.getProject(conn.getProject().getSrcProjName());
		}

		public List<String[]> getLogLines() {
			return logLines;
		}

		public IProject getSrcProj() {
			return srcProj;
		}

		@Override
		public void write(int b) throws IOException {
			char c = (char) b;
			if (c == '\n') {
				tokenize();
			} else {
				curLine.append(c);
			}
		}

		private void tokenize() {
			if (logLines.size() == MAX_LINES) {
				logLines.remove(0);
			}
			logLines.add(curLine.toString().split("\\s+"));
			curLine.delete(0, curLine.length());
			Display.getDefault().asyncExec(new Runnable() {
				@Override
				public void run() {
					viewer.refresh();
					viewer.reveal(logLines.get(logLines.size() - 1));
				}
			});
		}

	}
}
