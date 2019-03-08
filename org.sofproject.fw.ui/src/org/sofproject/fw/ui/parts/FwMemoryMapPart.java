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

package org.sofproject.fw.ui.parts;

import org.eclipse.gef.zest.fx.parts.NodePart;
import org.sofproject.fw.ui.visuals.FwMemoryMapVisual;

import javafx.scene.Group;
import javafx.scene.text.Text;

public class FwMemoryMapPart extends NodePart {

	public static final String COLOR_ATTR = "fw-bin-node-color";
	public static final String BORDER_COLOR_ATTR = "fw-bin-node-border-color";
	public static final String BORDER_WIDTH_ATTR = "fw-bin-node-border-width";
	
	/**
	 * X (column) position in the grid. 
	 */
	public static final String X_POS_ATTR = "fw-bin-node-x-pos";
	
	/**
	 * Y (row) position in the grid.
	 */
	public static final String Y_POS_ATTR = "fw-bin-node-y-pos";

	/**
	 * Attribute holds a reference to FwBinBlock instance.
	 */
	public static final String MODEL_ITEM_ATTR = "fw-bin-node-model-item";
	
	/**
	 * Attribute holds a reference to the source BinStruct instance,
	 * the FwBinBlock was built from.
	 */
	public static final String BIN_ITEM_ATTR = "fw-bin-node-bin-item";
	
	Text label;

	@Override
	protected Group doCreateVisual() {
		label = new Text("?"); // TODO:
		return new Group(new FwMemoryMapVisual(getContent()));
	}

	@Override
	protected Text getLabelText() {
		return label;
	}

}
