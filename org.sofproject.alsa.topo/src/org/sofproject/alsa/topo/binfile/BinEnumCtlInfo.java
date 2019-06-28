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

public class BinEnumCtlInfo extends BinItem {

	/**
	 * @formatter:off
	 */
	public enum Type {
		UNDEF(0), // let's accept 0 for extops
		VOLSW(1),
		VOLSW_SX(2),
		VOLSW_XR_SX(3),
		ENUM(4),
		BYTES(5),
		ENUM_VALUE(6),
		RANGE(7),
		STROBE(8);

		private final int val;

		Type(int val) {
			this.val = val;
		}

		public static Type find(int val) {
			for (Type bt : Type.values()) {
				if (bt.val == val)
					return bt;
			}
			throw new IllegalArgumentException("Unknown Ctl Type");
		}
	};

	Type value;

	public BinEnumCtlInfo(String name) {
		super(name);
	}

	@Override
	public BinItem read(ByteBuffer bb) {
		super.read(bb);
		this.value = Type.find(bb.getInt());
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

}
