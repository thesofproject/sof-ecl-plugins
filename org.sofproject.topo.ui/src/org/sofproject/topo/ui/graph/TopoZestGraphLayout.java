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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.gef.graph.Edge;
import org.eclipse.gef.graph.Graph;
import org.eclipse.gef.graph.Node;
import org.eclipse.gef.layout.LayoutContext;
import org.sofproject.ui.graph.SofXyZestGraphLayout;

public class TopoZestGraphLayout extends SofXyZestGraphLayout {

	public static final int DEFAULT_UNCONNECTED_PER_ROW = 8;

	private int unconnectedPerRow;

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

	public TopoZestGraphLayout() {
		this(DEFAULT_UNCONNECTED_PER_ROW);
	}

	public TopoZestGraphLayout(int unconnectedPerRow) {
		this.unconnectedPerRow = unconnectedPerRow;
	}

	@Override
	public void applyLayout(LayoutContext context, boolean clean) {
		if (isGridEmpty()) {

			// create all paths that begins/ends with pcm node, from column 0
			List<GefTopoNode> firstNodes = findFirstNodes(context.getGraph());

			// layout unconnected nodes ...
			int y = layoutUnconnected(context) + 1;

			// ... and continue from the next row
			for (GefTopoNode first : firstNodes) {
				int prefCol = first.getTopoModelNode().getPreferredColumn();
				if (prefCol == -1) {
					prefCol = 0;
				}
				addToGrid(first, prefCol, y);
				GridPosition outMaxPos = traverseOutgoing(first, new GridPosition(prefCol + 1, y));
				// if there is an outgoing stream, move down
				if (outMaxPos.col > prefCol + 1) {
					y = outMaxPos.row + 1;
				}
				GridPosition inMaxPos = traverseIncoming(first, new GridPosition(prefCol + 1, y));
				if (inMaxPos.col > prefCol + 1) {
					y = inMaxPos.row + 1;
				}
			}

			gridComplete();
		}
		super.applyLayout(context, clean);
	}

	/**
	 * Moves all unconnected widgets to top.
	 *
	 * @param context
	 * @return Number of occupied rows
	 */
	private int layoutUnconnected(LayoutContext context) {
		int row = 0;
		int col = 0;
		for (Node n : context.getGraph().getNodes()) {
			if (n.getIncomingEdges().isEmpty() && n.getOutgoingEdges().isEmpty()) {
				addToGrid(n, col++, row);
				if (col == unconnectedPerRow + 1) {
					col = 0;
					row++;
				}
			}
		}
		return row;
	}

	private List<GefTopoNode> findFirstNodes(Graph g) {
		List<GefTopoNode> firstNodes = new ArrayList<>();
		for (Node n : g.getNodes()) {
			if (n instanceof GefTopoNode) {
				GefTopoNode gefNode = (GefTopoNode) n;
				if (gefNode.getTopoModelNode().isFirst()) {
					firstNodes.add(gefNode);
				}
			}
		}
		return firstNodes;
	}

	private GridPosition traverseOutgoing(GefTopoNode node, GridPosition pos) {
		GridPosition ctrlPos = new GridPosition(pos.col - 1, pos.row + 1);
		for (Edge e : node.getIncomingEdges()) {
			GefTopoEdge topoEdge = (GefTopoEdge) e;
			// check if this is undirected connection to a control node
			if (!topoEdge.getTopoModelConnection().hasArrow()) {
				Node ctrlNode = e.getSource();
				addToGrid(ctrlNode, ctrlPos.col, ctrlPos.row);
				ctrlPos.col++;
			}
		}
		for (Edge e : node.getOutgoingEdges()) {
			GefTopoNode audioNode = (GefTopoNode) e.getTarget();

			if (!((GefTopoEdge) e).getTopoModelConnection().followMe())
				continue;

			// TODO: should request to remove empty columns before layouting
			addToGrid(audioNode, audioNode.getTopoModelNode().isLast() ? 20 : pos.col, pos.row);
			if (!audioNode.getTopoModelNode().isLast()) {
				pos.col++;
				pos = traverseOutgoing(audioNode, pos);
			}
		}
		// let's update position with audio positions and ctrl position
		return pos.max(ctrlPos);
	}

	private GridPosition traverseIncoming(GefTopoNode node, GridPosition pos) {
		GridPosition ctrlPos = new GridPosition(pos.col - 1, pos.row + 1);
		for (Edge e : node.getAllIncomingEdges()) {
			GefTopoEdge topoEdge = (GefTopoEdge) e;

			if (!topoEdge.getTopoModelConnection().followMe())
				continue;

			if (!topoEdge.getTopoModelConnection().hasArrow()) {
				Node ctrlNode = e.getSource();
				addToGrid(ctrlNode, ctrlPos.col, ctrlPos.row);
				ctrlPos.col++;
			} else {
				GefTopoNode audioNode = (GefTopoNode) e.getSource();
				if (audioNode == null) // TODO: BUG? echo ref out_drv connection
					continue;
				addToGrid(audioNode, audioNode.getTopoModelNode().isLast() ? 20 : pos.col, pos.row);
				if (!audioNode.getTopoModelNode().isLast()) {
					pos.col++;
					pos = traverseIncoming(audioNode, pos);
				}
			}
		}
		// let's update position
		return pos.max(ctrlPos);
	}

}
