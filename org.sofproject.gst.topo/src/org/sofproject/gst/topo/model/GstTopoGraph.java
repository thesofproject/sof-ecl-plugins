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
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.window.Window;
import org.sofproject.core.binfile.BinFile;
import org.sofproject.core.ops.IRemoteOpsProvider;
import org.sofproject.gst.json.JsonProperty;
import org.sofproject.gst.topo.ops.GstGraphOpsProvider;
import org.sofproject.gst.topo.plugins.GstElement;
import org.sofproject.gst.topo.plugins.GstPlugin;
import org.sofproject.gst.topo.plugins.GstPluginDb;
import org.sofproject.gst.topo.ui.handlers.GstNewElementDialog;
import org.sofproject.topo.ui.graph.ITopoCollectionNode;
import org.sofproject.topo.ui.graph.ITopoConnection;
import org.sofproject.topo.ui.graph.ITopoGraph;
import org.sofproject.topo.ui.graph.ITopoNode;

import com.fasterxml.jackson.databind.ObjectMapper;


public class GstTopoGraph implements ITopoGraph {

	public static final String[] NODE_TYPE_IDS = { "org.sofproject.gst.topo.element" };

	private IFile fileInput;
	private GstPluginDb pdb;
	private List<String> nodeTypes = new ArrayList<>();
	private List<GstTopoConnection> connections = new LinkedList<>();

	private GstTopoPipeline pipeline = new GstTopoPipeline();
	private GstGraphOpsProvider opsProvider = new GstGraphOpsProvider(this);

	private PropertyChangeSupport pcs = new PropertyChangeSupport(this);

	public GstTopoGraph(IFile fileInput, GstPluginDb pdb) {
		this.fileInput = fileInput;
		this.pdb = pdb;

		// build list of available node types (= gst elements)
		for (GstPlugin plg : pdb.getPlugins()) {
			for (GstElement elem : plg.getElements()) {
				StringBuilder qName = new StringBuilder(plg.getName()).append(".").append(elem.getName());
				nodeTypes.add(qName.toString());
			}
		}
	}

	public GstPluginDb getPluginDb() {
		return pdb;
	}

	public IFile getFileInput() {
		return fileInput;
	}

	@Override
	public Collection<? extends ITopoCollectionNode> getCollections() {
		return Arrays.asList(pipeline);
	}

	@Override
	public Collection<? extends ITopoNode> getNodes() {
		return pipeline.getChildren();
	}

	@Override
	public ITopoNode createNode(String nodeId) {
		GstNewElementDialog dlg = new GstNewElementDialog(null, pdb);
		if (dlg.open() == Window.OK) {
			// get first select item and create...
			GstElement elem = dlg.getSelectedElement();
			if (elem != null) {
				GstTopoNode node = new GstTopoNode(elem, this);
				pipeline.add(node);
				pcs.firePropertyChange("Graph", 0, 1);
				return node;
			}
		}
		return null;
	}

	@Override
	public void removeNode(ITopoNode node) {
		pipeline.remove(node);
		pcs.firePropertyChange("Graph", 1, 0);
	}

	public void addNode(GstTopoNode node) {
		pipeline.add(node);
	}

	@Override
	public Collection<? extends ITopoConnection> getConnections() {
		return connections;
	}

	@Override
	public ITopoConnection createConnection(ITopoNode source, ITopoNode target) {
		GstTopoNode gstSrc = (GstTopoNode) source;
		GstTopoNode gstTgt = (GstTopoNode) target;
		if (gstSrc.hasOutgoingConnection() || gstTgt.hasIncomingConnection())
			throw new IllegalStateException("Multiple connections not allowed");
		GstTopoConnection connection = new GstTopoConnection(this, gstSrc, gstTgt);
		connections.add(connection);
		return connection;
	}

	@Override
	public void removeConnection(ITopoConnection connection) {
		connections.remove(connection);
		((GstTopoConnection) connection).disconnect();
	}

	public void addConnection(GstTopoConnection connection) {
		connections.add(connection);
	}

	@Override
	public String[] getNodeTypeIds() {
		return NODE_TYPE_IDS;
	}

	@Override
	public String getNodeDisplayName(String nodeId) {
		return "GStreamer element";
	}

	@Override
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		pcs.addPropertyChangeListener(listener);
	}

	@Override
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		pcs.removePropertyChangeListener(listener);
	}

	@Override
	public BinFile getBinTopology() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void serialize() throws CoreException, IOException {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		Writer writer = new BufferedWriter(new OutputStreamWriter(os));
		pipeline.serialize(writer);
		writer.close();
		os.close();

		fileInput.setContents(new ByteArrayInputStream(os.toByteArray()), true, false, null);
	}
	
	@Override
	public void serializeJson(JsonProperty jsonProperty) throws CoreException, IOException {
		try {
			File file = new File(jsonProperty.getName()+".json");
			jsonProperty.setTemplate(getPipelineString());
			BufferedWriter writer = new BufferedWriter(new FileWriter(file));
			ObjectMapper obj = new ObjectMapper();
			obj.writeValue(writer, jsonProperty);
			writer.close();
			
		}catch(Exception e) {
			System.out.println(e.toString());
		}
	}

	public String getPipelineString() {
		try {
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			Writer writer = new BufferedWriter(new OutputStreamWriter(os));
			pipeline.serialize(writer);
			writer.close();
			os.close();

			return os.toString();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public IRemoteOpsProvider getRemoteOpsProvider() {
		return opsProvider;
	}

}
