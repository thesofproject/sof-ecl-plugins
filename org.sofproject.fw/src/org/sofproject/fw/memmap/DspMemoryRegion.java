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
package org.sofproject.fw.memmap;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class DspMemoryRegion {

	public static final int UNDEFINED = -1;

	private DspMemoryRegion parent = null;

	private String name;
	private int baseAddr;
	private int sizeBytes;
	private int usedBytes;
	private boolean hideFree;

	private List<DspMemoryRegion> nested = new LinkedList<>();

	public DspMemoryRegion(String name) {
		this(name, UNDEFINED, UNDEFINED);
	}

	public DspMemoryRegion(String name, int baseAddr, int sizeBytes) {
		this(name, baseAddr, sizeBytes, false);
	}

	public DspMemoryRegion(String name, int baseAddr, int sizeBytes, boolean hideFree) {
		this.name = name;
		this.baseAddr = baseAddr;
		this.sizeBytes = sizeBytes;
		this.usedBytes = 0;
		this.hideFree = hideFree;
	}

	public void addNested(DspMemoryRegion child) {
		nested.add(child);
		child.setParent(this);
	}

	public String getName() {
		return name;
	}

	public int getBaseAddr() {
		return baseAddr;
	}

	public int getEndAddr() {
		return baseAddr + sizeBytes;
	}

	public int getSizeBytes() {
		return sizeBytes;
	}

	public int getUsedBytes() {
		return usedBytes;
	}

	public boolean getHideFree() {
		return hideFree;
	}

	public void setParent(DspMemoryRegion parent) {
		this.parent = parent;
	}

	public void addUsedBytes(int usedBytesDelta) {
		usedBytes += usedBytesDelta;
		if (parent != null) {
			parent.addUsedBytes(usedBytesDelta);
		}
	}

	public boolean containsAddr(int addr) {
		return getBaseAddr() <= addr && addr < getEndAddr();
	}

	public Collection<DspMemoryRegion> getNested() {
		return nested;
	}

}
