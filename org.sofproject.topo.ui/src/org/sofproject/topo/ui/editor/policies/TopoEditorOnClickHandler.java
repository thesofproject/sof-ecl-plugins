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

package org.sofproject.topo.ui.editor.policies;

import org.eclipse.gef.mvc.fx.handlers.AbstractHandler;
import org.eclipse.gef.mvc.fx.handlers.IOnClickHandler;
import org.eclipse.gef.mvc.fx.parts.IContentPart;
import org.eclipse.gef.mvc.fx.parts.IRootPart;
import org.eclipse.gef.mvc.fx.parts.IVisualPart;
import org.eclipse.gef.mvc.fx.policies.CreationPolicy;
import org.eclipse.gef.mvc.fx.viewer.IViewer;
import org.eclipse.gef.mvc.fx.viewer.InfiniteCanvasViewer;
import org.sofproject.topo.ui.graph.GefTopoNode;
import org.sofproject.topo.ui.graph.ITopoGraph;
import org.sofproject.topo.ui.graph.TopoZestGraphBuilder;
import org.sofproject.topo.ui.models.TopoItemCreationModel;
import org.sofproject.topo.ui.parts.TopoGraphPart;

import com.google.common.collect.HashMultimap;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.input.MouseEvent;

public class TopoEditorOnClickHandler extends AbstractHandler implements IOnClickHandler {

	@Override
	public void click(MouseEvent e) {
		if (e.isSecondaryButtonDown()) {
			IViewer viewer = getHost().getRoot().getViewer();
			TopoItemCreationModel creationModel = viewer.getAdapter(TopoItemCreationModel.class);

			IVisualPart<? extends Node> part = getHost().getRoot().getChildrenUnmodifiable().get(0);
			if (part instanceof TopoGraphPart) {
				ITopoGraph topoModel = ((ITopoGraph) ((TopoGraphPart) part).getContent().getAttributes()
						.get(TopoZestGraphBuilder.TOPO_MODEL_ATTR));
				String[] nodeIds = topoModel.getNodeTypeIds();

				ContextMenu menu = new ContextMenu();

				for (String nodeId : nodeIds) {
					MenuItem mi = new MenuItem("New " + topoModel.getNodeDisplayName(nodeId));
					mi.setOnAction(new EventHandler<ActionEvent>() {
						@Override
						public void handle(ActionEvent event) {
							creationModel.setType(TopoItemCreationModel.Type.Node);
							creationModel.setObjectId(nodeId);
							createNode(e);
						}
					});
					menu.getItems().add(mi);
				}
				menu.getItems().add(new SeparatorMenuItem());
				MenuItem miSerialize = new MenuItem("Serialize Topology");
				miSerialize.setOnAction(new EventHandler<ActionEvent>() {
					@Override
					public void handle(ActionEvent event) {
						System.out.println("TODO: Serializing topology here...");
					}
				});
				menu.getItems().add(miSerialize);
				menu.show(((InfiniteCanvasViewer) viewer).getScene().getWindow(), e.getScreenX(), e.getScreenY());
			}
		}
	}

	private void createNode(MouseEvent e) {
		IViewer viewer = getHost().getRoot().getViewer();
		TopoItemCreationModel creationModel = viewer.getAdapter(TopoItemCreationModel.class);

		IVisualPart<? extends Node> part = getHost().getRoot().getChildrenUnmodifiable().get(0);
		if (part instanceof TopoGraphPart) { //TODO: need to test it again?
			ITopoGraph topoModel = ((ITopoGraph) ((TopoGraphPart) part).getContent().getAttributes()
					.get(TopoZestGraphBuilder.TOPO_MODEL_ATTR));
			GefTopoNode gefNode = new GefTopoNode(topoModel.createNode(creationModel.getObjectId()));

			IRootPart<? extends Node> root = getHost().getRoot();
			CreationPolicy creationPolicy = root.getAdapter(CreationPolicy.class);
			init(creationPolicy);
			creationPolicy.create(gefNode, part, HashMultimap.<IContentPart<? extends Node>, String>create());
			creationPolicy.commit();
		}
		creationModel.setType(TopoItemCreationModel.Type.None);
	}

}
