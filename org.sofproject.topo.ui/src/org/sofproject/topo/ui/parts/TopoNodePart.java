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

package org.sofproject.topo.ui.parts;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.gef.zest.fx.behaviors.GraphLayoutBehavior;
import org.eclipse.gef.zest.fx.parts.NodePart;
import org.sofproject.topo.ui.graph.GefTopoNode;
import org.sofproject.topo.ui.visuals.TopoNodeVisual;

import javafx.scene.Group;

public class TopoNodePart extends NodePart implements PropertyChangeListener {

	@Override
	protected Group doCreateVisual() {
		return new Group(new TopoNodeVisual(getContent()));
	}

	@Override
	public GefTopoNode getContent() {
		return (GefTopoNode) super.getContent();
	}

	@Override
	protected void doActivate() {
		super.doActivate();
		getContent().addPropertyChangeListener(this);
	}

	@Override
	protected void doDeactivate() {
		super.doDeactivate();
		getContent().removePropertyChangeListener(this);
	}

	@Override
	protected void doRefreshVisual(Group visual) {
		TopoNodeVisual tnv = (TopoNodeVisual) visual.getChildrenUnmodifiable().get(0);
		tnv.setName(getContent().getTopoModelNode().getName());
		super.doRefreshVisual(visual);
	}

	@Override
	public void propertyChange(PropertyChangeEvent e) {
		if (e.getPropertyName() == GefTopoNode.PROP_NODE_NAME) {
			refreshVisual();
			GraphLayoutBehavior glb = getParent().getAdapter(GraphLayoutBehavior.class);
			glb.applyLayout(true, null);
		}
	}
}
