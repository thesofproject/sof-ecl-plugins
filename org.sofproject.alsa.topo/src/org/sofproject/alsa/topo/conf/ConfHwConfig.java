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

public class ConfHwConfig extends ConfElement {

	/**
	 * @param name Unique name.
	 * @formatter:off
	 */
	public ConfHwConfig(String name) {
		super(name, Arrays.asList(
				new ConfInteger("id", true /*0 is valid*/),
				new ConfHwConfigFormat("format"),
				new ConfHwConfigClkGated("pm_gate_clocks"),
				new ConfBoolean("bclk_invert"),
				new ConfBoolean("fsync_invert"),
				new ConfHwConfigBclk("bclk"),
				new ConfHwConfigFsync("fsync"),
				new ConfHwConfigMclk("mclk"),
				new ConfInteger("mclk_freq"),
				new ConfInteger("bclk_freq"),
				new ConfInteger("fsync_freq"),
				new ConfInteger("tdm_slots"),
				new ConfInteger("tdm_slot_width"),
				new ConfInteger("tx_slots"),
				new ConfInteger("rx_slots"),
				new ConfInteger("tx_channels"),
				new ConfInteger("rx_channels")));
	}

}
