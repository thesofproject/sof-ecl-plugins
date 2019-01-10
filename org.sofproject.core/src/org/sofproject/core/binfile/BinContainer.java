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
import java.util.List;

public class BinContainer extends BinItem {

	List<BinItem> children = new ArrayList<>();

	int dispSize = -1;
	
	public BinContainer(String name) {
		super(name);
	}
	
	public void addChildItem(BinItem item) {
		children.add(item);
		item.setParent(this);
	}

	public BinItem getChildItem(String name) {
		for (BinItem item : children) {
			if (item.getName().equals(name))
				return item;
		}	
		return null;
	}

	public BinItem[] getAllChildItems(String name) {
		List<BinItem> items = new ArrayList<>();
		for (BinItem item : children) {
			if (item.getName().equals(name))
				items.add(item);
		}
		return items.toArray(new BinItem[0]);
	}
	
	public Object getChildValue(String name) {
		BinItem item = getChildItem(name);
		return item != null ? item.getValue() : null;
	}
	
	public BinItem getChildArrayField(String name, int index) {
		for (BinItem field : children) {
			if (field.getName().equals(name))
				return ((BinArray<?>) field).getItem(index);
		}
		return null;
	}

	public List<BinItem> getChildItems() {
		return children;
	}

	@Override
	public BinItem read(ByteBuffer bb) {
		super.read(bb);
		for (BinItem item : children) {
			item.read(bb);
		}
		return this;
	}
	
	@Override
	public Object getValue() {
		return this;
	}

	public void setDispSize(int dispSize) {
		this.dispSize = dispSize;
	}
	
	@Override
	public String getValueString() {
		return String.format("[%d]", dispSize != -1 ? dispSize : children.size());
	}
	
	@Override
	public String toString() {
		return String.format("Container %s", getName());
	}
}
