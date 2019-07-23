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

import org.sofproject.topo.ui.graph.ITopoConnection;
import org.sofproject.topo.ui.graph.ITopoGraph;
import org.sofproject.topo.ui.graph.ITopoNode;

import javafx.scene.paint.Color;

public class AlsaTopoConnection implements ITopoConnection {

	public enum Type {
		DAPM_PATH, STREAM_PATH, CONTROL_PATH,
	}

	public static String DAPM_TOOLTIP = "DAPM";
	public static String STREAM_TOOLTIP = "Stream";
	public static String CONTROL_TOOLTIP = "Control";
	public static String UNKNOWN_TOOLTIP = "?";

	private Type type;
	private String name;

	private AlsaTopoGraph parentGraph;
	private AlsaTopoPipeline parentPipeline;

	private AlsaTopoNode src;
	private AlsaTopoNode tgt;

	private boolean followMe = true;

	public AlsaTopoConnection(Type type, AlsaTopoNode src, AlsaTopoNode tgt) {
		this.type = type;
		this.src = src;
		this.tgt = tgt;
		src.addOutConn(this);
		tgt.addInConn(this);
		name = new StringBuilder(src.getName()).append("..").append(tgt.getName()).toString();
	}

	public void setParentGraph(AlsaTopoGraph parentGraph) {
		this.parentGraph = parentGraph;
	}

	@Override
	public ITopoGraph getParentGraph() {
		return parentGraph;
	}

	public AlsaTopoPipeline getParentPipeline() {
		return parentPipeline;
	}

	public void setParentPipeline(AlsaTopoPipeline parentPipeline) {
		this.parentPipeline = parentPipeline;
	}

	public Type getType() {
		return type;
	}

	public String getName() {
		return name;
	}

	@Override
	public ITopoNode getSource() {
		return src;
	}

	@Override
	public ITopoNode getTarget() {
		return tgt;
	}

	@Override
	public boolean hasArrow() {
		return type == Type.DAPM_PATH || type == Type.STREAM_PATH;
	}

	@Override
	public Color getColor() {
		// TODO: different by type?
		return Color.BLACK;
	}

	@Override
	public String getTooltip() {
		switch (type) {
		case DAPM_PATH:
			return DAPM_TOOLTIP;
		case STREAM_PATH:
			return STREAM_TOOLTIP;
		case CONTROL_PATH:
			return CONTROL_TOOLTIP;
		default:
			return UNKNOWN_TOOLTIP;
		}
	}

	@Override
	public boolean followMe() {
		return followMe;
	}

	public void setFollowMe(boolean followMe) {
		this.followMe = followMe;
	}

	@Override
	public String toString() {
		return String.format("%s (%s)", name, type);
	}
}
