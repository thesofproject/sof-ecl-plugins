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

public class ConfInteger extends ConfAttribute {

	private int value;
	private boolean alwaysChanged = false;

	public ConfInteger(String name) {
		super(name);
	}

	public ConfInteger(String name, boolean alwaysChanged) {
		this(name);
		this.alwaysChanged = alwaysChanged;
	}

	public ConfInteger(String name, int value) {
		this(name);
		setIntValue(value);
	}

	public ConfInteger(String name, boolean alwaysChanged, int value) {
		this(name, value);
		this.alwaysChanged = alwaysChanged;
	}

	public int getIntValue() {
		return this.value;
	}

	public void setIntValue(int value) {
		this.value = value;
	}

	@Override
	public Object getValue() {
		return value;
	}

	@Override
	public boolean isChanged() {
		return alwaysChanged || value != 0;
	}

	@Override
	public Type getNodeAtrributeType() {
		return Type.NODE_A_INTEGER;
	}

	@Override
	public void setValue(Object value) {
		if (value instanceof Integer)
			setIntValue((Integer) value);
		else if (value instanceof Short)
			setIntValue((Short) value);
		else if (value instanceof Byte)
			setIntValue((Byte) value);
		else if (value instanceof String) {
			setIntValue(Integer.parseInt((String)value));
		} else
			throw new RuntimeException("Expected Integer value for " + getName() + ", got " + value.getClass());
		((ConfElement)getParent()).onAttributeChange(this);
	}
}
