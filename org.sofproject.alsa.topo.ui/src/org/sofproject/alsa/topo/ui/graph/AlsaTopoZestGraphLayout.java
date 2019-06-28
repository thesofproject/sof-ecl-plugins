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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.gef.graph.Edge;
import org.eclipse.gef.graph.Graph;
import org.eclipse.gef.graph.Node;
import org.eclipse.gef.layout.LayoutContext;
import org.sofproject.alsa.topo.model.AlsaTopoConnection;
import org.sofproject.alsa.topo.model.AlsaTopoConnection.Type;
import org.sofproject.alsa.topo.model.AlsaTopoNode;
import org.sofproject.alsa.topo.model.AlsaTopoNodeBe;
import org.sofproject.alsa.topo.model.AlsaTopoNodePcm;
import org.sofproject.ui.graph.SofXyZestGraphLayout;

public class AlsaTopoZestGraphLayout extends SofXyZestGraphLayout {

	public static final int UNCONNECTED_PER_ROW = 8;

	class GridPosition {
		int col;
		int row;

		GridPosition(int col, int row) {
			this.col = col;
			this.row = row;
		}

		GridPosition max(GridPosition other) {
			return new GridPosition(Integer.max(col, other.col), Integer.max(row, other.row));
		}
	}

	@Override
	public void applyLayout(LayoutContext context, boolean clean) {
		if (isGridEmpty()) {

			// create all paths that begins/ends with pcm node, from column 0
			List<Node> pcmNodes = findPcmNodes(context.getGraph());

			// layout unconnected nodes ...
			int y = layoutUnconnected(context) + 1;

			// ... and continue from the next row
			for (Node pcm : pcmNodes) {
				addToGrid(pcm, 0, y);
				GridPosition outMaxPos = traverseOutgoing(pcm, new GridPosition(1, y));
				// if there is an outgoing stream, move down
				if (outMaxPos.col > 1) {
					y = outMaxPos.row + 1;
				}
				GridPosition inMaxPos = traverseIncoming(pcm, new GridPosition(1, y));
				if (inMaxPos.col > 1) {
					y = inMaxPos.row + 1;
				}
			}

			// create all paths that begins with siggen node, from column 1
			List<Node> sigGenNodes = findSigGenNodes(context.getGraph());
			for (Node sigNode : sigGenNodes) {
				addToGrid(sigNode, 1, y);
				GridPosition outMaxPos = traverseOutgoing(sigNode, new GridPosition(2, y));
				if (outMaxPos.col > 1) {
					y = outMaxPos.row + 1;
				}
			}

			gridComplete();
		}
		super.applyLayout(context, clean);
	}

	// move all unconnected widgets to row 0
	/**
	 * Moves all unconnected widgets to top.
	 *
	 * @param context
	 * @return Number of occupied rows
	 */
	private int layoutUnconnected(LayoutContext context) {
		int row = 0;
		int col = 1;
		for (Node n : context.getGraph().getNodes()) {
			if (n.getIncomingEdges().isEmpty() && n.getOutgoingEdges().isEmpty()) {
				addToGrid(n, col++, row);
				if (col == UNCONNECTED_PER_ROW + 1) {
					col = 1;
					row++;
				}
			}
		}
		return row;
	}

	private List<Node> findPcmNodes(Graph g) {
		List<Node> pcmNodes = new ArrayList<>();
		for (Node n : g.getNodes()) {
			AlsaTopoNode modelItem = AlsaTopoZestGraphBuilder.getModelNode(n);
			if (modelItem instanceof AlsaTopoNodePcm) {
				pcmNodes.add(n);
			}
		}
		return pcmNodes;
	}

	private List<Node> findSigGenNodes(Graph g) {
		List<Node> sigGenNodes = new ArrayList<>();

		// TODO:

//		for (Node n : g.getNodes()) {
//			AlsaTopoNode modelItem = AlsaTopoZestGraphBuilder.getModelNode(n);
//			if (modelItem.getTypeName().equals("siggen")) {
//				sigGenNodes.add(n);
//			}
//		}
		return sigGenNodes;
	}

	private GridPosition traverseOutgoing(Node node, GridPosition pos) {
		GridPosition ctrlPos = new GridPosition(pos.col - 1, pos.row + 1);
		for (Edge e : node.getIncomingEdges()) {
			AlsaTopoConnection topoConn = AlsaTopoZestGraphBuilder.getModelConnection(e);
			if (topoConn.getType() == Type.CONTROL_PATH) {
				Node ctrlNode = e.getSource();
				addToGrid(ctrlNode, ctrlPos.col, ctrlPos.row);
				ctrlPos.col++;
			}
		}
		for (Edge e : node.getOutgoingEdges()) {
			Node audioNode = e.getTarget();
			AlsaTopoNode modelNode = AlsaTopoZestGraphBuilder.getModelNode(audioNode);
			// TODO: should request to remove empty columns before layouting
			addToGrid(audioNode, modelNode instanceof AlsaTopoNodeBe ? 20 : pos.col, pos.row);
			if (!(modelNode instanceof AlsaTopoNodeBe)) {
				pos.col++;
				pos = traverseOutgoing(audioNode, pos);
			}
		}
		// let's update position with audio positions and ctrl position
		return pos.max(ctrlPos);
	}

	private GridPosition traverseIncoming(Node node, GridPosition pos) {
		GridPosition ctrlPos = new GridPosition(pos.col - 1, pos.row + 1);
		for (Edge e : node.getAllIncomingEdges()) {
			AlsaTopoConnection topoConn = AlsaTopoZestGraphBuilder.getModelConnection(e);
			if (topoConn.getType() == Type.CONTROL_PATH) {
				Node ctrlNode = e.getSource();
				addToGrid(ctrlNode, ctrlPos.col, ctrlPos.row);
				ctrlPos.col++;
			} else {
				Node audioNode = e.getSource();
				AlsaTopoNode modelNode = AlsaTopoZestGraphBuilder.getModelNode(audioNode);
				addToGrid(audioNode, modelNode instanceof AlsaTopoNodeBe ? 20 : pos.col, pos.row);
				if (!(modelNode instanceof AlsaTopoNodeBe)) {
					pos.col++;
					pos = traverseIncoming(audioNode, pos);
				}
			}
		}
		// let's update position
		return pos.max(ctrlPos);
	}

}
