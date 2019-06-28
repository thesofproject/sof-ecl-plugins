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

public class ConfWidgetType extends ConfEnum {

	private static final Map<Integer, String> BIN_VALUE_MAP = new HashMap<Integer, String>() {
		private static final long serialVersionUID = 1L;

		{
			put(0, "input");
			put(1, "output");
			put(2, "mux");
			put(3, "mixer");
			put(4, "pga");
			put(5, "out_drv");
			put(6, "adc");
			put(7, "dac");
			put(8, "switch");
			put(9, "pre");
			put(10, "post");
			put(11, "aif_in");
			put(12, "aif_out");
			put(13, "dai_in");
			put(14, "dai_out");
			put(15, "dai_link");
			put(16, "buffer");
			put(17, "scheduler");
			put(18, "effect");
			put(19, "siggen");
			put(20, "src");
			put(21, "asrc");
			put(22, "encoder");
			put(23, "decoder");
		}
	};

	public ConfWidgetType(String name) {
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
