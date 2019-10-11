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

package org.sofproject.gst.topo.model;

import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.sofproject.topo.ui.graph.ITopoCollectionNode;
import org.sofproject.topo.ui.graph.ITopoNode;

public class GstTopoPipeline implements ITopoCollectionNode {

	private List<GstTopoNode> nodes = new LinkedList<>();

	@Override
	public String getName() {
		return "The Pipeline";
	}

	@Override
	public boolean isVisible() {
		return false;
	}

	@Override
	public int size() {
		return nodes.size();
	}

	@Override
	public Collection<? extends ITopoNode> getChildren() {
		return nodes;
	}

	public void add(GstTopoNode node) {
		nodes.add(node);
	}

	@Override
	public void remove(ITopoNode node) {
		nodes.remove(node);
	}

	public void serialize(Writer writer) throws IOException {
		GstTopoNode nextNode = null;
		for (GstTopoNode node : nodes) {
			if (node.isFirst()) {
				nextNode = node;
				break;
			}
		}
		while (nextNode != null) {
			nextNode.serialize(writer);
			GstTopoConnection conn = nextNode.getOutgoingConnection();
			if (conn == null) {
				nextNode = null;
			} else {
				writer.write(" ! ");
				nextNode = (GstTopoNode) conn.getTarget();
			}
		}
	}

}
