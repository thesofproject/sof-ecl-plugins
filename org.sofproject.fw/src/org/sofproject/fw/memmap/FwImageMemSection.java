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

import java.util.HashSet;
import java.util.Set;

public class FwImageMemSection {
	private int idx;
	private String name;
	private int size;
	private int vma;
	private int lma;
	private int fileOff;
	private int align;

	private Set<String> attrs = new HashSet<>();

	public FwImageMemSection(int idx, String name, int size, int vma, int lma, int fileOff, int align) {
		this.idx = idx;
		this.name = name;
		this.size = size;
		this.vma = vma;
		this.lma = lma;
		this.fileOff = fileOff;
		this.align = align;
	}

	void addAttr(String attr) {
		attrs.add(attr.toLowerCase());
	}

	public boolean allocsMem() {
		return hasAttr("alloc");
	}

	public boolean isStack() {
		return getName().indexOf("stack") != -1;
	}

	public boolean isSystemHeap() {
		return isHeap() && getName().indexOf("system") != -1;
	}

	public boolean isHeap() {
		return getName().indexOf("heap") != -1;
	}

	public boolean hasAttr(String attr) {
		return attrs.contains(attr.toLowerCase());
	}

	public int getIdx() {
		return idx;
	}

	public String getName() {
		return name;
	}

	public int getSize() {
		return size;
	}

	public int getVma() {
		return vma;
	}

	public int getLma() {
		return lma;
	}

	public int getFileOff() {
		return fileOff;
	}

	public int getAlign() {
		return align;
	}

	public boolean beginsInRange(int beginAddr, int endAddr) {
		return beginAddr <= getVma() && getVma() < endAddr;
	}

	@Override
	public String toString() {
		return String.format("%s %08x %x", getName(), getVma(), getSize());
	}

}
