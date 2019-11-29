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
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.sofproject.alsa.topo.binfile.BinBlock;
import org.sofproject.alsa.topo.binfile.BinEnumBlockType;
import org.sofproject.alsa.topo.binfile.BinStructDapmGraph;
import org.sofproject.alsa.topo.binfile.BinStructDapmWidget;
import org.sofproject.alsa.topo.binfile.BinStructHdr;
import org.sofproject.alsa.topo.binfile.BinStructLinkConfig;
import org.sofproject.alsa.topo.binfile.BinStructPcm;
import org.sofproject.alsa.topo.conf.ConfBackEnd;
import org.sofproject.alsa.topo.conf.ConfControlBytes;
import org.sofproject.alsa.topo.conf.ConfControlMixer;
import org.sofproject.alsa.topo.conf.ConfData;
import org.sofproject.alsa.topo.conf.ConfGraph;
import org.sofproject.alsa.topo.conf.ConfHwConfig;
import org.sofproject.alsa.topo.conf.ConfPcm;
import org.sofproject.alsa.topo.conf.ConfPcmCapabilities;
import org.sofproject.alsa.topo.conf.ConfTlvDbScale;
import org.sofproject.alsa.topo.conf.ConfTopology;
import org.sofproject.alsa.topo.conf.ConfVendorTokens;
import org.sofproject.alsa.topo.conf.ConfVendorTuples;
import org.sofproject.alsa.topo.conf.ConfWidget;
import org.sofproject.alsa.topo.model.AlsaTopoConnection.Type;
import org.sofproject.core.binfile.BinFile;
import org.sofproject.core.binfile.BinItem;
import org.sofproject.core.ops.IRemoteOpsProvider;
import org.sofproject.gst.json.JsonProperty;
import org.sofproject.topo.ui.graph.ITopoCollectionNode;
import org.sofproject.topo.ui.graph.ITopoConnection;
import org.sofproject.topo.ui.graph.ITopoGraph;
import org.sofproject.topo.ui.graph.ITopoNode;

/**
 * Root container for the topology graph.
 */
public class AlsaTopoGraph implements ITopoGraph {

	public static final String PROP_GRAPH = "graph";

	private static final String[] nodeTypes = { AlsaTopoNodePcm.NODE_TYPE, AlsaTopoNodePcmCaps.NODE_TYPE };

	private PropertyChangeSupport pcs = new PropertyChangeSupport(this);

	private IFile inputFile;

	/**
	 * All child nodes to be displayed in the main graph.
	 */
	private List<AlsaTopoNode> childNodes = new ArrayList<>();

	/**
	 * Connections to be displayed in the main graph.
	 */
	private List<AlsaTopoConnection> connections = new ArrayList<>();

	private AlsaTopoNodeCollection<AlsaTopoNodePcm> pcms = new AlsaTopoNodeCollection<>("PCMs", false);
	private AlsaTopoNodeCollection<AlsaTopoNode> pcmCapsIndex = new AlsaTopoNodeCollection<>("PCM Capabilities", false);
	private Map<String, AlsaTopoNodeWidget> widgetIndex = new HashMap<>();
	private AlsaTopoNodeCollection<AlsaTopoNodeBe> beIndex = new AlsaTopoNodeCollection<>("BackEnds", false);
	private AlsaTopoNodeCollection<AlsaTopoNodeHwConfig> hwConfigs = new AlsaTopoNodeCollection<>("HW Configurations");
	private AlsaTopoNodeCollection<AlsaTopoNodeTlv> tlvs = new AlsaTopoNodeCollection<>("TLVs");
	private AlsaTopoNodeCollection<AlsaTopoNode> dataIndex = new AlsaTopoNodeCollection<>("Data", false);
	private AlsaTopoNodeCollection<AlsaTopoNodeKcontrol> controlMixers = new AlsaTopoNodeCollection<>("Control Mixers");
	private AlsaTopoNodeCollection<AlsaTopoNodeKcontrol> controlBytes = new AlsaTopoNodeCollection<>("Control Bytes");
	private AlsaTopoNodeCollection<AlsaTopoNodeVendorTokens> vTokensIndex = new AlsaTopoNodeCollection<>(
			"Vendor Tokens");
	private AlsaTopoNodeCollection<AlsaTopoNodeVendorTuples> vTuplesIndex = new AlsaTopoNodeCollection<>(
			"Vendor Tuples");

	// graph collection to fully model the topology
	private AlsaTopoNodeCollection<AlsaTopoPipeline> pipelines = new AlsaTopoNodeCollection<>("Pipelines",
			false /* do not display in graph */);

	// not shown in graph, not shown in outline
	private AlsaTopoNodeCollection<AlsaTopoPipeline> interConnections = new AlsaTopoNodeCollection<>(
			"Inter Connections", false);

	private BinFile binTplg;

	private ConfTopology confTplg;

	public AlsaTopoGraph(IFile file) {
		this.inputFile = file;
	}

	/**
	 * Topology read from a binary '.tplg' file is assigned and translated to the
	 * '.conf' topology model.
	 *
	 * @param binTplg
	 */
	public void setBinTopology(BinFile binTplg) {
		this.binTplg = binTplg;
		// TODO: resetting topo, remove all existing content

		confTplg = new ConfTopology(binTplg.getName());
		populateGraph(binTplg.getChildItems());
		createVendorNodes();
		createTlvNodes();
		createDataNodes();
		connectOthers();
	}

	@Override
	public Collection<? extends ITopoCollectionNode> getCollections() {
		return Arrays.asList(vTokensIndex, vTuplesIndex, tlvs, dataIndex, controlMixers, controlBytes, pcms,
				pcmCapsIndex, beIndex, hwConfigs, pipelines);
	}

	@Override
	public Collection<? extends ITopoNode> getNodes() {
		return childNodes;
	}

	private void addNode(ITopoNode node) {
		AlsaTopoNode atn = (AlsaTopoNode) node;
		atn.setParentGraph(this);
		childNodes.add(atn);
		// FIXME: find a better way to dispatch to the parent collection
		if (node instanceof AlsaTopoNodeWidget) {
			widgetIndex.put(node.getName(), (AlsaTopoNodeWidget) node);
		} else if (node instanceof AlsaTopoNodeControlMixer) {
			controlMixers.add((AlsaTopoNodeControlMixer) node);
		} else if (node instanceof AlsaTopoNodeControlBytes) {
			controlBytes.add((AlsaTopoNodeControlBytes) node);
		} else if (node instanceof AlsaTopoNodePcm) {
			pcms.add((AlsaTopoNodePcm) node);
		} else if (node instanceof AlsaTopoNodePcmCaps) {
			pcmCapsIndex.add((AlsaTopoNodePcmCaps) node);
		} else if (node instanceof AlsaTopoNodeBe) {
			beIndex.add((AlsaTopoNodeBe) node);
		}
	}

	@Override
	public void removeNode(ITopoNode node) {
		ITopoCollectionNode parent = node.getParent();
		if (parent != null) {
			parent.remove(node);
		}
		childNodes.remove(node);
		pcs.firePropertyChange(PROP_GRAPH, 0, 1);
	}

	@Override
	public Collection<? extends ITopoConnection> getConnections() {
		return connections;
	}

	private void addConnection(ITopoConnection connection) {
		AlsaTopoConnection atc = (AlsaTopoConnection) connection;
		atc.setParentGraph(this);

		// if source and target nodes belong to the same pipeline, the connection
		// index should equal the one of the nodes.
		// otherwise, as inter-pipeline connection, the index is set to 0.
		if (atc.getSource().getParent() == atc.getTarget().getParent()) {
			int index = (Integer) ((AlsaTopoPipeline) atc.getSource().getParent()).getConfElement()
					.getAttributeValue("index");
			getPipeline(index).add(atc);
		} else if (atc.getType() == AlsaTopoConnection.Type.DAPM_PATH) {
			newInterConnection().add(atc);
		}

		connections.add(atc);
	}

	@Override
	public void removeConnection(ITopoConnection connection) {
		connections.remove(connection);
		AlsaTopoConnection atc = (AlsaTopoConnection) connection;
		atc.setParentGraph(null);
		if (atc.getParentPipeline() != null) {
			atc.getParentPipeline().remove(atc);
		}
		if (atc.getTarget() != null) {
			((AlsaTopoNode) atc.getTarget()).removeInConn(atc);
		}
		if (atc.getSource() != null) {
			((AlsaTopoNode) atc.getSource()).removeOutConn(atc);
		}
		pcs.firePropertyChange(PROP_GRAPH, 0, 1);
	}

	@Override
	public String[] getNodeTypeIds() {
		return nodeTypes;
	}

	@Override
	public String getNodeDisplayName(String nodeId) {
		switch (nodeId) {
		case AlsaTopoNodePcm.NODE_TYPE:
			return "PCM";
		case AlsaTopoNodePcmCaps.NODE_TYPE:
			return "PCM Capabilities";
		default:
			return "Unknown?";
		}
	}

	@Override
	public ITopoNode createNode(String nodeId) {
		ITopoNode node = null;
		switch (nodeId) {
		case AlsaTopoNodePcm.NODE_TYPE:
			node = new AlsaTopoNodePcm(confTplg.createPcm());
			break;
		case AlsaTopoNodePcmCaps.NODE_TYPE:
			node = new AlsaTopoNodePcmCaps(confTplg.createPcmCapabilities());
			break;
		}
		if (node != null) {
			addNode(node);
		}
		pcs.firePropertyChange(PROP_GRAPH, 0, 1);
		return node;
	}

	@Override
	public ITopoConnection createConnection(ITopoNode source, ITopoNode target) {
		AlsaTopoConnection.Type type = AlsaTopoConnection.Type.DAPM_PATH;
		if (source instanceof AlsaTopoNodePcm || target instanceof AlsaTopoNodePcm || source instanceof AlsaTopoNodeBe
				|| target instanceof AlsaTopoNodeBe) {
			type = AlsaTopoConnection.Type.STREAM_PATH;
		}
		AlsaTopoConnection conn = new AlsaTopoConnection(type, (AlsaTopoNode) source, (AlsaTopoNode) target);
		addConnection(conn);
		pcs.firePropertyChange(PROP_GRAPH, 0, 1);
		return conn;
	}

	@Override
	public BinFile getBinTopology() {
		return binTplg;
	}

	public ConfTopology getConfTopology() {
		return confTplg;
	}

	private void populateGraph(Collection<BinItem> items) {
		for (BinItem item : items) {
			if (item instanceof BinBlock) {
				BinBlock block = (BinBlock) item;
				BinStructHdr hdr = (BinStructHdr) block.getChildItem("hdr");
				switch ((BinEnumBlockType.Type) hdr.getChildValue("type")) {
				case DAPM_WIDGET:
					createWidgets(block.getChildItems(), (Integer) hdr.getChildValue("index"));
					break;
				case PCM:
					createPcms(block.getChildItems());
					break;
				case BACKEND_LINK:
					createBackEnds(block.getChildItems());
					break;
				case DAPM_GRAPH:
					createConnections(block.getChildItems(), (Integer) hdr.getChildValue("index"));
					break;
				default:
					System.out.println("warn: unprocessed block " + hdr.getChildValue("type"));
					break;
				}
			}
		}
	}

	private AlsaTopoPipeline getPipeline(int blockIndex) {
		ConfGraph confGraph = confTplg.getGraph(blockIndex);
		AlsaTopoPipeline pipeline = pipelines.get(confGraph.getName());
		if (pipeline == null) {
			pipeline = new AlsaTopoPipeline(confGraph);
			pipelines.add(pipeline);
		}
		return pipeline;
	}

	private AlsaTopoPipeline newInterConnection() {
		ConfGraph confGraph = confTplg.getInterConnection(interConnections.size()); // always new one
		AlsaTopoPipeline interConn = new AlsaTopoPipeline(confGraph);
		interConnections.add(interConn);
		return interConn;
	}

	@SuppressWarnings("unchecked")
	private void createWidgets(Collection<BinItem> items, int blockIndex) {
		for (BinItem item : items) {
			if (item instanceof BinStructDapmWidget) {
				BinStructDapmWidget binWidget = (BinStructDapmWidget) item;
				ConfWidget confWidget = AlsaTopoBin2Conf.createConfWidget(confTplg, binWidget, blockIndex);
				AlsaTopoNodeWidget node = new AlsaTopoNodeWidget(confWidget);

				// add the node to its parent pipeline, create one if this is first item
				// note: the parent from general widgetIndex is overridden by pipeline here
				getPipeline(blockIndex).add(node);

				addNode(node);

				for (ConfControlMixer mixer : (Collection<ConfControlMixer>) confWidget.getAttributeValue("mixer")) {
					AlsaTopoNodeControlMixer mixerNode = new AlsaTopoNodeControlMixer(mixer);
					addNode(mixerNode);
					addConnection(new AlsaTopoConnection(Type.CONTROL_PATH, mixerNode, node));
				}
				for (ConfControlBytes bytes : (Collection<ConfControlBytes>) confWidget.getAttributeValue("bytes")) {
					AlsaTopoNodeControlBytes bytesNode = new AlsaTopoNodeControlBytes(bytes);
					addNode(bytesNode);
					addConnection(new AlsaTopoConnection(Type.CONTROL_PATH, bytesNode, node));
				}
			}
		}
	}

	private void createPcms(Collection<BinItem> items) {
		for (BinItem item : items) {
			if (item instanceof BinStructPcm) {
				BinStructPcm binPcm = (BinStructPcm) item;
				ConfPcm confPcm = AlsaTopoBin2Conf.createConfPcm(confTplg, binPcm);
				AlsaTopoNodePcm node = new AlsaTopoNodePcm(confPcm);
				addNode(node);

				// create a node for each supported capability and create connections
				ConfPcmCapabilities playbackCaps = confPcm.getPlaybackCaps();
				if (playbackCaps != null) {
					AlsaTopoNodePcmCaps pcNode = new AlsaTopoNodePcmCaps(playbackCaps);
					addNode(pcNode);
					addConnection(new AlsaTopoConnection(Type.STREAM_PATH, node, pcNode));
				}
				ConfPcmCapabilities captureCaps = confPcm.getCaptureCaps();
				if (captureCaps != null) {
					AlsaTopoNodePcmCaps ccNode = new AlsaTopoNodePcmCaps(captureCaps);
					addNode(ccNode);
					addConnection(new AlsaTopoConnection(Type.STREAM_PATH, ccNode, node));
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void createBackEnds(Collection<BinItem> items) {
		int index = 0;
		for (BinItem item : items) {
			if (item instanceof BinStructLinkConfig) {
				BinStructLinkConfig binLinkConfig = (BinStructLinkConfig) item;
				ConfBackEnd confBe = AlsaTopoBin2Conf.createConfBackEnd(confTplg, binLinkConfig, index++);
				AlsaTopoNodeBe node = new AlsaTopoNodeBe(confBe);
				addNode(node);

				for (ConfHwConfig hwConfig : (Collection<ConfHwConfig>) confBe.getAttributeValue("hw_configs")) {
					hwConfigs.add(new AlsaTopoNodeHwConfig(hwConfig));
				}
			}
		}
	}

	private void createConnections(Collection<BinItem> items, int blockIndex) {
		for (BinItem item : items) {
			if (item instanceof BinStructDapmGraph) {
				BinStructDapmGraph binGraph = (BinStructDapmGraph) item;
				AlsaTopoBin2Conf.createLine(confTplg, binGraph, blockIndex);
				AlsaTopoNode srcNode = widgetIndex.get(binGraph.getChildValue("source"));
				AlsaTopoNode tgtNode = widgetIndex.get(binGraph.getChildValue("sink"));
				if (srcNode != null && tgtNode != null) {
					AlsaTopoConnection conn = new AlsaTopoConnection(Type.DAPM_PATH, srcNode, tgtNode);
					// tell the layout to not follow connections between pipelines
					if (srcNode.getParent() != tgtNode.getParent()) {
						conn.setFollowMe(false);
					}
					// and no connection going out from the out_drv
					if (srcNode.getTypeName().equals("out_drv")) {
						conn.setFollowMe(false);
					}
					// but override all connections going towards out_drv even foreign ones
					// (for example some virtual-dai mixed pipes stream)
					if (tgtNode.getTypeName().equals("out_drv")) {
						conn.setFollowMe(true);
					}
					addConnection(conn);
				}
			}
		}
	}

	private void createVendorNodes() {
		// vendor tokens
		for (ConfVendorTokens vendorTokens : confTplg.getVendorTokens()) {
			vTokensIndex.add(new AlsaTopoNodeVendorTokens(vendorTokens));
		}

		// vendor tuples
		for (ConfVendorTuples vendorTuples : confTplg.getVendorTuples()) {
			vTuplesIndex.add(new AlsaTopoNodeVendorTuples(vendorTuples));
		}
	}

	private void createTlvNodes() {
		for (ConfTlvDbScale tlv : confTplg.getTlvs()) {
			AlsaTopoNodeTlv tlvNode = new AlsaTopoNodeTlv(tlv);
			tlvs.add(tlvNode);
		}
	}

	private void createDataNodes() {
		for (ConfData d : confTplg.getData()) {
			dataIndex.add(new AlsaTopoNode(d));
		}
	}

	private void connectOthers() {
		for (AlsaTopoNodeWidget widget : widgetIndex.values()) {
			if (widget.getTypeName().equals("dai_in") || widget.getTypeName().equals("dai_out")) {
				AlsaTopoNodeBe be = beIndex.get(widget.getSname());
				if (be != null) {
					if (widget.getTypeName().equals("dai_in")) {
						addConnection(new AlsaTopoConnection(Type.STREAM_PATH, widget, be));
					} else {
						addConnection(new AlsaTopoConnection(Type.STREAM_PATH, be, widget));
					}
				}
			} else if (widget.getTypeName().equals("aif_in") || widget.getTypeName().equals("aif_out")) {
				/*
				 * TODO: newer version of the topologies seem to skip this connection in the
				 * graph definition. Should avoid adding double one for old versions? Does not
				 * hurt atm
				 */
				AlsaTopoNode pcmCaps = pcmCapsIndex.get(widget.getSname());
				if (pcmCaps != null) {
					if (widget.getTypeName().equals("aif_in")) {
						addConnection(new AlsaTopoConnection(Type.STREAM_PATH, pcmCaps, widget));
					} else {
						addConnection(new AlsaTopoConnection(Type.STREAM_PATH, widget, pcmCaps));
					}
				}
			} else if (widget.getTypeName().equals("scheduler")) {
				AlsaTopoNode node = widgetIndex.get(widget.getSname());
				if (node != null) { // some tplg-s declare schedulers with no stream name set
					addConnection(new AlsaTopoConnection(Type.CONTROL_PATH, widget, node));
				}
			}
		}
	}

	@Override
	public void serialize() throws CoreException, IOException {
		ByteArrayOutputStream os = new ByteArrayOutputStream();

		Writer writer = new BufferedWriter(new OutputStreamWriter(os));
		// tlv-s
		tlvs.serialize(writer);

		// vendor tokens
		vTokensIndex.serialize(writer);

		// vendor tuples
		vTuplesIndex.serialize(writer);

		// data
		dataIndex.serialize(writer);

		// control bytes
		controlBytes.serialize(writer);

		// control mixers
		controlMixers.serialize(writer);

		// pcm capabilities
		pcmCapsIndex.serialize(writer);

		// pcm-s
		pcms.serialize(writer);

		// be-s
		beIndex.serialize(writer);

		// hw-configs
		hwConfigs.serialize(writer);

		// pipelines (widgets + graphs)
		pipelines.serialize(writer);
		interConnections.serialize(writer);

		writer.close();
		os.close();

		IPath inputFilePath = inputFile.getProjectRelativePath();
		IFile outputFile = inputFile.getProject().getFile(inputFilePath.addFileExtension("conf"));
		if (outputFile.exists()) {
			outputFile.setContents(new ByteArrayInputStream(os.toByteArray()), true, false, null);
		} else {
			outputFile.create(new ByteArrayInputStream(os.toByteArray()), false, null);
		}
		outputFile.getParent().refreshLocal(1, null);
	}
	
	@Override
	public void serializeJson(JsonProperty jsonProperty) throws CoreException, IOException {
	}

	@Override
	public IRemoteOpsProvider getRemoteOpsProvider() {
		return null; // no extra ops
	}

	@Override
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		pcs.addPropertyChangeListener(listener);
	}

	@Override
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		pcs.removePropertyChangeListener(listener);
	}

}
