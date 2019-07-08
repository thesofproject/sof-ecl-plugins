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

package org.sofproject.alsa.topo.model;

import org.sofproject.alsa.topo.conf.ConfWidget;
import org.sofproject.ui.resources.SofResources;

import javafx.scene.paint.Color;

public class AlsaTopoNodeWidget extends AlsaTopoNode {

	public AlsaTopoNodeWidget(ConfWidget confWidget) {
		super(confWidget);
		setTypeName(confWidget.getType());
	}

	public String getSname() {
		return (String) getConfElement().getAttributeValue("stream_name");
	}

	@Override
	public Color getColor() {
		switch (getTypeName()) {
		case "aif_in":
			return SofResources.SOF_BLUE;
		case "aif_out":
			return SofResources.SOF_LIGHT_BLUE;
		case "buffer":
			return SofResources.SOF_GREEN;
		case "scheduler":
			return SofResources.SOF_LIGHT_GREY;
		}
		return super.getColor();
	}

	@Override
	public Color getBorderColor() {
		switch (getTypeName()) {
		case "scheduler":
			return SofResources.SOF_GREY;
		}
		return super.getBorderColor();
	}

}
