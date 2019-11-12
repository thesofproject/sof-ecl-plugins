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

package org.sofproject.fw.ui.graph;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.gef.graph.Edge;
import org.eclipse.gef.graph.Graph;
import org.eclipse.gef.graph.Node;
import org.eclipse.gef.zest.fx.ZestProperties;
import org.sofproject.core.binfile.BinStruct;
import org.sofproject.fw.model.FwBinBlock;
import org.sofproject.fw.model.FwBinConnection;
import org.sofproject.fw.model.FwBinGraph;
import org.sofproject.fw.model.FwBinItem;
import org.sofproject.fw.model.FwMemoryMap;
import org.sofproject.fw.ui.parts.FwBinNodePart;
import org.sofproject.fw.ui.resources.SofResources;
import org.sofproject.ui.editor.IBinStructHolder;

import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;

public class FwBinZestGraphBuilder {

	public static class EdgeArrow extends Polygon {
		public EdgeArrow() {
			super(0, 0, 5, 3, 5, -3);
		}
	}

	public class BinFileNode extends Node implements IBinStructHolder {

		public BinFileNode(FwBinBlock modelBlock) {

			Color textColor = SofResources.SOF_DARK_VIOLET;
			Color color = SofResources.SOF_LIGHT_BLUE;
			Color borderColor = SofResources.SOF_DARK_BLUE;
			double borderWidth = 1.5d;

			getAttributes().put(FwBinNodePart.MODEL_ITEM_ATTR, modelBlock);
			getAttributes().put(FwBinNodePart.BIN_ITEM_ATTR, modelBlock.getBinStruct());
			getAttributes().put(FwBinNodePart.TEXT_COLOR_ATTR, textColor);
			getAttributes().put(FwBinNodePart.COLOR_ATTR, color);
			getAttributes().put(FwBinNodePart.BORDER_COLOR_ATTR, borderColor);
			getAttributes().put(FwBinNodePart.BORDER_WIDTH_ATTR, borderWidth);
			getAttributes().put(FwBinNodePart.X_POS_ATTR, modelBlock.getColumn());
			getAttributes().put(FwBinNodePart.Y_POS_ATTR, modelBlock.getRow());
		}

		@Override
		public BinStruct getBinStruct() {
			Object item = getAttributes().get(FwBinNodePart.BIN_ITEM_ATTR);
			return item != null ? (BinStruct) item : null;
		}

	}

	public class FwMemoryMapNode extends Node {
		public FwMemoryMapNode(FwMemoryMap memMap) {
			getAttributes().put(FwBinNodePart.MODEL_ITEM_ATTR, memMap);
			getAttributes().put(FwBinNodePart.COLOR_ATTR, SofResources.SOF_LIGHT_GREY);
			getAttributes().put(FwBinNodePart.BORDER_COLOR_ATTR, SofResources.SOF_BLUE);
			getAttributes().put(FwBinNodePart.BORDER_WIDTH_ATTR, 1.0);
			getAttributes().put(FwBinNodePart.X_POS_ATTR, memMap.getColumn());
			getAttributes().put(FwBinNodePart.Y_POS_ATTR, memMap.getRow());
		}
	}

	private Map<FwBinItem, Node> nodes = new HashMap<>();

	public Graph build(FwBinGraph topoModel) {
		Graph g = new Graph.Builder().attr(ZestProperties.LAYOUT_ALGORITHM__G, new FwBinZestGraphLayout()).build();

		for (FwBinItem item : topoModel.getChildElements()) {
			if (item instanceof FwMemoryMap) {
				g.getNodes().add(new FwMemoryMapNode((FwMemoryMap) item));
			} else if (item instanceof FwBinBlock) {
				g.getNodes().add(buildNode((FwBinBlock) item));
			} else if (item instanceof FwBinConnection) {
				g.getEdges().add(buildEdge((FwBinConnection) item));
			}
		}
		return g;
	}

	public static FwMemoryMap getModelMemMap(Node n) {
		return (FwMemoryMap) n.getAttributes().get(FwBinNodePart.MODEL_ITEM_ATTR);
	}

	public static FwBinBlock getModelNode(Node n) {
		return (FwBinBlock) n.getAttributes().get(FwBinNodePart.MODEL_ITEM_ATTR);
	}

	public static FwBinConnection getModelConnection(Edge e) {
		return (FwBinConnection) e.getAttributes().get(FwBinNodePart.MODEL_ITEM_ATTR);
	}

	public static String getNodeName(Node n) {
		return getModelNode(n).getName();
	}

	public static String getNodeTypeName(Node n) {
		return getModelNode(n).getTypeName();
	}

	public static Color getNodeColor(Node n) {
		return (Color) n.getAttributes().get(FwBinNodePart.COLOR_ATTR);
	}

	public static Color getNodeTextColor(Node n) {
		return (Color) n.getAttributes().get(FwBinNodePart.TEXT_COLOR_ATTR);
	}

	public static Color getNodeBorderColor(Node n) {
		return (Color) n.getAttributes().get(FwBinNodePart.BORDER_COLOR_ATTR);
	}

	public static double getNodeBorderWidth(Node n) {
		return (double) n.getAttributes().get(FwBinNodePart.BORDER_WIDTH_ATTR);
	}

	public static int getNodePosX(Node n) {
		return (int) n.getAttributes().get(FwBinNodePart.X_POS_ATTR);
	}

	public static int getNodePosY(Node n) {
		return (int) n.getAttributes().get(FwBinNodePart.Y_POS_ATTR);
	}

	private Node buildNode(FwBinBlock modelBlock) {
		BinFileNode n = new BinFileNode(modelBlock);
		nodes.put(modelBlock, n);
		return n;
	}

	private Edge buildEdge(FwBinConnection modelEdge) {
		Node srcNode = nodes.get(modelEdge.getSrc());
		Node destNode = nodes.get(modelEdge.getTgt());
		Edge.Builder builder = new Edge.Builder(srcNode, destNode).attr(FwBinNodePart.MODEL_ITEM_ATTR, modelEdge);
		builder.attr(ZestProperties.TARGET_DECORATION__E, new EdgeArrow());
		return builder.buildEdge();
	}
}
