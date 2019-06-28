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

public class ConfPcmRates extends ConfBitSet {

	public static final String[][] BIT_NAMES = {
		{ "SND_PCM_RATE_5512", "5512" },
		{ "SND_PCM_RATE_8000", "8K" },
		{ "SND_PCM_RATE_11025", "11.025K" },
		{ "SND_PCM_RATE_16000", "16K" },
		{ "SND_PCM_RATE_22050", "22.05K" },
		{ "SND_PCM_RATE_32000", "32K" },
		{ "SND_PCM_RATE_44100", "44.1K" },
		{ "SND_PCM_RATE_48000", "48K" },
		{ "SND_PCM_RATE_64000", "64K" },
		{ "SND_PCM_RATE_88200", "88.2K" },
		{ "SND_PCM_RATE_96000", "96K" },
		{ "SND_PCM_RATE_176400", "176.4K" },
		{ "SND_PCM_RATE_192000", "192K" },
		{ "SND_PCM_RATE_CONTINUOUS", "Continuous" },
		{ "SND_PCM_RATE_KNOT", "Knot" }
	};

	public ConfPcmRates(String name) {
		super(name, BIT_NAMES);
	}

}
