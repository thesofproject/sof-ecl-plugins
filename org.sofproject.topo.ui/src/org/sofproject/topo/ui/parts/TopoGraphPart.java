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

package org.sofproject.topo.ui.parts;

import java.util.List;

import org.eclipse.gef.mvc.fx.parts.IVisualPart;
import org.eclipse.gef.zest.fx.parts.GraphPart;
import org.sofproject.topo.ui.graph.GefTopoEdge;
import org.sofproject.topo.ui.graph.GefTopoNode;
import org.sofproject.topo.ui.graph.ITopoConnection;
import org.sofproject.topo.ui.graph.ITopoNode;

import javafx.scene.Node;

public class TopoGraphPart extends GraphPart {

	private Object lastChildAdded = null;

	@Override
	protected void doAddContentChild(Object contentChild, int index) {
		if (contentChild instanceof GefTopoNode) {
			getContent().getNodes().add((GefTopoNode) contentChild);
			lastChildAdded = contentChild;
		} else if (contentChild instanceof GefTopoEdge) {
			getContent().getEdges().add((GefTopoEdge) contentChild);
		}
	}

	@Override
	protected void doRemoveContentChild(Object contentChild) {
		if (contentChild instanceof GefTopoNode) {

			ITopoNode topoNode = ((GefTopoNode) contentChild).getTopoModelNode();
			topoNode.getParentGraph().removeNode(topoNode);

			getContent().getNodes().remove(contentChild);
		} else if (contentChild instanceof GefTopoEdge) {
			ITopoConnection topoConnection = ((GefTopoEdge) contentChild).getTopoModelConnection();
			topoConnection.getParentGraph().removeConnection(topoConnection);

			getContent().getEdges().remove(contentChild);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	protected List<? extends Object> doGetContentChildren() {
		// FIXME: dirty hack to w/a GraphPart behavior
		// It returns nodes than edges while add() action
		// requires the new element to be returned on the last position

		List<Object> cc = (List<Object>) super.doGetContentChildren();

		if (lastChildAdded != null) {
			cc.remove(lastChildAdded);
			cc.add(lastChildAdded);
			lastChildAdded = null;
		}
		return cc;
	}

	@Override
	public void reorderChild(IVisualPart<? extends Node> child, int index) {
		// FIXME: do nothing here, there is annother hack above to satisfy add().
	}
}
