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

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.sofproject.core.binfile.BinStruct;
import org.sofproject.gst.topo.plugins.GstElement;
import org.sofproject.topo.ui.graph.ITopoCollectionNode;
import org.sofproject.topo.ui.graph.ITopoElement;
import org.sofproject.topo.ui.graph.ITopoGraph;
import org.sofproject.topo.ui.graph.ITopoNode;
import org.sofproject.topo.ui.graph.ITopoNodeAttribute;

import javafx.scene.paint.Color;

public class GstTopoNode implements ITopoNode {

	/**
	 * Reference to the source element.
	 */
	private GstElement elem;
	private Map<String, GstProperty> properties = new HashMap<>();
	private GstTopoGraph parent;
	private GstTopoConnection inConn; // single incoming connection allowed
	private GstTopoConnection outConn; // single outgoing connection allowed
	private PropertyChangeSupport pcs = new PropertyChangeSupport(this);

	public GstTopoNode(GstElement elem, GstTopoGraph parent) {
		this.elem = elem;
		elem.clonePropertiesTo(properties);
		for (GstProperty prop : properties.values()) {
			prop.setOwner(this);
		}
		this.parent = parent;
	}

	public boolean hasIncomingConnection() {
		return inConn != null;
	}

	public boolean hasOutgoingConnection() {
		return outConn != null;
	}

	public void setIncomingConnection(GstTopoConnection inConn) {
		if (inConn != null && this.inConn != null)
			throw new IllegalStateException("Incoming connection set");
		this.inConn = inConn;
	}

	public GstTopoConnection getIncomingConnection() {
		return inConn;
	}

	public void setOutgoingConnection(GstTopoConnection outConn) {
		if (outConn != null && this.outConn != null)
			throw new IllegalStateException("Outgoing connection set");
		this.outConn = outConn;
	}

	public GstTopoConnection getOutgoingConnection() {
		return outConn;
	}

	public void serialize(Writer writer) throws IOException {
		writer.write(elem.getName());
		for (GstProperty prop : properties.values()) {
			//if (!prop.isChanged())
			//	continue;
			writer.write(" ");
			prop.serialize(writer);
		}
	}

	@Override
	public String getName() {
		return elem.getName();
	}

	@Override
	public Collection<? extends ITopoNodeAttribute> getAttributes() {
		return properties.values();
	}

	public void setAttribute(String name, String strValue) {
		GstProperty prop = properties.get(name);
		if (prop != null) {
			prop.setValue(strValue);
		}
	}

	@Override
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		pcs.addPropertyChangeListener(listener);
	}

	@Override
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		pcs.removePropertyChangeListener(listener);
	}

	public void notifyPropertyChanged(GstProperty property) {
		pcs.firePropertyChange(PROP_ATTRIB, null, property);
	}

	@Override
	public void setName(String newName) {
		// TODO Unsupported, this is klass (element) name, not instance name
	}

	@Override
	public String getDescription() {
		return elem.getLongName();
	}

	@Override
	public String getTooltip() {
		return elem.getDescription();
	}

	@Override
	public boolean isFirst() {
		return !hasIncomingConnection();
	}

	@Override
	public boolean isLast() {
		return false; // not relevant for this graph
	}

	@Override
	public ITopoGraph getParentGraph() {
		return parent;
	}

	@Override
	public ITopoCollectionNode getParent() {
		return null;
	}

	@Override
	public Collection<? extends ITopoElement> getChildElements() {
		return Collections.emptyList();
	}

	@Override
	public Color getColor() {
		return Color.rgb(202, 247, 181);
	}

	@Override
	public Color getBorderColor() {
		return Color.rgb(52, 130, 15);
	}

	@Override
	public double getBorderWidth() {
		return 1.0;
	}

	@Override
	public BinStruct getBinStruct() {
		return null;
	}

	@Override
	public int getPreferredColumn() {
		// TODO Auto-generated method stub
		return 0;
	}

}
