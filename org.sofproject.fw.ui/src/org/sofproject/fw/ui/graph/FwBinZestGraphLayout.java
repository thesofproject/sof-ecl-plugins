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

import org.eclipse.gef.graph.Node;
import org.eclipse.gef.layout.LayoutContext;
import org.sofproject.ui.graph.SofXyZestGraphLayout;

public class FwBinZestGraphLayout extends SofXyZestGraphLayout {

	// exclude the first row (fw memory map) from column size computations
	private static final int TOP_ROWS_EXCLUDED = 1;

	public FwBinZestGraphLayout() {
		super(TOP_ROWS_EXCLUDED);
	}

	public void applyLayout(LayoutContext context, boolean clean) {
		if (isGridEmpty()) {
			for (Node n : context.getGraph().getNodes()) {
				int col = FwBinZestGraphBuilder.getNodePosX(n);
				int row = FwBinZestGraphBuilder.getNodePosY(n);
				addToGrid(n, col, row);
			}
			gridComplete();
		}
		super.applyLayout(context, clean);
	}
}
