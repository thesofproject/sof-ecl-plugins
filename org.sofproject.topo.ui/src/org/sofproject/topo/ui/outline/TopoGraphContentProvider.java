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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.sofproject.topo.ui.graph.ITopoCollectionNode;
import org.sofproject.topo.ui.graph.ITopoElement;
import org.sofproject.topo.ui.graph.ITopoGraph;
import org.sofproject.topo.ui.graph.ITopoNode;

/**
 * The content provider for topology outline works on the topology model level
 * using ITopoGraph interface. So that the outline may use a different
 * structure, show some domain specific dependencies not visualized in the graph
 * editor directly.
 */
public class TopoGraphContentProvider implements ITreeContentProvider {

	private TopoGraphContentOutlinePage parentPage;

	public TopoGraphContentProvider(TopoGraphContentOutlinePage parentPage) {
		this.parentPage = parentPage;
	}

	@Override
	public Object[] getElements(Object inputElement) {
		if (inputElement instanceof ITopoGraph) {
			return ((ITopoGraph) inputElement).getCollections().toArray();
		}
		return null;
	}

	@Override
	public Object[] getChildren(Object parentElement) {

		if (parentElement instanceof ITopoCollectionNode) {
			Collection<? extends ITopoNode> nodes = ((ITopoCollectionNode)parentElement).getChildren();
			for (ITopoNode node : nodes) {
				node.addPropertyChangeListener(parentPage);
			}
			return nodes.toArray();
		} else if (parentElement instanceof ITopoNode) {

			ITopoNode node = (ITopoNode) parentElement;
			node.addPropertyChangeListener(parentPage);

			List<Object> all = new ArrayList<>(node.getChildElements().size() + node.getAttributes().size());
			all.addAll(node.getChildElements());
			all.addAll(node.getAttributes());
			return all.toArray();
		} else if (parentElement instanceof ITopoElement) {
			return ((ITopoElement) parentElement).getAttributes().toArray();
		}

		return null;
	}

	@Override
	public Object getParent(Object element) {

		if (element instanceof ITopoNode) {
			return ((ITopoNode) element).getParent();
		}

		return null;
	}

	@Override
	public boolean hasChildren(Object element) {

		if (element instanceof ITopoCollectionNode) {
			return !((ITopoCollectionNode) element).getChildren().isEmpty();
		} else if (element instanceof ITopoNode) {
			return !((ITopoNode) element).getAttributes().isEmpty();
		} else if (element instanceof ITopoElement) {
			return !((ITopoElement) element).getAttributes().isEmpty();
		}

		return false;
	}
}
