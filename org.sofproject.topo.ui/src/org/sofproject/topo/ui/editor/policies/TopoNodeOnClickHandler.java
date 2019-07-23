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

import java.util.ArrayList;

import org.eclipse.gef.mvc.fx.handlers.AbstractHandler;
import org.eclipse.gef.mvc.fx.handlers.IOnClickHandler;
import org.eclipse.gef.mvc.fx.models.HoverModel;
import org.eclipse.gef.mvc.fx.parts.IContentPart;
import org.eclipse.gef.mvc.fx.parts.IRootPart;
import org.eclipse.gef.mvc.fx.parts.IVisualPart;
import org.eclipse.gef.mvc.fx.policies.CreationPolicy;
import org.eclipse.gef.mvc.fx.policies.DeletionPolicy;
import org.eclipse.gef.mvc.fx.viewer.IViewer;
import org.eclipse.gef.mvc.fx.viewer.InfiniteCanvasViewer;
import org.eclipse.gef.zest.fx.parts.EdgePart;
import org.sofproject.topo.ui.graph.GefTopoEdge;
import org.sofproject.topo.ui.graph.GefTopoNode;
import org.sofproject.topo.ui.graph.ITopoGraph;
import org.sofproject.topo.ui.graph.TopoZestGraphBuilder;
import org.sofproject.topo.ui.models.TopoItemCreationModel;
import org.sofproject.topo.ui.parts.TopoGraphPart;
import org.sofproject.topo.ui.parts.TopoNodeCollectionPart;
import org.sofproject.topo.ui.parts.TopoNodePart;

import com.google.common.collect.HashMultimap;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.input.MouseEvent;

public class TopoNodeOnClickHandler extends AbstractHandler implements IOnClickHandler {

	@Override
	public void click(MouseEvent e) {
		if (getHost() instanceof TopoNodeCollectionPart)
			return; // do nothing if collection node is clicked

		if (e.isSecondaryButtonDown()) {
			IViewer viewer = getHost().getRoot().getViewer();

			MenuItem newConnectionItem = new MenuItem("New Connection");
			newConnectionItem.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					IViewer viewer = getHost().getViewer();
					TopoItemCreationModel cm = viewer.getAdapter(TopoItemCreationModel.class);
					cm.setType(TopoItemCreationModel.Type.Connection);
					cm.setSource((TopoNodePart) getHost());
				}
			});

			MenuItem deleteNodeItem = new MenuItem("Delete Node");
			deleteNodeItem.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					HoverModel hover = getHost().getViewer().getAdapter(HoverModel.class);
					if (getHost() == hover.getHover()) {
						hover.clearHover();
					}

					IRootPart<? extends Node> root = getHost().getRoot();
					DeletionPolicy deletionPolicy = root.getAdapter(DeletionPolicy.class);
					init(deletionPolicy);
					for (IVisualPart<? extends Node> edge : new ArrayList<>(getHost().getAnchoredsUnmodifiable())) {
						if (edge instanceof EdgePart) {
							deletionPolicy.delete((IContentPart<? extends Node>) edge);
						}
					}

					deletionPolicy.delete((IContentPart<? extends Node>) getHost());
					commit(deletionPolicy);
				}

			});

			ContextMenu menu = new ContextMenu(newConnectionItem, new SeparatorMenuItem(), deleteNodeItem);
			menu.show(((InfiniteCanvasViewer) viewer).getScene().getWindow(), e.getScreenX(), e.getScreenY());

		} else if (e.isPrimaryButtonDown()) {
			IViewer viewer = getHost().getViewer();
			TopoItemCreationModel cm = viewer.getAdapter(TopoItemCreationModel.class);
			if (cm.getType() != TopoItemCreationModel.Type.Connection) {
				return;
			}
			TopoNodePart source = cm.getSource();
			TopoNodePart target = (TopoNodePart) getHost();
			if (source == target) {
				return;
			}
			for (IVisualPart<? extends Node> part : getHost().getRoot().getChildrenUnmodifiable()) {
				if (part instanceof TopoGraphPart) {
					ITopoGraph topoModel = ((ITopoGraph) ((TopoGraphPart) part).getContent().getAttributes()
							.get(TopoZestGraphBuilder.TOPO_MODEL_ATTR));
					GefTopoNode gefSource = source.getContent();
					GefTopoNode gefTarget = target.getContent();
					try
					{
						GefTopoEdge conn = new GefTopoEdge(
								topoModel.createConnection(gefSource.getTopoModelNode(), gefTarget.getTopoModelNode()),
								gefSource, gefTarget);
						IRootPart<? extends Node> root = getHost().getRoot();
						CreationPolicy creationPolicy = root.getAdapter(CreationPolicy.class);
						init(creationPolicy);
						creationPolicy.create(conn, part, HashMultimap.<IContentPart<? extends Node>, String>create());
						creationPolicy.commit();
					} catch (RuntimeException e1) {
						// TODO: display error message in a message box
						e1.printStackTrace();
					}
					break;
				}
			}
			cm.setType(TopoItemCreationModel.Type.None);
			cm.setSource(null);
		}
	}
}
