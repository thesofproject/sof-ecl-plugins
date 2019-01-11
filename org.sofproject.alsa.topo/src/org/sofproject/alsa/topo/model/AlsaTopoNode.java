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

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.sofproject.alsa.topo.binfile.BinStructTuple;
import org.sofproject.alsa.topo.binfile.BinStructTupleArray;
import org.sofproject.core.binfile.BinContainer;
import org.sofproject.core.binfile.BinItem;
import org.sofproject.core.binfile.BinStruct;

/**
 * Groups of attributes, common for all nodes: - general - vendor tuples
 */
public class AlsaTopoNode extends AlsaTopoItem {

	private String typeName;
	private BinStruct binItem;

	public static final String AG_GENERAL = "General";
	public static final String AG_TUPLES = "Vendor Tuples";
	public static final String AG_STREAM = "Streams";

	protected Map<String, Map<String, Object>> attributes = new LinkedHashMap<>();

	private List<AlsaTopoConnection> inConn = new ArrayList<>();
	private List<AlsaTopoConnection> outConn = new ArrayList<>();

	public AlsaTopoNode(String name, BinStruct binItem) {
		super(name);
		this.binItem = binItem;

		addAttributeGroup(AG_GENERAL);
		addAttributeGroup(AG_TUPLES);

		setAttribute(AG_GENERAL, "name", name);
		setAttribute(AG_GENERAL, "stream name", binItem.getChildValue("sname"));

		BinContainer priv = (BinContainer) binItem.getChildItem("priv");
		if (priv != null) {
			BinItem[] tas = (BinItem[]) priv.getAllChildItems("tuple_array");
			for (BinItem it : tas) {
				BinStructTupleArray ta = (BinStructTupleArray) it;
				String typeName = ta.getChildItem("type").getValueString();
				typeName = typeName.substring(typeName.lastIndexOf('_') + 1).toLowerCase();
				for (BinItem c : ta.getChildItems()) {
					if (c instanceof BinStructTuple) {
						BinStructTuple t = (BinStructTuple) c;
						setAttribute(AG_TUPLES, typeName + " : " + t.getChildItem("tkn_id").getValueString(),
								t.getChildItem("value").getValueString());
					}
				}
			}
		}
	}

	public BinStruct getBinStruct() {
		return binItem;
	}

	public void setAttribute(String group, String name, Object value) {
		Map<String, Object> ag = attributes.get(group);
		if (ag == null)
			throw new InvalidParameterException("Invalid attribute group");
		ag.put(name, value);
	}

	protected void setAttributeRange(String group, String name, BinContainer parent, String minFieldName,
			String maxFieldName) {
		int min = (Integer) parent.getChildValue(minFieldName);
		int max = (Integer) parent.getChildValue(maxFieldName);
		if (min == max) {
			setAttribute(group, name, String.format("%d", min));
		} else {
			setAttribute(group, name, String.format("%d .. %d", min, max));
		}
	}

	protected void addAttributeGroup(String group) {
		attributes.put(group, new LinkedHashMap<String, Object>());
	}

	public Set<String> getAttributeGroups() {
		return attributes.keySet();
	}

	public Set<String> getAttributes(String group) {
		return attributes.get(group).keySet();
	}

	public Object getAttribute(String group, String name) {
		return attributes.get(group).get(name);
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

	@Override
	public String toString() {
		return getName() + " " + getTypeName();
	}
}
