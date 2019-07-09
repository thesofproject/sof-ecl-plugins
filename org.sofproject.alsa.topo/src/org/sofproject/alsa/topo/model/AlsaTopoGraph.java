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
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

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
import org.sofproject.topo.ui.graph.ITopoCollectionNode;
import org.sofproject.topo.ui.graph.ITopoConnection;
import org.sofproject.topo.ui.graph.ITopoGraph;
import org.sofproject.topo.ui.graph.ITopoNode;

/**
 * Root container for the topology graph.
 */
public class AlsaTopoGraph implements ITopoGraph {

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
	private AlsaTopoNodeCollection<AlsaTopoNodeWidget> widgetIndex = new AlsaTopoNodeCollection<>("Widgets", false);
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

	// some collections to hide never-connected nodes from the top level
	private AlsaTopoNodeCollection<AlsaTopoNodeWidget> inputWidgets = new AlsaTopoNodeCollection<>("input Widgets");
	private AlsaTopoNodeCollection<AlsaTopoNodeWidget> outputWidgets = new AlsaTopoNodeCollection<>("output Widgets");
	private AlsaTopoNodeCollection<AlsaTopoNodeWidget> outDrvWidgets = new AlsaTopoNodeCollection<>("out_drv Widgets");

	// graph collection to fully model the topology
	private AlsaTopoNodeCollection<AlsaTopoPipeline> pipelines = new AlsaTopoNodeCollection<>("Pipelines",
			false /* exclude from graph */);

	private BinFile binTplg;

	private ConfTopology confTplg;

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

	@Override
	public Collection<? extends ITopoConnection> getConnections() {
		return connections;
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

	@SuppressWarnings("unchecked")
	private void createWidgets(Collection<BinItem> items, int blockIndex) {
		for (BinItem item : items) {
			if (item instanceof BinStructDapmWidget) {
				BinStructDapmWidget binWidget = (BinStructDapmWidget) item;
				ConfWidget confWidget = AlsaTopoBin2Conf.createConfWidget(confTplg, binWidget, blockIndex);
				AlsaTopoNodeWidget node = new AlsaTopoNodeWidget(confWidget);

				widgetIndex.add(node);

				switch (node.getTypeName()) {
				case "input":
					inputWidgets.add(node);
					break;
				case "output":
					outputWidgets.add(node);
					break;
				case "out_drv":
					outDrvWidgets.add(node);
					break;
				default:
					childNodes.add(node);
					break;
				}

				// add the node to its parent pipeline, create one if this is first item
				getPipeline(blockIndex).add(node);

				for (ConfControlMixer mixer : (Collection<ConfControlMixer>) confWidget.getAttributeValue("mixer")) {
					AlsaTopoNodeKcontrol mixerNode = new AlsaTopoNodeKcontrol(mixer);
					childNodes.add(mixerNode);
					controlMixers.add(mixerNode);
					connections.add(new AlsaTopoConnection(Type.CONTROL_PATH, mixerNode, node));
				}
				for (ConfControlBytes bytes : (Collection<ConfControlBytes>) confWidget.getAttributeValue("bytes")) {
					AlsaTopoNodeKcontrol bytesNode = new AlsaTopoNodeKcontrol(bytes);
					childNodes.add(bytesNode);
					controlBytes.add(bytesNode);
					connections.add(new AlsaTopoConnection(Type.CONTROL_PATH, bytesNode, node));
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
				childNodes.add(node);
				pcms.add(node);

				// create a node for each supported capability and create connections
				ConfPcmCapabilities playbackCaps = confPcm.getPlaybackCaps();
				if (playbackCaps != null) {
					AlsaTopoNode pcNode = new AlsaTopoNode(playbackCaps);
					childNodes.add(pcNode);
					pcmCapsIndex.add(pcNode);
					connections.add(new AlsaTopoConnection(Type.STREAM_PATH, node, pcNode));
				}
				ConfPcmCapabilities captureCaps = confPcm.getCaptureCaps();
				if (captureCaps != null) {
					AlsaTopoNode ccNode = new AlsaTopoNode(captureCaps);
					childNodes.add(ccNode);
					pcmCapsIndex.add(ccNode);
					connections.add(new AlsaTopoConnection(Type.STREAM_PATH, ccNode, node));
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
				beIndex.add(node);
				childNodes.add(node);

				for (ConfHwConfig hwConfig : (Collection<ConfHwConfig>) confBe.getAttributeValue("hw_configs")) {
					AlsaTopoNodeHwConfig hwConfigNode = new AlsaTopoNodeHwConfig(hwConfig);
					hwConfigs.add(hwConfigNode);
				}
			}
		}
	}

	private void createConnections(Collection<BinItem> items, int blockIndex) {
		for (BinItem item : items) {
			if (item instanceof BinStructDapmGraph) {
				BinStructDapmGraph binGraph = (BinStructDapmGraph) item;
				AlsaTopoBin2Conf.createLine(confTplg, binGraph, blockIndex);
				AlsaTopoNode srcNode = widgetIndex.get((String) binGraph.getChildValue("source"));
				AlsaTopoNode tgtNode = widgetIndex.get((String) binGraph.getChildValue("sink"));
				if (srcNode != null && tgtNode != null) {
					AlsaTopoConnection conn = new AlsaTopoConnection(Type.DAPM_PATH, srcNode, tgtNode);
					connections.add(conn);
					// TODO: there are lines connecting other pipelines, index=0, no widgets
					getPipeline(blockIndex).add(conn);
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
		for (AlsaTopoNodeWidget widget : widgetIndex.getElements()) {
			if (widget.getTypeName().equals("dai_in") || widget.getTypeName().equals("dai_out")) {
				AlsaTopoNodeBe be = beIndex.get(widget.getSname());
				if (be != null) {
					if (widget.getTypeName().equals("dai_in")) {
						connections.add(new AlsaTopoConnection(Type.STREAM_PATH, widget, be));
					} else {
						connections.add(new AlsaTopoConnection(Type.STREAM_PATH, be, widget));
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
						connections.add(new AlsaTopoConnection(Type.STREAM_PATH, pcmCaps, widget));
					} else {
						connections.add(new AlsaTopoConnection(Type.STREAM_PATH, widget, pcmCaps));
					}
				}
			} else if (widget.getTypeName().equals("scheduler")) {
				AlsaTopoNode node = widgetIndex.get(widget.getSname());
				if (node != null) { // some tplg-s declare schedulers with no stream name set
					connections.add(new AlsaTopoConnection(Type.CONTROL_PATH, widget, node));
				}
			}
		}
	}

}
