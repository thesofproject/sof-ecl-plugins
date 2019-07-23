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

package org.sofproject.topo.ui.parts.feedback;

import org.eclipse.gef.common.adapt.AdapterKey;
import org.eclipse.gef.fx.anchors.IAnchor;
import org.eclipse.gef.fx.anchors.StaticAnchor;
import org.eclipse.gef.geometry.convert.fx.FX2Geometry;
import org.eclipse.gef.geometry.convert.fx.Geometry2FX;
import org.eclipse.gef.geometry.planar.Point;
import org.eclipse.gef.mvc.fx.parts.AbstractFeedbackPart;
import org.eclipse.gef.mvc.fx.parts.IVisualPart;
import org.sofproject.topo.ui.visuals.TopoConnectionVisual;

import com.google.common.reflect.TypeToken;
import com.google.inject.Provider;

import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;

public class NewTopoConnectionFeedbackPart extends AbstractFeedbackPart<Node> {

	private class PointerAnchor extends StaticAnchor implements EventHandler<MouseEvent> {

		public PointerAnchor(Point refPos) {
			super(refPos);
			getRoot().getVisual().getScene().addEventHandler(MouseEvent.MOUSE_MOVED, this);
		}

		@Override
		public void handle(MouseEvent event) {
			Point v = new Point(event.getSceneX(), event.getSceneY());
			referencePositionProperty().setValue(v);
		}

		public void dispose() {
			getRoot().getVisual().getScene().removeEventHandler(MouseEvent.MOUSE_MOVED, this);
		}
	}

	@Override
	public void doAttachToAnchorageVisual(IVisualPart<? extends Node> anchorage, String role) {
		TopoConnectionVisual v = getVisual();

		@SuppressWarnings("serial")
		Provider<? extends IAnchor> adapter = anchorage
				.getAdapter(AdapterKey.get(new TypeToken<Provider<? extends IAnchor>>() {
				}));
		v.setStartAnchor(adapter.get());
		v.setEndAnchor(
				new PointerAnchor(FX2Geometry.toPoint(v.localToScene(Geometry2FX.toFXPoint(v.getStartPoint())))));
	}

	@Override
	protected Node doCreateVisual() {
		return new TopoConnectionVisual();
	}

	@Override
	public TopoConnectionVisual getVisual() {
		return (TopoConnectionVisual) super.getVisual();
	}

	@Override
	protected void doDetachFromAnchorageVisual(IVisualPart<? extends Node> anchorage, String role) {
		TopoConnectionVisual v = getVisual();
		((PointerAnchor) v.getEndAnchor()).dispose();
		v.setStartPoint(v.getStartPoint());
		v.setEndPoint(v.getEndPoint());
	}

	@Override
	protected void doRefreshVisual(Node visual) {
		// abstract - need to provide this empty one
	}

}
