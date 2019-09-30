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

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.sofproject.alsa.topo.conf.ConfAttribute;
import org.sofproject.alsa.topo.conf.ConfElement;
import org.sofproject.alsa.topo.conf.ConfItem;
import org.sofproject.core.binfile.BinStruct;
import org.sofproject.topo.ui.graph.ITopoCollectionNode;
import org.sofproject.topo.ui.graph.ITopoElement;
import org.sofproject.topo.ui.graph.ITopoGraph;
import org.sofproject.topo.ui.graph.ITopoNode;
import org.sofproject.topo.ui.graph.ITopoNodeAttribute;
import org.sofproject.ui.resources.SofResources;

import javafx.scene.paint.Color;

/**
 * Groups of attributes, common for all nodes: - general - vendor tuples
 */
public class AlsaTopoNode implements ITopoNode {

	private String typeName = "";

	private AlsaTopoGraph parentGraph;

	private ITopoCollectionNode parent;

	private List<AlsaTopoElement> elements = new LinkedList<>();

	/**
	 * Main model element, represented here as associated graph node. If the model
	 * is being "disassembled" from the binary topology file, it provides also the
	 * reference to part of the binary file. May give null otherwise.
	 */
	private ConfElement confElement;
	private PropertyChangeSupport pcs = new PropertyChangeSupport(this);

	boolean first = false;
	boolean last = false;
	int preferredColumn = -1;

	private List<AlsaTopoConnection> inConn = new ArrayList<>();
	private List<AlsaTopoConnection> outConn = new ArrayList<>();

	/**
	 * @param confElement Topology file element, already initialized with data from
	 *                    binItem.
	 */
	public AlsaTopoNode(ConfElement confElement) {
		this.confElement = confElement;
		confElement.setParentNode(this);
		for (ConfItem child : confElement.getChildren()) {
			if (child instanceof ConfElement) {
				ConfElement confChild = (ConfElement) child;
				elements.add(new AlsaTopoElement(confChild));
				confChild.setParentNode(this);
			}
		}
	}

	protected void setFirst(boolean first) {
		this.first = first;
	}

	protected void setLast(boolean last) {
		this.last = last;
	}

	protected void setPreferredColumn(int preferredColumn) {
		this.preferredColumn = preferredColumn;
	}

	public void setParentGraph(AlsaTopoGraph parentGraph) {
		this.parentGraph = parentGraph;
	}

	@Override
	public ITopoGraph getParentGraph() {
		return parentGraph;
	}

	public void setParent(ITopoCollectionNode parent) {
		this.parent = parent;
	}

	@Override
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

	public void removeInConn(AlsaTopoConnection conn) {
		inConn.remove(conn);
	}

	public void addOutConn(AlsaTopoConnection conn) {
		outConn.add(conn);
	}

	public void removeOutConn(AlsaTopoConnection conn) {
		outConn.remove(conn);
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

	@Override
	public String getName() {
		return confElement.getName();
	}

	@Override
	public void setName(String newName) {
		String oldName = confElement.getName();
		confElement.setName(newName);
		pcs.firePropertyChange(PROP_NAME, oldName, newName);
	}

	@Override
	public String getDescription() {
		return typeName;
	}

	@Override
	public String getTooltip() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isFirst() {
		return first;
	}

	@Override
	public boolean isLast() {
		return last;
	}

	@Override
	public int getPreferredColumn() {
		return preferredColumn;
	}

	@Override
	public ITopoCollectionNode getParent() {
		return parent;
	}

	@Override
	public Collection<? extends ITopoElement> getChildElements() {
		return elements;
	}

	@Override
	public Color getColor() {
		return SofResources.SOF_GREY;
	}

	@Override
	public Color getBorderColor() {
		return SofResources.SOF_GREY;
	}

	@Override
	public double getBorderWidth() {
		return 1.0;
	}

	@Override
	public Collection<? extends ITopoNodeAttribute> getAttributes() {
		return confElement.getAttributes();
	}

	@Override
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		pcs.addPropertyChangeListener(listener);
	}

	@Override
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		pcs.addPropertyChangeListener(listener);
	}

	public void notifyAttributeChanged(ConfAttribute attrib) {
		pcs.firePropertyChange(PROP_ATTRIB, null, attrib);
	}

	@Override
	public String toString() {
		return confElement.getName();
	}

	public void serialize(Writer writer, String indent) throws IOException {
		confElement.serialize(writer, indent);
	}

}
