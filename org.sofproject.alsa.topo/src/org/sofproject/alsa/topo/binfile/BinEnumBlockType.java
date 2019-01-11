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

public class BinEnumBlockType extends BinItem {

	public enum Type {
		MIXER(1),
		BYTES(2),
		ENUM(3),
		DAPM_GRAPH(4),
		DAPM_WIDGET(5),
		DAI_LINK(6),
		PCM(7),
		MANIFEST(8),
		CODEC_LINK(9),
		BACKEND_LINK(10),
		PDATA(11),
		DAI(12);

		private final int val;

		Type(int val) {
			this.val = val;
		}

		public static Type find(int val) {
			for (Type bt : Type.values()) {
				if (bt.val == val)
					return bt;
			}
			throw new IllegalArgumentException("Unknown BlockType");
		}
	};

	Type value;

	public BinEnumBlockType(String name) {
		super(name);
	}

	@Override
	public BinItem read(ByteBuffer bb) {
		super.read(bb);
		this.value = Type.find(bb.getInt());
		return this;
	}

	@Override
	public String getValueString() {
		return value.name();
	}

	@Override
	public Object getValue() {
		return value;
	}

}
