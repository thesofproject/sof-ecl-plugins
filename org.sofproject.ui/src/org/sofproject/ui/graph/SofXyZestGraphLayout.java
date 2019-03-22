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

package org.sofproject.ui.graph;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.gef.geometry.planar.Dimension;
import org.eclipse.gef.geometry.planar.Point;
import org.eclipse.gef.graph.Node;
import org.eclipse.gef.layout.ILayoutAlgorithm;
import org.eclipse.gef.layout.LayoutContext;
import org.eclipse.gef.layout.LayoutProperties;

public class SofXyZestGraphLayout implements ILayoutAlgorithm {

	private static double TOP_MARGIN = 10d;
	private static double LEFT_MARGIN = 10d;

	private static double COLUMN_MARGIN = 30d; // need more space for nice arrows
	private static double ROW_MARGIN = 10;

	class GridNode {
		Node node;
		int col;
		int row;

		GridNode(Node node, int col, int row) {
			this.node = node;
			this.col = col;
			this.row = row;
		}
	};

	private List<GridNode> grid = new ArrayList<>();

	class GridElemSize {
		double size;
		double offset;

		GridElemSize() {
			this.size = 0d;
			this.offset = 0d;
		}

		double getSize() {
			return size;
		}

		void updateSize(double otherSize) {
			size = Double.max(size, otherSize);
		}

		double getOffset() {
			return offset;
		}

		void setOffset(double offset) {
			this.offset = offset;
		}
	}

	private List<GridElemSize> colSizes = new ArrayList<>();
	private List<GridElemSize> rowSizes = new ArrayList<>();

	/**
	 * Number of rows excluded from the column size computations.
	 */
	private int topRowsExcluded;

	public SofXyZestGraphLayout() {
		this(0);
	}

	public SofXyZestGraphLayout(int topRowsExcluded) {
		this.topRowsExcluded = topRowsExcluded;
	}

	protected boolean isGridEmpty() {
		return grid.isEmpty();
	}

	protected void gridComplete() {
		computeOffsets();
	}

	protected void addToGrid(Node node, int col, int row) {
		Dimension nodeSize = LayoutProperties.getSize(node);

		grid.add(new GridNode(node, col, row));

		while (col >= colSizes.size()) {
			colSizes.add(new GridElemSize());
		}

		if (row >= topRowsExcluded) {
			colSizes.get(col).updateSize(nodeSize.getWidth());
		}

		while (row >= rowSizes.size()) {
			rowSizes.add(new GridElemSize());
		}
		rowSizes.get(row).updateSize(nodeSize.getHeight());
	}

	private void computeOffsets() {
		// now compute accumulated offsets for each column and row
		double accSize = LEFT_MARGIN;
		for (GridElemSize colData : colSizes) {
			double colSize = colData.getSize();
			colData.setOffset(accSize);
//			System.out.println("colSize [" + i + "] = " + accSize);

			accSize += colSize + (colSize > 0 ? COLUMN_MARGIN : 0);
		}

		accSize = TOP_MARGIN;
		for (GridElemSize rowData : rowSizes) {
			double rowSize = rowData.getSize();
			rowData.setOffset(accSize);
			accSize += rowSize + ROW_MARGIN;
		}
	}

	public void applyLayout(LayoutContext context, boolean clean) {

		// Some nodes are repositioned since the default GraphLayoutBehavior running
		// in post-layout step attempts to center the nodes at the provided location
		// when calling node's setPosition() finally.

		if (grid.isEmpty())
			return;

		// setting up fixed grid locations assigned while building the graph

		for (GridNode gridNode : grid) {
			if (gridNode.node != null) {
				Dimension size = LayoutProperties.getSize(gridNode.node);
				GridElemSize colData = colSizes.get(gridNode.col);
				GridElemSize rowData = rowSizes.get(gridNode.row);
				double x = colData.getOffset() + size.getWidth() / 2;
				x += (colData.getSize() - size.getWidth()) / 2;
				double y = rowData.getOffset() + size.getHeight() / 2;
				y += (rowData.getSize() - size.getHeight()) / 2;
				LayoutProperties.setLocation(gridNode.node, new Point(x, y));
			}
		}
	}
}
