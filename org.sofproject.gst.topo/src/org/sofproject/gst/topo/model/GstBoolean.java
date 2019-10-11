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

package org.sofproject.gst.topo.model;

public class GstBoolean extends GstProperty {

	private boolean value = false;
	private boolean defaultValue = false;

	public GstBoolean(String name, String description, boolean readOnly, boolean defaultValue) {
		super(name, description, readOnly);
		this.defaultValue = defaultValue;
		this.value = defaultValue;
	}

	@Override
	public Type getNodeAtrributeType() {
		return Type.NODE_A_BOOLEAN;
	}

	@Override
	public Object getValue() {
		return value;
	}

	@Override
	public Object getDefaultValue() {
		return defaultValue;
	}

	@Override
	public void setValue(Object value) {
		if (value instanceof Boolean) {
			this.value = (Boolean) value;
		} else if (value instanceof Integer) {
			this.value = ((Integer) value) == 0 ? false : true;
		} else if (value instanceof String) {
			if (value.equals("true")) {
				this.value = true;
			} else if (value.equals("false")) {
				this.value = false;
			} else {
				throw new RuntimeException("Unrecognized string value");
			}
		} else {
			throw new RuntimeException("Boolean value expected");
		}
		if (owner != null) {
			owner.notifyPropertyChanged(this);
		}
	}

	@Override
	public String getStringValue() {
		return value ? "true" : "false";
	}
}
