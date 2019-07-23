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

package org.sofproject.topo.ui.graph;

import java.util.Collection;

import org.sofproject.core.binfile.BinStruct;

import javafx.scene.paint.Color;

/**
 * A topology node interface implemented by nodes created by a specific topology
 * binding.
 *
 * Each item is held by a GefTopoNode (inherited from Node) instance inserted
 * directly into the graph.
 */
public interface ITopoNode extends ITopoElement {

	/**
	 * @param newName New name
	 */
	public void setName(String newName);

	/**
	 * @return Short description of the node (displayed below the name).
	 */
	public String getDescription();

	/**
	 * @return Tooltip for the node.
	 */
	public String getTooltip();

	/**
	 * @return true if node should be positioned in the first column
	 */
	public boolean isFirst();

	/**
	 * @return true if node should be positioned in the last column
	 */
	public boolean isLast();

	/**
	 * @return Parent graph.
	 */
	public ITopoGraph getParentGraph();

	/**
	 * @return Parent collection node or null.
	 */
	public ITopoCollectionNode getParent();

	/**
	 * @return Child elements that are not visualized as separate nodes but have
	 *         editable attributes.
	 */
	public Collection<? extends ITopoElement> getChildElements();

	/**
	 * @return Color used to fill the node.
	 */
	public Color getColor();

	/**
	 * @return Color of the border.
	 */
	public Color getBorderColor();

	/**
	 * @return Width of the border.
	 */
	public double getBorderWidth();

	/**
	 * @return Binary structure reference if there is a binary representation of the
	 *         model already built, null otherwise.
	 */
	public BinStruct getBinStruct();

	/**
	 * @return Index of preferred column for layout algorithm, -1 if none.
	 */
	public int getPreferredColumn();

}
