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

import java.nio.ByteBuffer;

import org.sofproject.core.binfile.BinItem;

public class BinEnumDapmWidgetType extends BinItem {

	/**
	 * @formatter:off
	 */
	public enum Type {
		DAPM_INPUT(0),
		DAPM_OUTPUT(1),
		DAPM_MUX(2),
		DAPM_MIXER(3),
		DAPM_PGA(4),
		DAPM_OUT_DRV(5),
		DAPM_ADC(6),
		DAPM_DAC(7),
		DAPM_SWITCH(8),
		DAPM_PRE(9),
		DAPM_POST(10),
		DAPM_AIF_IN(11),
		DAPM_AIF_OUT(12),
		DAPM_DAI_IN(13),
		DAPM_DAI_OUT(14),
		DAPM_DAI_LINK(15),
		DAPM_BUFFER(16),
		DAPM_SCHEDULER(17),
		DAPM_EFFECT(18),
		DAPM_SIGGEN(19),
		DAPM_SRC(20),
		DAPM_ASRC(21),
		DAPM_ENCODER(22),
		DAPM_DECODER(23);

		private final int val;

		Type(int val) {
			this.val = val;
		}

		public static Type find(int val) {
			for (Type bt : Type.values()) {
				if (bt.val == val)
					return bt;
			}
			throw new IllegalArgumentException("Unknown Dapm Widget Type " + val);
		}
	};

	Type value;

	public BinEnumDapmWidgetType(String name) {
		super(name);
	}

	@Override
	public BinItem read(ByteBuffer bb) {
		super.read(bb);
		try {
			this.value = Type.find(bb.getInt());
		} catch (IllegalArgumentException e) {
			throw new RuntimeException(String.format("Unknown item at 0x%x: %s", bb.position(), e.getMessage()), e);
		}
		return this;
	}

	@Override
	public Object getValue() {
		return value;
	}

	@Override
	public Object getRawValue() {
		return value.val;
	}

	@Override
	public String getValueString() {
		return value.name();
	}

	@Override
	public String toString() {
		return String.format("%s : %s", getName(), value.toString());
	}

	public String getShortString() {
		// no DAPM_
		return value.toString().substring(5).toLowerCase();
	}
}
