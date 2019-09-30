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

package org.sofproject.topo.ui.outline;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider.IStyledLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.contentoutline.ContentOutlinePage;
import org.sofproject.topo.ui.editor.TopoEditor;
import org.sofproject.topo.ui.graph.ITopoCollectionNode;
import org.sofproject.topo.ui.graph.ITopoElement;
import org.sofproject.topo.ui.graph.ITopoGraph;
import org.sofproject.topo.ui.graph.ITopoNode;
import org.sofproject.topo.ui.graph.ITopoNodeAttribute;

public class TopoGraphContentOutlinePage extends ContentOutlinePage implements PropertyChangeListener {
	ITopoGraph topoModel;

	public TopoGraphContentOutlinePage(TopoEditor editor) {
		topoModel = editor.getTopoModel();
		topoModel.addPropertyChangeListener(this);
	}

	@Override
	public void createControl(Composite parent) {
		super.createControl(parent);
		TreeViewer viewer = getTreeViewer();
		viewer.setContentProvider(new TopoGraphContentProvider(this));
		viewer.setLabelProvider(new DelegatingStyledCellLabelProvider(new AlsaTopoGraphLabelProvider()));
		viewer.addSelectionChangedListener(this);
		viewer.setInput(topoModel);
	}

	@Override
	public void dispose() {
		topoModel.removePropertyChangeListener(this);
		topoModel = null;
		super.dispose();
	}

	class AlsaTopoGraphLabelProvider extends LabelProvider implements IStyledLabelProvider {

		@Override
		public StyledString getStyledText(Object element) {

			if (element instanceof ITopoCollectionNode) {
				ITopoCollectionNode topoCol = (ITopoCollectionNode) element;
				StyledString s = new StyledString(topoCol.getName()).append(" ");
				s.append(String.format("[%d]", topoCol.size()), StyledString.DECORATIONS_STYLER);
				return s;
			} else if (element instanceof ITopoNode) {
				return new StyledString(((ITopoNode) element).getName());
			} else if (element instanceof ITopoElement) {
				return new StyledString(((ITopoElement) element).getName());
			} else if (element instanceof ITopoNodeAttribute) {
				ITopoNodeAttribute attr = (ITopoNodeAttribute) element;
				StyledString s = new StyledString(attr.getName()).append(" : ", StyledString.COUNTER_STYLER)
						.append(attr.getStringValue(), StyledString.COUNTER_STYLER);
				return s;
			}

			return new StyledString(element.toString());
		}

	}

	@Override
	public void propertyChange(PropertyChangeEvent arg0) {
		// TODO: narrow down the changed area
		getTreeViewer().refresh();

	}
}
