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

import java.util.Arrays;
import java.util.Collection;

/**
 * Repository of all topology objects. There is one ConfTopology instance
 * associated with a graph.
 */
public class ConfTopology {

	/**
	 * Name of the topology
	 */
	private String name;

	private ConfSection<ConfVendorTokens> vendorTokens = new ConfSection<>("SectionVendorTokens");
	private ConfSection<ConfVendorTuples> vendorTuples = new ConfSection<>("SectionVendorTuples");
	private ConfSection<ConfData> dataSets = new ConfSection<>("SectionData");

	/**
	 * Collection of graphs (pipelines).
	 */
	private ConfSection<ConfGraph> graphs = new ConfSection<>("SectionGraph");

	/**
	 * Collection of widgets, unique name -> widget. There is a single list
	 * serialized to the conf file, while the parser may organize them into blocks
	 * based on the defined dapm-graph connections.
	 */
	private ConfSection<ConfWidget> widgets = new ConfSection<>("SectionWidget");

	private ConfSection<ConfControlMixer> mixers = new ConfSection<>("SectionControlMixer");
	private ConfSection<ConfControlBytes> bytes = new ConfSection<>("SectionControlBytes");
	private ConfSection<ConfHwConfig> hwConfigs = new ConfSection<>("SectionHWConfig");
	private ConfSection<ConfPcm> pcms = new ConfSection<>("SectionPCM");
	private ConfSection<ConfPcmCapabilities> pcmCaps = new ConfSection<>("SectionPCMCapabilities");
	private ConfSection<ConfBackEnd> backEnds = new ConfSection<>("SectionBE");
	private ConfSection<ConfTlvDbScale> tlvs = new ConfSection<>("SectionTLV");

	/**
	 * @param name
	 * @formatter:off
	 */
	public ConfTopology(String name) {
		this.name = name;

		// TODO: move to separate vendor (Intel) specific token src.
		addVendorTokens(new ConfVendorTokens("sof_buffer_tokens",
				Arrays.asList(
						new ConfInteger("SOF_TKN_BUF_SIZE", 100),
						new ConfInteger("SOF_TKN_BUF_CAPS", 101))));

		addVendorTokens(new ConfVendorTokens("sof_dai_tokens",
				Arrays.asList(
						new ConfInteger("SOF_TKN_DAI_TYPE", 154),
						new ConfInteger("SOF_TKN_DAI_INDEX", 155),
						new ConfInteger("SOF_TKN_DAI_DIRECTION", 156))));

		addVendorTokens(new ConfVendorTokens("sof_sched_tokens",
				Arrays.asList(
						new ConfInteger("SOF_TKN_SCHED_PERIOD", 200),
						new ConfInteger("SOF_TKN_SCHED_PRIORITY", 201),
						new ConfInteger("SOF_TKN_SCHED_MIPS", 202),
						new ConfInteger("SOF_TKN_SCHED_CORE", 203),
						new ConfInteger("SOF_TKN_SCHED_FRAMES", 204),
						new ConfInteger("SOF_TKN_SCHED_TIME_DOMAIN", 205))));

		addVendorTokens(new ConfVendorTokens("sof_volume_tokens",
				Arrays.asList(
						new ConfInteger("SOF_TKN_RAMP_STEP_TYPE", 250),
						new ConfInteger("SOF_TKN_RAMP_STEP_MS", 251))));

		addVendorTokens(new ConfVendorTokens("sof_src_tokens",
				Arrays.asList(
						new ConfInteger("SOF_TKN_SRC_RATE_IN", 300),
						new ConfInteger("SOF_TKN_SRC_RATE_OUT", 301))));

		addVendorTokens(new ConfVendorTokens("sof_pcm_tokens",
				Arrays.asList(
						new ConfInteger("SOF_TKN_PCM_DMAC_CONFIG", 353))));

		addVendorTokens(new ConfVendorTokens("sof_comp_tokens",
				Arrays.asList(
						new ConfInteger("SOF_TKN_COMP_PERIOD_SINK_COUNT", 400),
						new ConfInteger("SOF_TKN_COMP_PERIOD_SOURCE_COUNT", 401),
						new ConfInteger("SOF_TKN_COMP_FORMAT", 402))));

		addVendorTokens(new ConfVendorTokens("sof_ssp_tokens",
				Arrays.asList(
						new ConfInteger("SOF_TKN_INTEL_SSP_CLKS_CONTROL", 500),
						new ConfInteger("SOF_TKN_INTEL_SSP_MCLK_ID", 501),
						new ConfInteger("SOF_TKN_INTEL_SSP_SAMPLE_BITS", 502),
						new ConfInteger("SOF_TKN_INTEL_SSP_FRAME_PULSE_WIDTH", 503),
						new ConfInteger("SOF_TKN_INTEL_SSP_QUIRKS", 504),
						new ConfInteger("SOF_TKN_INTEL_SSP_TDM_PADDING_PER_SLOT", 505))));

		addVendorTokens(new ConfVendorTokens("sof_dmic_tokens",
				Arrays.asList(
						new ConfInteger("SOF_TKN_INTEL_DMIC_DRIVER_VERSION", 600),
						new ConfInteger("SOF_TKN_INTEL_DMIC_CLK_MIN", 601),
						new ConfInteger("SOF_TKN_INTEL_DMIC_CLK_MAX", 602),
						new ConfInteger("SOF_TKN_INTEL_DMIC_DUTY_MIN", 603),
						new ConfInteger("SOF_TKN_INTEL_DMIC_DUTY_MAX", 604),
						new ConfInteger("SOF_TKN_INTEL_DMIC_NUM_PDM_ACTIVE", 605),
						new ConfInteger("SOF_TKN_INTEL_DMIC_SAMPLE_RATE", 608),
						new ConfInteger("SOF_TKN_INTEL_DMIC_FIFO_WORD_LENGTH", 609))));

		addVendorTokens(new ConfVendorTokens("sof_dmic_pdm_tokens",
				Arrays.asList(
						new ConfInteger("SOF_TKN_INTEL_DMIC_PDM_CTRL_ID", 700),
						new ConfInteger("SOF_TKN_INTEL_DMIC_PDM_MIC_A_Enable", 701),
						new ConfInteger("SOF_TKN_INTEL_DMIC_PDM_MIC_B_Enable", 702),
						new ConfInteger("SOF_TKN_INTEL_DMIC_PDM_POLARITY_A", 703),
						new ConfInteger("SOF_TKN_INTEL_DMIC_PDM_POLARITY_B", 704),
						new ConfInteger("SOF_TKN_INTEL_DMIC_PDM_CLK_EDGE", 705),
						new ConfInteger("SOF_TKN_INTEL_DMIC_PDM_SKEW", 706))));

		addVendorTokens(new ConfVendorTokens("sof_tone_tokens",
				Arrays.asList(
						new ConfInteger("SOF_TKN_TONE_SAMPLE_RATE", 800))));

		addVendorTokens(new ConfVendorTokens("sof_process_tokens",
				Arrays.asList(
						new ConfInteger("SOF_TKN_PROCESS_TYPE", 900))));

	}

	public String getName() {
		return name;
	}

	public Collection<ConfSection<?>> getSections() {
		return Arrays.asList(vendorTokens, vendorTuples);
	}

	public void addVendorTokens(ConfVendorTokens vendorTkns) {
		vendorTokens.add(vendorTkns);
	}

	public Collection<ConfVendorTokens> getVendorTokens() {
		return vendorTokens.getElements();
	}

	public ConfVendorTokens findVendorTokens(int token) {
		for (ConfVendorTokens tokens : vendorTokens.getElements()) {
			if (tokens.find(token) != null)
				return tokens;
		}
		throw new RuntimeException("Topology: missing vendor token " + token);
	}

	public void addVendorTuples(ConfVendorTuples vendorTpls) {
		vendorTuples.add(vendorTpls);
	}

	public Collection<ConfVendorTuples> getVendorTuples() {
		return vendorTuples.getElements();
	}

	public void addData(ConfData dataSet) {
		dataSets.add(dataSet);
	}

	public void addWidget(ConfWidget widget) {
		widgets.add(widget);
	}

	public void addLine(ConfLine line) {
		ConfGraph graph = getGraph((Integer)line.getAttributeValue("index"));
		graph.addLine(line);
	}

	public void addControlMixer(ConfControlMixer mixer) {
		mixers.add(mixer);
	}

	public void addControlBytes(ConfControlBytes bytes) {
		this.bytes.add(bytes);
	}

	public void addPcm(ConfPcm pcm) {
		pcms.add(pcm);
	}

	public void addPcmCapabilities(ConfPcmCapabilities caps) {
		pcmCaps.add(caps);
	}

	public void addBackEnd(ConfBackEnd backEnd) {
		backEnds.add(backEnd);
	}

	public void addHwConfig(ConfHwConfig hwConfig) {
		hwConfigs.add(hwConfig);
	}

	public String generateHwConfigName() {
		return String.format("hw-config-%d", hwConfigs.size());
	}

	public void addTlv(ConfTlvDbScale tlv) {
		tlvs.add(tlv);
	}

	public String generateTlvName() {
		return String.format("tlv-%d", tlvs.size());
	}

	public Collection<ConfTlvDbScale> getTlvs() {
		return tlvs.getElements();
	}

	public Collection<ConfData> getData() {
		return dataSets.getElements();
	}

	public ConfWidget findWidget(String name) {
		return widgets.findElement(name);
	}

	private static String graphNameFromIndex(int index) {
		return String.format("pipeline-%d", index);
	}

	public ConfGraph getGraph(int index) {
		String graphName = graphNameFromIndex(index);
		ConfGraph graph = graphs.findElement(graphName);
		if (graph == null) {
			graph = new ConfGraph(graphName);
			graphs.add(graph);
		}
		return graph;
	}

	public Collection<ConfGraph> getGraphs() {
		return graphs.getElements();
	}

}
