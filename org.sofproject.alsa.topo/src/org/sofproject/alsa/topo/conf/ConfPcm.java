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

/**
 * TODO: support for streams
 */
public class ConfPcm extends ConfElement {

	public static final String PLAYBACK_CAPS_NAME = "playback";
	public static final String CAPTURE_CAPS_NAME = "capture";

	public ConfPcm(String name) {
		super(name, Arrays.asList(new ConfInteger("id")));
	}

	public void addFeDai(String name, int id) {
		ConfInteger daiId = new ConfInteger("id");
		daiId.setIntValue(id);
		ConfElement feDai = new ConfElement(name, Arrays.asList(daiId));
		feDai.setSectionName("dai");
		addChild(feDai);
	}

	public void addCapabilities(String name, ConfPcmCapabilities caps) {
		ConfReference capRef = new ConfReference("capabilities");
		capRef.setRefValue(caps);
		ConfElement pcm = new ConfElement(name, Arrays.asList(capRef));
		pcm.setSectionName("pcm");
		addChild(pcm);
	}

	public ConfPcmCapabilities getPlaybackCaps() {
		return getCaps(PLAYBACK_CAPS_NAME);
	}

	public ConfPcmCapabilities getCaptureCaps() {
		return getCaps(CAPTURE_CAPS_NAME);
	}

	private ConfPcmCapabilities getCaps(String name) {
		ConfElement e = getChildElement("pcm", name);
		if (e != null)
			return (ConfPcmCapabilities) e.getAttributeValue("capabilities");
		return null;
	}
}
