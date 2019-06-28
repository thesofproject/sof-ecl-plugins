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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Mapping is defined in alsa-lib/include/sound/asound.h
 */
public class ConfChannelId extends ConfEnum {

	private static final Map<Integer, String> BIN_VALUE_MAP = new HashMap<Integer, String>() {
		private static final long serialVersionUID = 1L;

		{
			// put(0, "unknown");
			// put(1, "na");
			put(2, "mono"); // mono
			put(3, "fl"); // front left
			put(4, "fr"); // front right
			put(5, "rl"); // rear left
			put(6, "rr"); // rear right
			put(7, "fc"); // front center
			put(8, "lfe"); // LFE
			put(9, "sl"); // side left
			put(10, "sr"); // side right
			put(11, "rc"); // rear center
			put(12, "flc"); // front left center
			put(13, "frc"); // front right center
			put(14, "rlc"); // rear left center
			put(15, "rrc"); // rear right center
			put(16, "flw"); // front left wide
			put(17, "frw"); // front right wide
			put(18, "flh"); // front left high
			put(19, "fch"); // front center high
			put(20, "frh"); // front right high
			put(21, "tc"); // top center
			put(22, "tfl"); // top front left
			put(23, "tfr"); // top front right
			put(24, "tfc"); // top front center
			put(25, "trl"); // top rear left
			put(26, "trr"); // top rear right
			put(27, "trc"); // top rear center
			put(28, "tflc"); // top front left center
			put(29, "tfrc"); // top front right center
			put(30, "tsl"); // top side left
			put(31, "tsr"); // top side right
			put(32, "llfe"); // left LFE
			put(33, "rlfe"); // right LFE
			put(34, "bc"); // bottom center
			put(35, "blc"); // bottom left center
			put(36, "brc"); // bottom right center
		}
	};

	public ConfChannelId(String name) {
		super(name);
	}

	@Override
	public Collection<String> getValueSet() {
		return BIN_VALUE_MAP.values();
	}

	@Override
	public void setIntValue(int value) {
		setStringValue(BIN_VALUE_MAP.get(value));
	}

}
