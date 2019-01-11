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

import org.sofproject.alsa.topo.binfile.BinStructLinkConfig;
import org.sofproject.core.binfile.BinArray;
import org.sofproject.core.binfile.BinContainer;

public class AlsaTopoNodeBe extends AlsaTopoNode {

	public static final String AG_HW_CONFIG = "HW Configuration";

	@SuppressWarnings("unchecked")
	public AlsaTopoNodeBe(BinStructLinkConfig binBe) {
		super((String) binBe.getChildValue("name"), binBe);
		setTypeName("BE");

		setAttribute(AG_GENERAL, "id", binBe.getChildValue("id"));
		setAttribute(AG_GENERAL, "default hw configuration id",
				binBe.getChildValue("default_hw_config_id"));

		int streamCount = (Integer) binBe.getChildValue("num_hw_configs");
		BinArray<? extends BinContainer> hwCfgArray = (BinArray<? extends BinContainer>) binBe.getChildItem("hw_config");
		for (int i = 0; i < streamCount; i++) {			
			String hwAg = String.format("%s #%d", AG_HW_CONFIG, i);
			addAttributeGroup(hwAg);
			BinContainer hwCfg = hwCfgArray.getItem(i);
			setAttribute(hwAg, "id", hwCfg.getChildValue("id"));
			setAttribute(hwAg, "fmt", hwCfg.getChildValue("fmt"));
			// TODO: add the rest
			setAttribute(hwAg, "todo...", "todo...");
		}

	}
}
