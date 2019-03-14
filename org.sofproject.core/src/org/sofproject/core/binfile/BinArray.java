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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BinArray<T extends BinItem> extends BinItem {

	final Class<T> itemClass;
	private List<T> value;
	int fixedSize;
	BinInteger dynSize;

	public BinArray(Class<T> itemClass, String name, int length) {
		super(name);
		this.itemClass = itemClass;
		this.fixedSize = length;
		this.value = new ArrayList<T>(length);
	}

	public BinArray(Class<T> itemClass, String name, BinInteger size) {
		super(name);
		this.itemClass = itemClass;
		// array not allocated yet, size known when 'size' is read
		this.dynSize = size;
	}

	@Override
	public BinItem read(ByteBuffer bb) {
		super.read(bb);
		if (value == null) {
			fixedSize = (Integer) dynSize.getValue();
			value = new ArrayList<T>(fixedSize);
		}
		for (int i = 0; i < fixedSize; ++i) {
			T item;
			try {
				item = itemClass.newInstance();
				item.setName(getName());
				item.read(bb);
				item.setParent(this);
				value.add(item);
			} catch (InstantiationException | IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return this;
	}

	public List<T> getItems() {
		return value;
	}

	public T getItem(int index) {
		return value.get(index);
	}

	@Override
	public Object getValue() {
		return Arrays.asList(value);
	}

	@Override
	public String getValueString() {
		return String.format("[%d]", value.size());
	}

	@Override
	public String toString() {
		return String.format("%s[%d] %s", itemClass.getSimpleName(), value.size(), getName());
	}

}
