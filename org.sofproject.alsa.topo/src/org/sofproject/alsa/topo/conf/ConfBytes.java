package org.sofproject.alsa.topo.conf;

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

public class ConfBytes extends ConfAttribute {

	private static final String TYPE_NAME = "bytes";

	private byte[] value;

	public ConfBytes(String name) {
		super(TYPE_NAME, name);
	}

	@Override
	public void setValue(Object value) {
		if (value instanceof byte[])
			this.value = (byte[]) value;
		else
			throw new RuntimeException("Expected byte[] value for " + getName() + ", got " + value.getClass());
	}

	@Override
	public Object getValue() {
		return value;
	}

	@Override
	public boolean isChanged() {
		return value != null;
	}

	@Override
	public Type getNodeAtrributeType() {
		// TODO:
		return Type.NODE_A_OTHER;
	}

	@Override
	public String getStringValue() {
		StringBuilder sb = new StringBuilder();
		int i = 0;
		for (byte b : value) {
			sb.append(String.format("%02x", b));
			if (i++ == 8) {
				sb.append("...");
				break;
			} else {
				sb.append(' ');
			}
		}
		return sb.toString();
	}

}
