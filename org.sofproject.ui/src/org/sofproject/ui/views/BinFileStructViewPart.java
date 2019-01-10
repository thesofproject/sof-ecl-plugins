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

import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider.IStyledLabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.ViewPart;
import org.sofproject.core.binfile.BinFile;
import org.sofproject.core.binfile.BinItem;
import org.sofproject.core.binfile.BinStruct;
import org.sofproject.ui.editor.IBinFileEditor;
import org.sofproject.ui.editor.IBinStructHolder;

// TODO: use ILinkedWithEditor instead of listening on all selection changes?

public class BinFileStructViewPart extends ViewPart {

	private TreeViewer treeViewer;

	@Override
	public void createPartControl(Composite parent) {
		treeViewer = new TreeViewer(parent);
		treeViewer.getTree().setHeaderVisible(true);
		treeViewer.getTree().setLinesVisible(true);

		treeViewer.setLabelProvider(
				new DelegatingStyledCellLabelProvider(new BinItemLabelProvider()));
		treeViewer.setContentProvider(new BinFileStructContentProvider());
		treeViewer.setAutoExpandLevel(2); // TODO: find optimal level

		getSite().getPage().addPostSelectionListener(new ISelectionListener() {

			@Override
			public void selectionChanged(IWorkbenchPart part, ISelection selection) {
				// System.out.println("selection changed: " + selection);
				IEditorPart editor = part.getSite().getPage().getActiveEditor();
				if (editor instanceof IBinFileEditor) {
					BinFile tplg = ((IBinFileEditor) editor).getBinFile();
					if (treeViewer.getInput() != tplg) {
						treeViewer.setInput(tplg);
					}
					if (selection instanceof IStructuredSelection) {
						IStructuredSelection sel = (IStructuredSelection) selection;
						if (sel.getFirstElement() instanceof IBinStructHolder) {
							BinStruct bin = (((IBinStructHolder) sel.getFirstElement())
									.getBinStruct());
							TreePath path = new TreePath(new Object[] { bin.getParent(), bin });
							treeViewer.setSelection(new TreeSelection(path), true);
							treeViewer.setExpandedState(path, true);
						}
					}
				} else {
					treeViewer.setInput(null);
				}

			}
		});
	}

	@Override
	public void setFocus() {
		treeViewer.getControl().setFocus();
	}

	class BinItemLabelProvider extends LabelProvider implements IStyledLabelProvider {

		@Override
		public StyledString getStyledText(Object element) {
			if (element instanceof BinItem) {
				BinItem item = (BinItem) element;
				StyledString s = new StyledString(String.format("0x%x  ", item.getOffset()),
						StyledString.DECORATIONS_STYLER);
				s.append(item.getName());
				String valS = item.getValueString();
				if (!valS.isEmpty())
					s.append(" :  " + item.getValueString(), StyledString.COUNTER_STYLER);
				return s;
			}
			// TODO Auto-generated method stub
			return new StyledString("?");
		}

	}
}
