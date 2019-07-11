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

package org.sofproject.alsa.topo.model;

import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.sofproject.alsa.topo.conf.ConfGraph;
import org.sofproject.topo.ui.graph.ITopoCollectionNode;
import org.sofproject.topo.ui.graph.ITopoNode;

/**
 * Pipeline is a list of connected widgets. Connected to ConfGraph to generate
 * graph item (set of lines).
 */
public class AlsaTopoPipeline extends AlsaTopoNode implements ITopoCollectionNode {

	private Map<String, AlsaTopoNode> widgets = new LinkedHashMap<>();
	private Map<String, AlsaTopoConnection> connections = new HashMap<>();

	public AlsaTopoPipeline(ConfGraph confGraph) {
		super(confGraph);
	}

	public void add(AlsaTopoNode widget) {
		widgets.put(widget.getName(), widget);
	}

	public void add(AlsaTopoConnection connection) {
		connections.put(connection.getName(), connection);
	}

	@Override
	public boolean isVisible() {
		return false;
	}

	@Override
	public int size() {
		return widgets.size();
	}

	@Override
	public Collection<? extends ITopoNode> getChildren() {
		return widgets.values();
	}

	@Override
	public void serialize(Writer writer, String indent) throws IOException {
		// widgets
		for (AlsaTopoNode widget : widgets.values()) {
			widget.serialize(writer, indent);
		}
		// connections
		getConfElement().serialize(writer, indent);
	}

}
