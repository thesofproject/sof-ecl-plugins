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

package org.sofproject.gst.topo.ops;

import org.sofproject.core.connection.AudioDevNodeConnection;
import org.sofproject.core.connection.AudioDevNodeConnectionManager;
import org.sofproject.core.ops.IRemoteOp;
import org.sofproject.core.ops.IRemoteOpsProvider;
import org.sofproject.gst.topo.model.GstTopoGraph;

public class GstGraphOpsProvider implements IRemoteOpsProvider {

	public static final String TEST_TOPO = "org.sofproject.gst.topo.ops.testtopo";

	public static final String[] OPS = { TEST_TOPO };

	private GstTopoGraph graph;

	public GstGraphOpsProvider(GstTopoGraph graph) {
		this.graph = graph;
	}

	@Override
	public String[] getRemoteOpsIds() {
		return OPS;
	}

	@Override
	public String getRemoteOpDisplayName(String opId) {
		switch (opId) {
		case TEST_TOPO:
			return "Test Topology";
		default:
			return null;
		}
	}

	@Override
	public IRemoteOp createRemoteOp(String opId, AudioDevNodeConnection conn) {
		switch (opId) {
		case TEST_TOPO:
			if (conn == null) {
				conn = AudioDevNodeConnectionManager.getInstance().getConnection(graph.getFileInput().getProject());;
			}
			return new GstTopoTestOperation(graph, conn);
		default:
			return null;
		}
	}

}
