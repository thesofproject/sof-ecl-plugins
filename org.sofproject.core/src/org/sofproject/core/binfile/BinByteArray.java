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

package org.sofproject.core.binfile;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class BinByteArray extends BinItem {

	private byte[] value;
	BinInteger dynSize;
	int sizeAdjustment = 0;

	public BinByteArray(String name, int length) {
		super(name);
		this.value = new byte[length];
	}

	public BinByteArray(String name, BinInteger size) {
		super(name);
		// array not allocated yet, size known when 'size' is read
		this.dynSize = size;
	}

	public BinByteArray(String name, BinInteger size, int sizeAdjustment) {
		super(name);
		// array not allocated yet, size known when 'size' is read
		this.dynSize = size;
		this.sizeAdjustment = sizeAdjustment;
	}

	@Override
	public BinItem read(ByteBuffer bb) {
		super.read(bb);
		if (value == null) {
			int size = (Integer) dynSize.getValue();
			size += sizeAdjustment;
			value = new byte[size];
		}
		bb.get(value);
		return this;
	}

	@Override
	public String getValueString() {
		StringBuffer s = new StringBuffer("[ ");
		for (byte b : value) { 
			s.append(String.format("%02x " , b));
		}
		s.append("]");
		return s.toString();
	}

	@Override
	public Object getValue() {
		return Arrays.asList(value);
	}

}
