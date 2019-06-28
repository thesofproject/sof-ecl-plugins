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

package org.sofproject.alsa.topo.model;

import java.util.List;

import org.sofproject.alsa.topo.binfile.BinEnumCtlInfo;
import org.sofproject.alsa.topo.binfile.BinEnumTupleType;
import org.sofproject.alsa.topo.binfile.BinStructChannel;
import org.sofproject.alsa.topo.binfile.BinStructCtlTlv;
import org.sofproject.alsa.topo.binfile.BinStructDapmGraph;
import org.sofproject.alsa.topo.binfile.BinStructDapmWidget;
import org.sofproject.alsa.topo.binfile.BinStructHwConfig;
import org.sofproject.alsa.topo.binfile.BinStructIoOps;
import org.sofproject.alsa.topo.binfile.BinStructKcontrol;
import org.sofproject.alsa.topo.binfile.BinStructKcontrolHeader;
import org.sofproject.alsa.topo.binfile.BinStructLinkConfig;
import org.sofproject.alsa.topo.binfile.BinStructPcm;
import org.sofproject.alsa.topo.binfile.BinStructStreamCaps;
import org.sofproject.alsa.topo.binfile.BinStructTlvDbscale;
import org.sofproject.alsa.topo.binfile.BinStructTuple;
import org.sofproject.alsa.topo.binfile.BinStructTupleArray;
import org.sofproject.alsa.topo.conf.ConfBackEnd;
import org.sofproject.alsa.topo.conf.ConfChannel;
import org.sofproject.alsa.topo.conf.ConfChannelId;
import org.sofproject.alsa.topo.conf.ConfControlBytes;
import org.sofproject.alsa.topo.conf.ConfControlMixer;
import org.sofproject.alsa.topo.conf.ConfData;
import org.sofproject.alsa.topo.conf.ConfElementWithData;
import org.sofproject.alsa.topo.conf.ConfHwConfig;
import org.sofproject.alsa.topo.conf.ConfLine;
import org.sofproject.alsa.topo.conf.ConfOps;
import org.sofproject.alsa.topo.conf.ConfPcm;
import org.sofproject.alsa.topo.conf.ConfPcmCapabilities;
import org.sofproject.alsa.topo.conf.ConfTlvDbScale;
import org.sofproject.alsa.topo.conf.ConfTopology;
import org.sofproject.alsa.topo.conf.ConfVendorTokens;
import org.sofproject.alsa.topo.conf.ConfVendorTuples;
import org.sofproject.alsa.topo.conf.ConfWidget;
import org.sofproject.core.binfile.BinArray;
import org.sofproject.core.binfile.BinByteArray;
import org.sofproject.core.binfile.BinContainer;
import org.sofproject.core.binfile.BinItem;

/**
 * Translates between binary tplg tree and conf model.
 */
public class AlsaTopoBin2Conf {

	/**
	 * Mapping of attribute names bin -> conf. NOTE: reg == -1 translates also to
	 * "no_pm true".
	 * @formatter:off
	 */
	private static final String[][] WIDGET_ATTRIBS = {
		{ "id", "type" },
		{ "sname", "stream_name" },
		{ "reg", "reg" },
		{ "shift", "shift" },
		{ "subseq", "subseq" },
		{ "invert", "invert" },
		{ "event_flags", "event_flags" },
		{ "event_type", "event_type" }
	};

	@SuppressWarnings("unchecked")
	public static ConfWidget createConfWidget(ConfTopology topology, BinStructDapmWidget binWidget, int index) {
		ConfWidget widget = new ConfWidget((String) binWidget.getChildValue("name"));
		widget.setBinSource(binWidget);
		widget.setAttributeValue("index", index);

		// process attributes
		for (String[] attr : WIDGET_ATTRIBS) {
			widget.setAttributeValue(attr[1], binWidget.getChildRawValue(attr[0]));
		}

		// add to topology
		topology.addWidget(widget);

		// process referred elements (like kcontrols)
		BinArray<BinStructKcontrol> kcontrols = (BinArray<BinStructKcontrol>) binWidget.getChildItem("kcontrols");
		for (BinStructKcontrol kcontrol : kcontrols.getItems()) {
			createConfKcontrol(topology, widget, kcontrol, index);
		}

		// process private data (vendor tuples)
		BinContainer priv = (BinContainer) binWidget.getChildItem("priv");
		if (priv != null) {
			BinItem[] tas = priv.getAllChildItems("tuple_array");
			for (BinItem it : tas) {
				BinStructTupleArray ta = (BinStructTupleArray) it;
				createData(topology, widget, ta);
			}
		}

		return widget;
	}

	private static void createConfKcontrol(ConfTopology topology, ConfWidget parent, BinStructKcontrol kcontrol,
			int index) {
		BinStructKcontrolHeader hdr = (BinStructKcontrolHeader) kcontrol.getChildValue("hdr");
		BinStructIoOps ioOps = (BinStructIoOps) hdr.getChildValue("ops");

		switch ((BinEnumCtlInfo.Type) ioOps.getChildValue("info")) {
		case VOLSW:
			createControlMixer(topology, parent, kcontrol, index);
			break;
		case BYTES:
			createControlBytes(topology, parent, kcontrol, index);
			break;
		default:
			break;
		}
	}

	private static void createData(ConfTopology topology, ConfElementWithData parent, BinStructTupleArray data) {
		List<BinItem> tuples = data.getChildItems();
		if (tuples.isEmpty())
			return; // Empty array possible?

		ConfVendorTuples vendorTuples = new ConfVendorTuples(
				String.format("%s-tuples-%d", parent.getName(), parent.getDataSize()));

		boolean tokensAssigned = false;
		BinEnumTupleType.Type tupleType = (BinEnumTupleType.Type) data.getChildValue("type");
		for (BinItem c : data.getChildItems()) {
			if (c instanceof BinStructTuple) {
				BinStructTuple t = (BinStructTuple) c;
				int tokenId = ((Integer) t.getChildRawValue("tkn_id"));
				if (!tokensAssigned) {
					ConfVendorTokens vendorTokens = topology.findVendorTokens(tokenId);
					vendorTuples.setAttributeValue(ConfVendorTuples.TOKENS, vendorTokens);
					tokensAssigned = true;
				}
				switch (tupleType) {
				case STRING:
					vendorTuples.addString(tokenId, (String) t.getChildValue("value"));
					break;
				case WORD:
					vendorTuples.addWord(tokenId, (Integer) t.getChildValue("value"));
					break;
				case SHORT:
					vendorTuples.addShort(tokenId, (Integer) t.getChildValue("value"));
					break;
				case BYTE:
					vendorTuples.addByte(tokenId, (Byte) t.getChildValue("value"));
				default:
					break;
				}

			}
		}

		topology.addVendorTuples(vendorTuples);

		ConfData confData = new ConfData(String.format("%s-data-%d", parent.getName(), parent.getDataSize()));
		confData.setAttributeValue("tuples", vendorTuples);

		topology.addData(confData);

		parent.addData(confData);
	}

	private static void createData(ConfTopology topology, ConfElementWithData parent, BinByteArray data) {
		ConfData confData = new ConfData(String.format("%s-priv", parent.getName()));
		confData.setAttributeValue("bytes", data.getValue());
		topology.addData(confData);
		parent.addData(confData);
	}

	/**
	 * @formatter:off
	 */
	private static final String[][] CONTROL_MIXER_ATTRIBS = {
		{ "min", "min" },
		{ "max", "max" },
		{ "platform_max", "platform_max" },
		{ "invert", "invert" }
	};

	private static void createControlMixer(ConfTopology topology, ConfWidget parent, BinStructKcontrol kcontrol,
			int index) {
		BinStructKcontrolHeader hdr = (BinStructKcontrolHeader) kcontrol.getChildValue("hdr");
		ConfControlMixer mixer = new ConfControlMixer((String) hdr.getChildValue("name"));
		mixer.setBinSource(kcontrol);

		for (String[] attr : CONTROL_MIXER_ATTRIBS) {
			mixer.setAttributeValue(attr[1], kcontrol.getChildValue(attr[0]));
		}

		createTlv(topology, mixer, hdr);

		setOps(mixer.getCtl(), (BinStructIoOps) hdr.getChildValue("ops"));

		for (int i = 0; i < (Integer) kcontrol.getChildValue("num_channels"); i++) {
			BinStructChannel binChannel = (BinStructChannel) kcontrol.getChildArrayField("channel", i);
			ConfChannelId id = new ConfChannelId("id");
			id.setValue(binChannel.getChildValue("id"));
			ConfChannel channel = new ConfChannel(id.getStringValue());
			channel.setAttributeValue("reg", binChannel.getChildValue("reg"));
			channel.setAttributeValue("shift", binChannel.getChildValue("shift"));
			mixer.addChannel(channel);
		}

		topology.addControlMixer(mixer);
		parent.addMixer(mixer);
	}

	/**
	 * @formatter:off
	 */
	private static final String[][] TLV_DB_SCALE_ATTRIBS = {
		{ "min", "min" },
		{ "step", "step" },
		{ "mute", "mute" }
	};

	/**
	 * TODO: support other tlv types based on 'type'
	 *
	 * @param topology
	 * @param parent
	 * @param kcontrol
	 */
	private static void createTlv(ConfTopology topology, ConfControlMixer parent, BinStructKcontrolHeader binHdr) {
		BinStructCtlTlv binTlv = (BinStructCtlTlv) binHdr.getChildValue("tlv");
		BinStructTlvDbscale binDbScale = (BinStructTlvDbscale) binTlv.getChildItem("scale");
		ConfTlvDbScale tlv = new ConfTlvDbScale(topology.generateTlvName());

		for (String[] attr : TLV_DB_SCALE_ATTRIBS) {
			tlv.setAttributeValue(attr[1], binDbScale.getChildRawValue(attr[0]));
		}
		topology.addTlv(tlv);
		parent.addTlv(tlv);
	}

	/**
	 * @formatter:off
	 */
	private static final String[][] OPS_ATTRIBS = {
		{ "get", "get" },
		{ "put", "put" },
		{ "info", "info" }
	};

	private static void setOps(ConfOps ops, BinStructIoOps binOps) {
		for (String[] attr : OPS_ATTRIBS) {
			ops.setAttributeValue(attr[1], binOps.getChildRawValue(attr[0]));
		}
	}

	/**
	 * @formatter:off
	 */
	private static final String[][] CONTROL_BYTES_ATTRIBS = {
		{ "base", "base" },
		{ "num_regs", "num_regs" },
		{ "max", "max" },
		{ "mask", "mask" }
	};

	private static void createControlBytes(ConfTopology topology, ConfWidget parent, BinStructKcontrol kcontrol, int index) {
		BinStructKcontrolHeader hdr = (BinStructKcontrolHeader) kcontrol.getChildValue("hdr");
		ConfControlBytes bytes = new ConfControlBytes((String) hdr.getChildValue("name"));
		bytes.setBinSource(kcontrol);

		for (String[] attr : CONTROL_BYTES_ATTRIBS) {
			bytes.setAttributeValue(attr[1], kcontrol.getChildValue(attr[0]));
		}
		bytes.setAttributeValue("access", hdr.getChildValue("access"));
		setOps(bytes.getCtl(), (BinStructIoOps) hdr.getChildValue("ops"));
		setOps(bytes.getExtCtl(), (BinStructIoOps) kcontrol.getChildValue("ext_ops"));
		createData(topology, bytes, (BinByteArray) kcontrol.getChildItem("priv_bytes"));

		topology.addControlBytes(bytes);
		parent.addBytes(bytes);
	}

	public static ConfPcm createConfPcm(ConfTopology topology, BinStructPcm binPcm) {
		ConfPcm pcm = new ConfPcm((String) binPcm.getChildValue("pcm_name"));
		pcm.setBinSource(binPcm);

		pcm.setAttributeValue("id", binPcm.getChildValue("pcm_id"));
		// TODO: other pcm attributes {compress, flags, flag_mask} ?

		topology.addPcm(pcm);

		// FE DAI
		String feDaiName = (String) binPcm.getChildValue("dai_name");
		if (!feDaiName.isEmpty()) {
			Integer feDaiId = (Integer) binPcm.getChildValue("dai_id");
			pcm.addFeDai(feDaiName, feDaiId);
		}

		// pcm caps for each playback and capture if present
		if ((Integer) binPcm.getChildValue("playback") > 0) {
			pcm.addCapabilities(ConfPcm.PLAYBACK_CAPS_NAME,
					createConfPcmCapabilities(topology, (BinStructStreamCaps) binPcm.getChildArrayField("caps", 0)));
		}
		if ((Integer) binPcm.getChildValue("capture") > 0) {
			pcm.addCapabilities(ConfPcm.CAPTURE_CAPS_NAME,
					createConfPcmCapabilities(topology, (BinStructStreamCaps) binPcm.getChildArrayField("caps", 1)));
		}

		return pcm;
	}

	/**
	 * @formatter:off
	 */
	private static String[][] PCM_CAPS_ATTRIBS = {
		{ "formats", "formats" },
		{ "rates", "rates" },
		{ "rate_min", "rate_min" },
		{ "rate_max", "rate_max" },
		{ "channels_min", "channels_min" },
		{ "channels_max", "channels_max" },
		{ "periods_min", "periods_min" },
		{ "periods_max", "periods_max" },
		{ "period_size_min", "period_size_min" },
		{ "period_size_max", "period_size_max" },
		{ "buffer_size_min", "buffer_size_min" },
		{ "buffer_size_max", "buffer_size_max" },
		{ "sig_bits", "sig_bits" }
	};

	private static ConfPcmCapabilities createConfPcmCapabilities(ConfTopology topology, BinStructStreamCaps binCaps) {
		ConfPcmCapabilities caps = new ConfPcmCapabilities((String) binCaps.getChildValue("name"));
		caps.setBinSource(binCaps);

		for (String[] attr : PCM_CAPS_ATTRIBS) {
			caps.setAttributeValue(attr[1], binCaps.getChildValue(attr[0]));
		}

		topology.addPcmCapabilities(caps);
		return caps;
	}

	/**
	 * @formatter:off
	 */
	private static final String[][] BACK_END_ATTRIBS = {
		{ "id", "id" },
		{ "stream_name", "stream_name" },
		{ "default_hw_config_id", "default_hw_conf_id" },
		{ "flag_mask", "flag_mask" },
		{ "flags", "flags" }
	};

	@SuppressWarnings("unchecked")
	public static ConfBackEnd createConfBackEnd(ConfTopology topology, BinStructLinkConfig binLinkConfig, int index) {
		ConfBackEnd backEnd = new ConfBackEnd((String) binLinkConfig.getChildValue("name"));
		backEnd.setBinSource(binLinkConfig);
		backEnd.setAttributeValue("index", index);

		for (String[] attr : BACK_END_ATTRIBS) {
			backEnd.setAttributeValue(attr[1], binLinkConfig.getChildValue(attr[0]));
		}

		topology.addBackEnd(backEnd);

		// array of hwconfigs
		int streamCount = (Integer) binLinkConfig.getChildValue("num_hw_configs");
		BinArray<? extends BinContainer> hwCfgArray = (BinArray<? extends BinContainer>) binLinkConfig
				.getChildItem("hw_config");
		for (int i = 0; i < streamCount; i++) {
			BinStructHwConfig binHwConfig = (BinStructHwConfig) hwCfgArray.getItem(i);
			ConfHwConfig confHwConfig = createConfHwConfig(topology, binHwConfig);
			backEnd.addHwConfig(confHwConfig);
		}

		// private data (vendor tuples)
		BinContainer priv = (BinContainer) binLinkConfig.getChildItem("priv");
		if (priv != null) {
			BinItem[] tas = priv.getAllChildItems("tuple_array");
			for (BinItem it : tas) {
				BinStructTupleArray ta = (BinStructTupleArray) it;
				createData(topology, backEnd, ta);
			}
		}

		return backEnd;
	}

	/**
	 * Mapping from tplg to conf for HWConfig.
	 * @formatter:off
	 */
	private static final String[][] HW_CONFIG_ATTRIBS = {
		{ "id", "id" },
		{ "fmt", "format" },
		{ "clock_gated", "pm_gate_clocks" },
		{ "invert_bclk", "bclk_invert" },
		{ "bclk_master", "bclk" },
		{ "fsync_master", "fsync" },
		{ "mclk_direction", "mclk" },
		{ "mclk_rate", "mclk_freq" },
		{ "bclk_rate", "bclk_freq" },
		{ "fsync_rate", "fsync_freq" },
		{ "tdm_slots", "tdm_slots" },
		{ "tdm_slot_width", "tdm_slot_width" },
		{ "tx_slots", "tx_slots" },
		{ "rx_slots", "rx_slots" },
		{ "tx_channels", "tx_channels" },
		// TODO: { "tx_chanmap", ""));
		{ "rx_channels", "rx_channels" },
		// TODO: { "rx_chanmap", ""));
	};

	/**
	 * Create HwConfig from the binary part. HwConfigs are embedded inside BE parts,
	 * so this will be called internally from another create(...)
	 */
	private static ConfHwConfig createConfHwConfig(ConfTopology topology, BinStructHwConfig binHwConfig) {
		ConfHwConfig hwConfig = new ConfHwConfig(topology.generateHwConfigName());
		hwConfig.setBinSource(binHwConfig);
		for (String[] attr : HW_CONFIG_ATTRIBS) {
			hwConfig.setAttributeValue(attr[1], binHwConfig.getChildValue(attr[0]));
		}
		topology.addHwConfig(hwConfig);
		return hwConfig;
	}

	/**
	 * @param topology
	 * @param connection
	 * @param blockIndex Index of the block, may be different than the connected widget index.
	 * @return
	 */
	public static ConfLine createLine(ConfTopology topology, BinStructDapmGraph connection, int blockIndex) {
		ConfWidget sink = topology.findWidget((String) connection.getChildValue("sink"));
		ConfWidget source = topology.findWidget((String) connection.getChildValue("source"));
		ConfLine line = new ConfLine(String.format("%s..%s", source.getName(), sink.getName()));
		line.setAttributeValue("index", blockIndex);
		topology.addLine(line);
		return line;
	}

}
