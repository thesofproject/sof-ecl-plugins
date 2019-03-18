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

import org.eclipse.gef.geometry.planar.Dimension;
import org.eclipse.gef.geometry.planar.Point;
import org.eclipse.gef.graph.Graph;
import org.eclipse.gef.graph.Node;
import org.eclipse.gef.layout.ILayoutAlgorithm;
import org.eclipse.gef.layout.LayoutContext;
import org.eclipse.gef.layout.LayoutProperties;
import org.sofproject.fw.model.FwBinGraph;

public class FwBinZestGraphLayout implements ILayoutAlgorithm {

	private static double TOP_MARGIN = 10d;
	private static double LEFT_MARGIN = 10d;

	private static double COLUMN_MARGIN = 30d; // need more space for nice arrows
	private static double ROW_MARGIN = 10;

	List<Double> colSizes = new ArrayList<>();
	List<Double> rowSizes = new ArrayList<>();

	private void updateColumnSize(int col, double size) {
		while (colSizes.size() <= col) {
			colSizes.add(0d);
		}
		colSizes.set(col, Double.max(colSizes.get(col), size));
	}

	private void updateRowSize(int row, double size) {
		while (rowSizes.size() <= row) {
			rowSizes.add(0d);
		}
		rowSizes.set(row, Double.max(rowSizes.get(row), size));
	}

	private void computeSizes(Graph graph) {
		colSizes.clear();
		rowSizes.clear();
		for (Node n : graph.getNodes()) {
			int col = FwBinZestGraphBuilder.getNodePosX(n);
			int row = FwBinZestGraphBuilder.getNodePosY(n);
			Dimension size = LayoutProperties.getSize(n);

//			System.out.println("computeSizes ["+col+", "+row+"] "
//					+ size.getWidth() + " x " + size.getHeight());

			// item in the first row may span across multiple columns, so do not
			// update the column size common for other rows
			if (row != FwBinGraph.FW_MEM_MAP_ROW) {
				updateColumnSize(col, size.getWidth());
			}
			updateRowSize(row, size.getHeight());
		}
		// now compute accumulated offsets for each column and row
		double accSize = LEFT_MARGIN;
		for (int i = 0; i < colSizes.size(); i++) {
			double colSize = colSizes.set(i, accSize);

//			System.out.println("colSize [" + i + "] = " + accSize);

			accSize += colSize + COLUMN_MARGIN;
		}

		accSize = TOP_MARGIN;
		for (int i = 0; i < rowSizes.size(); i++) {
			double rowSize = rowSizes.set(i, accSize);
			accSize += rowSize + ROW_MARGIN;
		}
	}

	public void applyLayout(LayoutContext context, boolean clean) {

		// Some nodes are repositioned since the default GraphLayoutBehavior running
		// in post-layout step attempts to center the nodes at the provided location
		// when calling node's setPosition() finally.

		Graph g = context.getGraph();
		if (g.getNodes().isEmpty())
			return;

		computeSizes(g);

		// setting up fixed grid locations assigned while building the graph

		// TODO: now the nodes are top-left aligned, should be centered inside cells

		for (Node n : context.getGraph().getNodes()) {
			int col = FwBinZestGraphBuilder.getNodePosX(n);
			int row = FwBinZestGraphBuilder.getNodePosY(n);
			Dimension size = LayoutProperties.getSize(n);
			double x = colSizes.get(col) + size.getWidth() / 2;
			double y = rowSizes.get(row) + size.getHeight() / 2;
			LayoutProperties.setLocation(n, new Point(x, y));
		}
	}
}
