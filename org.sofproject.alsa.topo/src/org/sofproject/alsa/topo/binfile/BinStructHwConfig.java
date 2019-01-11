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

package org.sofproject.alsa.topo.binfile;

import org.sofproject.core.binfile.BinByte;
import org.sofproject.core.binfile.BinInteger;
import org.sofproject.core.binfile.BinIntegerArray;
import org.sofproject.core.binfile.BinShort;
import org.sofproject.core.binfile.BinStruct;

public class BinStructHwConfig extends BinStruct {

	public BinStructHwConfig() {
		this("");
	}
	
	public BinStructHwConfig(String name) {
		super(name);
		addChildItem(new BinInteger("size"));
		addChildItem(new BinInteger("id"));
		addChildItem(new BinInteger("fmt"));
		addChildItem(new BinByte("clock_gated"));
		addChildItem(new BinByte("invert_bclk"));
		addChildItem(new BinByte("invert_fsync"));
		addChildItem(new BinByte("bclk_master"));
		addChildItem(new BinByte("fsync_master"));
		addChildItem(new BinByte("mclk_direction"));
		addChildItem(new BinShort("reserved"));
		addChildItem(new BinInteger("mclk_rate"));
		addChildItem(new BinInteger("bclk_rate"));
		addChildItem(new BinInteger("fsync_rate"));
		addChildItem(new BinInteger("tdm_slots"));
		addChildItem(new BinInteger("tdm_slot_width"));
		addChildItem(new BinInteger("tx_slots"));
		addChildItem(new BinInteger("rx_slots"));
		addChildItem(new BinInteger("tx_channels"));
		addChildItem(new BinIntegerArray("tx_chanmap", BinTopoCommons.MAX_CHAN));
		addChildItem(new BinInteger("rx_channels"));
		addChildItem(new BinIntegerArray("rx_chanmap", BinTopoCommons.MAX_CHAN));
	}
}
