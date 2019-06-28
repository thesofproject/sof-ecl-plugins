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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.sofproject.alsa.topo.conf.ConfElement;
import org.sofproject.alsa.topo.conf.ConfItem;
import org.sofproject.core.binfile.BinStruct;

/**
 * Groups of attributes, common for all nodes: - general - vendor tuples
 */
public class AlsaTopoNode extends AlsaTopoItem {

	private String typeName = "";

	/**
	 * Main model element, represented here as associated graph node. If the model
	 * is being "disassembled" from the binary topology file, it provides also the
	 * reference to part of the binary file. May give null otherwise.
	 */
	private ConfElement confElement;

	private List<AlsaTopoConnection> inConn = new ArrayList<>();
	private List<AlsaTopoConnection> outConn = new ArrayList<>();

	/**
	 * @param confElement Topology file element, already initialized with data from
	 *                    binItem.
	 */
	public AlsaTopoNode(ConfElement confElement) {
		super(confElement.getName());
		this.confElement = confElement;
	}

	public BinStruct getBinStruct() {
		return confElement.getBinSource();
	}

	public ConfElement getConfElement() {
		return confElement;
	}

	public Collection<ConfItem> getConfItems() {
		return confElement.getChildren();
	}

	public void addInConn(AlsaTopoConnection conn) {
		inConn.add(conn);
	}

	public void addOutConn(AlsaTopoConnection conn) {
		outConn.add(conn);
	}

	public List<AlsaTopoConnection> getInConns() {
		return inConn;
	}

	public List<AlsaTopoConnection> getOutConns() {
		return outConn;
	}

	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	public void removeIncomingConnection(AlsaTopoConnection conn) {
		inConn.remove(conn);
	}

	public void removeOutgoingConnection(AlsaTopoConnection conn) {
		outConn.remove(conn);
	}

}
