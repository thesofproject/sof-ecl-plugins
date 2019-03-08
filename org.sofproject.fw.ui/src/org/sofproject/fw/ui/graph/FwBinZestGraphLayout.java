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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.gef.geometry.planar.Point;
import org.eclipse.gef.graph.Graph;
import org.eclipse.gef.graph.Node;
import org.eclipse.gef.layout.LayoutContext;
import org.eclipse.gef.layout.LayoutProperties;
import org.eclipse.gef.layout.algorithms.GridLayoutAlgorithm;

public class FwBinZestGraphLayout extends GridLayoutAlgorithm {

	class GridPoint {
		int x;
		int y;

		GridPoint(int x, int y) {
			this.x = x;
			this.y = y;
		}

		void moveLeft() {
			x++;
		}

		void moveDown() {
			y++;
		}

		GridPoint max(GridPoint other) {
			return new GridPoint(Integer.max(x, other.x), Integer.max(y, other.y));
		}
	}

	private List<List<Node>> grid = new ArrayList<>();

	public void applyLayout(LayoutContext context, boolean clean) {

		// build the grid (once)
		if (grid.isEmpty()) {
//			List<Node> pcmNodes = findPcmNodes(context.getGraph());
//
//			// start from the first row to keep 0 for unconnected widgets atm
//			int y = 1;
//			for (Node pcm : pcmNodes) {
//				addToGrid(pcm, new GridPoint(0, y));
//				GridPoint outMaxPos = traverseOutgoing(pcm, new GridPoint(1, y));
//				// if there is an outgoing stream, move down
//				if (outMaxPos.x > 1) {
//					y = outMaxPos.y + 1;
//				}
//				GridPoint inMaxPos = traverseIncoming(pcm, new GridPoint(1, y));
//				if (inMaxPos.x > 1) {
//					y = inMaxPos.y + 1;
//				}
//			}

			Graph g = context.getGraph();
			if (g.getNodes().isEmpty())
				return;
//			Node rootNode = g.getNodes().get(0);
//			GridPoint rootGp = new GridPoint(0, 0);
//			traverseOutgoing(rootNode, rootGp);
			for (Node n : context.getGraph().getNodes()) {
				addToGrid(n, new GridPoint(FwBinZestGraphBuilder.getNodePosX(n), FwBinZestGraphBuilder.getNodePosY(n)));
			}
		}

		int colIdx = 0;
		for (List<Node> col : grid) {
			int rowIdx = 0;
			for (Node n : col) {
				if (n != null) {
					LayoutProperties.setLocation(n, new Point(colIdx * 240, rowIdx * 120));
				}
				rowIdx++;
			}
			colIdx++;
		}

//		super.applyLayout(context, clean);
	}

	private void addToGrid(Node node, GridPoint pos) {
		int col = pos.x;
		int row = pos.y;

//		System.out.println(String.format("addToGrid() %s at %d.%d", 
//				FwBinZestGraphBuilder.getNodeName(node), col, row));

		if (col == grid.size()) {
			grid.add(new ArrayList<Node>());
		}
		List<Node> column = grid.get(col);
		while (row >= column.size()) {
			column.add(null);
		}
		column.set(row, node);
	}
}
