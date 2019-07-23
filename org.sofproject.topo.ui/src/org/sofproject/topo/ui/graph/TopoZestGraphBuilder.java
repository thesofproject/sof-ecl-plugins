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

package org.sofproject.topo.ui.graph;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.gef.graph.Graph;
import org.eclipse.gef.zest.fx.ZestProperties;

public class TopoZestGraphBuilder {

	public static String TOPO_MODEL_ATTR = "org-sofproject-topo-model";

	private Map<ITopoNode, GefTopoNode> nodes = new HashMap<>();

	public Graph build(ITopoGraph topoModel) {
		Graph g = new Graph.Builder().attr(ZestProperties.LAYOUT_ALGORITHM__G, new TopoZestGraphLayout()).build();

		// attach the model to the graph, may be used through the interface where graph is available
		g.getAttributes().put(TopoZestGraphBuilder.TOPO_MODEL_ATTR, topoModel);

		// create graph objects in two iterations
		// the second one creates the edges to make sure all nodes are already there

		// 1. create collection nodes (ITopoCollectionNode)
		for (ITopoCollectionNode collection : topoModel.getCollections()) {
			if (!collection.isVisible())
				continue;
			g.getNodes().add(buildCollectionNode(collection));
		}
		// 2. create nodes (ITopoNode)
		for (ITopoNode node : topoModel.getNodes()) {
			g.getNodes().add(buildNode(node));
		}
		// 3. create connections (ITopoConnection)
		for (ITopoConnection connection : topoModel.getConnections()) {
			g.getEdges().add(buildEdge(connection));
		}
		// the map was used to build edges quickly, not needed anymore
		nodes.clear();

		return g;
	}

	private GefTopoNode buildNode(ITopoNode node) {
		GefTopoNode gefNode = new GefTopoNode(node);
		nodes.put(node, gefNode);
		return gefNode;
	}

	private GefTopoCollectionNode buildCollectionNode(ITopoCollectionNode colNode) {
		GefTopoCollectionNode gefColNode = new GefTopoCollectionNode(colNode);
		Graph sub = new Graph.Builder()
				.attr(ZestProperties.LAYOUT_ALGORITHM__G, new TopoZestGraphLayout(6 /* items per row */)).build();

		for (ITopoNode childNode : colNode.getChildren()) {
			sub.getNodes().add(buildNode(childNode));
		}
		gefColNode.setNestedGraph(sub);
		return gefColNode;
	}

	private GefTopoEdge buildEdge(ITopoConnection edge) {
		return new GefTopoEdge(edge, nodes.get(edge.getSource()), nodes.get(edge.getTarget()));
	}
}
