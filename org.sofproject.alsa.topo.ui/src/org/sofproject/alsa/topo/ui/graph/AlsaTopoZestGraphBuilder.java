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

package org.sofproject.alsa.topo.ui.graph;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.gef.graph.Edge;
import org.eclipse.gef.graph.Graph;
import org.eclipse.gef.graph.Node;
import org.eclipse.gef.zest.fx.ZestProperties;
import org.sofproject.alsa.topo.model.AlsaTopoConnection;
import org.sofproject.alsa.topo.model.AlsaTopoGraph;
import org.sofproject.alsa.topo.model.AlsaTopoItem;
import org.sofproject.alsa.topo.model.AlsaTopoNode;
import org.sofproject.alsa.topo.model.AlsaTopoNodeBe;
import org.sofproject.alsa.topo.model.AlsaTopoNodePcm;
import org.sofproject.alsa.topo.model.AlsaTopoNodeWidget;
import org.sofproject.alsa.topo.ui.parts.AlsaTopoNodePart;
import org.sofproject.core.binfile.BinStruct;
import org.sofproject.ui.editor.IBinStructHolder;
import org.sofproject.ui.resources.SofResources;

import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;

public class AlsaTopoZestGraphBuilder {

	public static class EdgeArrow extends Polygon {
		public EdgeArrow() {
			super(0, 0, 5, 3, 5, -3);
		}
	}

	public class BinFileNode extends Node implements IBinStructHolder {

		public BinFileNode(AlsaTopoNode modelNode) {

			Color color = SofResources.SOF_GREY;
			Color borderColor = SofResources.SOF_GREY;
			double borderWidth = 1.0;

			if (modelNode instanceof AlsaTopoNodePcm) {
				color = SofResources.SOF_YELLOW;
				borderWidth = 2.0;
				borderColor = Color.BLACK;
			} else if (modelNode instanceof AlsaTopoNodeBe) {
				color = SofResources.SOF_RED;
				borderWidth = 2.0;
				borderColor = Color.BLACK;
			} else if (modelNode instanceof AlsaTopoNodeWidget) {
				switch (modelNode.getTypeName()) {
				case "aif_in":
					color = SofResources.SOF_BLUE;
					break;
				case "aif_out":
					color = SofResources.SOF_LIGHT_BLUE;
					break;
				case "buffer":
					color = SofResources.SOF_GREEN;
					break;
				case "scheduler":
					color = SofResources.SOF_LIGHT_GREY;
					borderColor = SofResources.SOF_GREY;
					break;
				}
			}

			getAttributes().put(AlsaTopoNodePart.MODEL_ITEM_ATTR, modelNode);
			getAttributes().put(AlsaTopoNodePart.BIN_ITEM_ATTR, modelNode.getBinStruct());
			getAttributes().put(AlsaTopoNodePart.COLOR_ATTR, color);
			getAttributes().put(AlsaTopoNodePart.BORDER_COLOR_ATTR, borderColor);
			getAttributes().put(AlsaTopoNodePart.BORDER_WIDTH_ATTR, borderWidth);
		}

		@Override
		public BinStruct getBinStruct() {
			Object item = getAttributes().get(AlsaTopoNodePart.BIN_ITEM_ATTR);
			return item != null ? (BinStruct) item : null;
		}

	}

	private Map<AlsaTopoItem, Node> nodes = new HashMap<>();

	public Graph build(AlsaTopoGraph topoModel) {
		Graph g = new Graph.Builder().attr(ZestProperties.LAYOUT_ALGORITHM__G, new AlsaTopoZestGraphLayout()).build();

		// create graph objects in two iterations
		// the second one creates the edges to make sure all nodes are already there

		for (AlsaTopoItem item : topoModel.getChildElements()) {
			if (item instanceof AlsaTopoNode) {
				g.getNodes().add(buildNode((AlsaTopoNode) item));
			}
		}

		for (AlsaTopoItem item : topoModel.getChildElements()) {
			if (item instanceof AlsaTopoConnection) {
				g.getEdges().add(buildEdge((AlsaTopoConnection) item));
			}
		}

		return g;
	}

	public static AlsaTopoNode getModelNode(Node n) {
		return (AlsaTopoNode) n.getAttributes().get(AlsaTopoNodePart.MODEL_ITEM_ATTR);
	}

	public static AlsaTopoConnection getModelConnection(Edge e) {
		return (AlsaTopoConnection) e.getAttributes().get(AlsaTopoNodePart.MODEL_ITEM_ATTR);
	}

	public static String getNodeName(Node n) {
		return getModelNode(n).getName();
	}

	public static String getNodeTypeName(Node n) {
		return getModelNode(n).getTypeName();
	}

	public static Color getNodeColor(Node n) {
		return (Color) n.getAttributes().get(AlsaTopoNodePart.COLOR_ATTR);
	}

	public static Color getNodeBorderColor(Node n) {
		return (Color) n.getAttributes().get(AlsaTopoNodePart.BORDER_COLOR_ATTR);
	}

	public static double getNodeBorderWidth(Node n) {
		return (double) n.getAttributes().get(AlsaTopoNodePart.BORDER_WIDTH_ATTR);
	}

	private Node buildNode(AlsaTopoNode modelNode) {
		BinFileNode n = new BinFileNode(modelNode);

		if (modelNode instanceof AlsaTopoNodeBe) {
			// TODO: improve nested graphs for some special nodes
//			AlsaTopoNodeBe beNode = (AlsaTopoNodeBe) modelNode;
//			Node nn = new Node.Builder().attr(AlsaTopoNodePart.NAME_ATTR, modelNode.getName())
//					.buildNode();
//			Node ns = new Node.Builder().attr(AlsaTopoNodePart.NAME_ATTR, "hw config").buildNode();
//			Edge e = new Edge.Builder(nn, ns).buildEdge();
//			Graph sub = new Graph.Builder()
//					.attr(ZestProperties.LAYOUT_ALGORITHM__G, new SpringLayoutAlgorithm())
//					.nodes(nn, ns).edges(e).build();
//			n.setNestedGraph(sub);
		}

		nodes.put(modelNode, n);
		return n;
	}

	private Edge buildEdge(AlsaTopoConnection modelEdge) {
		Node srcNode = nodes.get(modelEdge.getSrc());
		Node destNode = nodes.get(modelEdge.getTgt());
		Edge.Builder builder = new Edge.Builder(srcNode, destNode).attr(AlsaTopoNodePart.MODEL_ITEM_ATTR, modelEdge);
		// control path does not have arrows.
		if (modelEdge.getType() != AlsaTopoConnection.Type.CONTROL_PATH) {
			builder.attr(ZestProperties.TARGET_DECORATION__E, new EdgeArrow());
		}
		return builder.buildEdge();
	}
}
