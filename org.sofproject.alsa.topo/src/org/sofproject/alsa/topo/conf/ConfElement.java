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

package org.sofproject.alsa.topo.conf;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.sofproject.alsa.topo.model.AlsaTopoNode;
import org.sofproject.core.binfile.BinStruct;

/**
 * Element of the topology configuration file.
 *
 * Serialized as
 *
 * sectionName."name" { ... }
 *
 */
public class ConfElement extends ConfItem {

	/**
	 * Section name, set by embedded elements.
	 */
	private String sectionName;

	private String categoryName;

	private AlsaTopoNode parentNode;

	/**
	 * Map of attributes. References to external elements are represented as
	 * attributes.
	 */
	private Map<String, ConfAttribute> attribs;

	/**
	 * List of all children (embedded elements + attributes).
	 */
	private List<ConfItem> children = new ArrayList<>();

	/**
	 * Reference to binary structure if disassembled from binary.
	 */
	private BinStruct binSource;

	public ConfElement(String name, List<ConfAttribute> attribs) {
		super(name);
		if (attribs != null) {
			this.attribs = new LinkedHashMap<>(attribs.size());
			for (ConfAttribute a : attribs) {
				this.attribs.put(a.getName(), a);
				addChild(a);
			}
		} else {
			this.attribs = new LinkedHashMap<>();
		}
	}

	public void setParentNode(AlsaTopoNode parentNode) {
		this.parentNode = parentNode;
	}

	/**
	 * Used to serialize embedded elements.
	 *
	 * @param sectionName Name of the section.
	 */
	public void setSectionName(String sectionName) {
		this.sectionName = sectionName;
		rebuildCategoryName();
	}

	@Override
	public void setName(String newName) {
		super.setName(newName);
		rebuildCategoryName();
	}

	private void rebuildCategoryName() {
		categoryName = new StringBuilder(sectionName).append(".\"").append(getName()).append("\"").toString();
	}

	protected ConfElement getChildElement(String sectionName, String name) {
		for (ConfItem item : children) {
			if (!(item instanceof ConfElement))
				continue;
			ConfElement e = (ConfElement) item;
			if (e.sectionName.equals(sectionName) && e.getName().equals(name))
				return e;
		}
		return null;
	}

	public String getSectionName() {
		return sectionName;
	}

	public String getCategory() {
		return categoryName;
	}

	@Override
	public boolean hasChildren() {
		return !children.isEmpty();
	}

	@Override
	public Collection<ConfItem> getChildren() {
		return children;
	}

	protected void addChild(ConfItem child) {
		children.add(child);
		child.setParent(this);
	}

	protected void addAttribute(ConfAttribute attribute) {
		attribs.put(attribute.getName(), attribute);
		addChild(attribute);
	}

	protected ConfAttribute getAttribute(String name) {
		return attribs.get(name);
	}

	public Object getAttributeValue(String name) {
		ConfAttribute a = attribs.get(name);
		if (a == null)
			throw new RuntimeException("Invalid attribute name " + name);
		return a.getValue();
	}

	public void setAttributeValue(String name, Object val) {
		ConfAttribute a = attribs.get(name);
		if (a == null)
			throw new RuntimeException("Invalid attribute name " + name);
		a.setValue(val);
	}

	public Collection<ConfAttribute> getAttributes() {
		return attribs.values();
	}

	public void onAttributeChange(ConfAttribute attrib) {
		// if there is already a parent node assigned, notify it
		if (parentNode != null) {
			parentNode.notifyAttributeChanged(attrib);
		}
	}

	public void setBinSource(BinStruct binSource) {
		this.binSource = binSource;
	}

	public BinStruct getBinSource() {
		return binSource;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		if (sectionName != null) {
			sb.append(sectionName).append(".");
		}
		sb.append(getName());
		return sb.toString();
	}

	@Override
	public void serialize(Writer writer, String indent) throws IOException {
		writer.write(indent + getCategory());
		writer.write(" {\n");
		for (ConfItem child : children) {
			child.serialize(writer, indent + "   ");
		}
		writer.write(indent + "}\n");
	}
}
