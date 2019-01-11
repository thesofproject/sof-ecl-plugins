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
import java.util.List;

import org.sofproject.alsa.topo.binfile.BinStructPcm;
import org.sofproject.alsa.topo.binfile.BinStructStreamCaps;
import org.sofproject.core.binfile.BinStruct;

/**
 * Groups of attributes: - playback capabilities (if supported) - capture
 * capabilities (if supported) - stream #i (0..7)
 */
public class AlsaTopoNodePcm extends AlsaTopoNode {

	public static final String AG_PLAYBACK_CAPS = "Playback Capabilities";
	public static final String AG_CAPTURE_CAPS = "Capture Capabilities";

	public AlsaTopoNodePcm(BinStructPcm binPcm) {
		super((String) binPcm.getChildValue("pcm_name"), binPcm);

		setTypeName("PCM : " + (String) binPcm.getChildValue("pcm_name"));
		if (hasPlayback()) {
			BinStruct playbackCap = (BinStruct) binPcm.getChildArrayField("caps", 0);
			addAttributeGroup(AG_PLAYBACK_CAPS);
			addCapabilityAttribs(AG_PLAYBACK_CAPS, playbackCap);
		}
		if (hasCapture()) {
			BinStruct captureCap = (BinStruct) binPcm.getChildArrayField("caps", 1);
			addAttributeGroup(AG_CAPTURE_CAPS);
			addCapabilityAttribs(AG_CAPTURE_CAPS, captureCap);
		}
	}

	private void addCapabilityAttribs(String group, BinStruct cap) {
		setAttribute(group, "name", (String) cap.getChildItem("name").getValueString());
		setAttribute(group, "formats", cap.getChildItem("formats").getValueString());
		setAttributeRange(group, "rate", cap, "rate_min", "rate_max");
		setAttributeRange(group, "channels", cap, "channels_min", "channels_max");
		setAttributeRange(group, "periods", cap, "periods_min", "periods_max");
		setAttributeRange(group, "period size", cap, "period_size_min", "period_size_max");
		setAttributeRange(group, "buffer size", cap, "buffer_size_min", "buffer_size_max");
	}

	public boolean hasPlayback() {
		return (Integer) getBinStruct().getChildValue("playback") > 0;
	}

	public boolean hasCapture() {
		return (Integer) getBinStruct().getChildValue("capture") > 0;
	}

	public List<String> getAllCapNames() {
		List<String> capNames = new ArrayList<>(2);
		if (hasPlayback()) {
			BinStructStreamCaps caps = (BinStructStreamCaps) getBinStruct()
					.getChildArrayField("caps", 0);
			capNames.add((String) caps.getChildValue("name"));
		}
		if (hasCapture()) {
			BinStructStreamCaps caps = (BinStructStreamCaps) getBinStruct()
					.getChildArrayField("caps", 1);
			capNames.add((String) caps.getChildValue("name"));
		}
		return capNames;
	};
}
