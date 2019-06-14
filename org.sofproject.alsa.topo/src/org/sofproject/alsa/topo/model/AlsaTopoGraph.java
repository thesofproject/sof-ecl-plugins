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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.sofproject.alsa.topo.binfile.BinStructDapmGraph;
import org.sofproject.alsa.topo.binfile.BinStructDapmWidget;
import org.sofproject.alsa.topo.binfile.BinStructLinkConfig;
import org.sofproject.alsa.topo.binfile.BinStructPcm;
import org.sofproject.alsa.topo.model.AlsaTopoConnection.Type;
import org.sofproject.core.binfile.BinContainer;
import org.sofproject.core.binfile.BinFile;
import org.sofproject.core.binfile.BinItem;

/**
 * Root container for the topology graph.
 */
public class AlsaTopoGraph extends AlsaTopoItem {

	private List<AlsaTopoItem> childElements = new ArrayList<>();
	private List<AlsaTopoNodePcm> pcmList = new ArrayList<>();
	private Map<String, AlsaTopoNodePcm> pcmIndex = new HashMap<>();
	private Map<String, AlsaTopoNodeWidget> widgetIndex = new HashMap<>();
	private Map<String, AlsaTopoNodeBe> beIndex = new HashMap<>();

	private BinFile binTplg;

	public AlsaTopoGraph() {
		super("Graph");
	}

	public void setBinTopology(BinFile binTplg) {
		this.binTplg = binTplg;
		// TODO: resetting topo, remove all existing content

		populateGraph(binTplg.getChildItems());
		connectOthers();
	}

	public BinFile getBinTopology() {
		return binTplg;
	}

	private void populateGraph(Collection<BinItem> items) {
		for (BinItem item : items) {
			if (item instanceof BinStructDapmWidget) {
				addWidgetNode(new AlsaTopoNodeWidget((BinStructDapmWidget) item));
			} else if (item instanceof BinStructPcm) {
				addPcmNode(new AlsaTopoNodePcm((BinStructPcm) item));
			} else if (item instanceof BinStructLinkConfig) {
				addBeNode(new AlsaTopoNodeBe((BinStructLinkConfig) item));
			} else if (item instanceof BinStructDapmGraph) {
				BinStructDapmGraph dapmGraph = (BinStructDapmGraph) item;
				AlsaTopoNode srcNode = (AlsaTopoNode) findChild((String) dapmGraph.getChildValue("source"));
				AlsaTopoNode tgtNode = (AlsaTopoNode) findChild((String) dapmGraph.getChildValue("sink"));
				if (srcNode != null && tgtNode != null) {
					addChildElement(new AlsaTopoConnection(Type.DAPM_PATH, srcNode, tgtNode));
				} else {
//					throw new RuntimeException("Graph connection not found");
				}
			} else if (item instanceof BinContainer) {
				populateGraph(((BinContainer) item).getChildItems());
			}
		}
	}

	public void addPcmNode(AlsaTopoNodePcm pcmNode) {
		// Map instance to each active capability name
		for (String capName : pcmNode.getAllCapNames()) {
			pcmIndex.put(capName, pcmNode);
		}
		pcmList.add(pcmNode);
		addChildElement(pcmNode);
	}

	public void addWidgetNode(AlsaTopoNodeWidget widgetNode) {
		widgetIndex.put(widgetNode.getName(), widgetNode);
		addChildElement(widgetNode);
	}

	public void addBeNode(AlsaTopoNodeBe beNode) {
		beIndex.put(beNode.getName(), beNode);
		addChildElement(beNode);
	}

	public void addChildElement(AlsaTopoItem node) {
		childElements.add(node);
	}

	public void addChildElement(AlsaTopoItem node, int idx) {
		childElements.add(idx, node);
	}

	public List<AlsaTopoItem> getChildElements() {
		return childElements;
	}

	public void removeChildElement(AlsaTopoItem node) {
		childElements.remove(node);
	}

	public AlsaTopoItem findChild(String name) {
		AlsaTopoItem item = widgetIndex.get(name);
		if (item != null)
			return item;
		return pcmIndex.get(name);
	}

	private void connectOthers() {
		for (AlsaTopoNodeWidget widget : widgetIndex.values()) {
			if (widget.getTypeName().equals("dai_in") || widget.getTypeName().equals("dai_out")) {
				AlsaTopoNodeBe be = beIndex.get(widget.getSname());
				if (be != null) {
					if (widget.getTypeName().equals("dai_in")) {
						addChildElement(new AlsaTopoConnection(Type.STREAM_PATH, widget, be));
					} else {
						addChildElement(new AlsaTopoConnection(Type.STREAM_PATH, be, widget));
					}
				}
			} else if (widget.getTypeName().equals("aif_in") || widget.getTypeName().equals("aif_out")) {
				/*
				 * TODO: newer version of the topologies seem to skip this connection in the
				 * graph definition. Should avoid adding double one for old versions? Does not
				 * hurt atm
				 */
				AlsaTopoNodePcm pcm = pcmIndex.get(widget.getSname());
				if (pcm != null) {
					if (widget.getTypeName().equals("aif_in")) {
						addChildElement(new AlsaTopoConnection(Type.STREAM_PATH, pcm, widget));
					} else {
						addChildElement(new AlsaTopoConnection(Type.STREAM_PATH, widget, pcm));
					}
				}
			} else if (widget.getTypeName().equals("scheduler")) {
				AlsaTopoNode node = (AlsaTopoNode) findChild(widget.getSname());
				addChildElement(new AlsaTopoConnection(Type.CONTROL_PATH, widget, node));
			}
		}
	}
}
