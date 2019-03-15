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
package org.sofproject.core.memmap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FwImageMemMap {

	/**
	 * Name of the memory map file.
	 */
	String fileName;

	/**
	 * Memory layout of the target dsp platform.
	 */
	IMemLayout memLayout;

	private List<FwImageMemSection> sections = new ArrayList<>();

	private Map<MemSegment, List<FwImageMemSection>> secMap = new HashMap<>();

	public FwImageMemMap(String fileName, IMemLayout memLayout) {
		this.fileName = fileName;
		this.memLayout = memLayout;
		for (MemSegment segment : memLayout.getMemSegments()) {
			secMap.put(segment, new ArrayList<>());
		}
	}

	public void addSection(FwImageMemSection sec) {
		sections.add(sec);
	}

	public void endOfMemMap() {
		sections.sort((s1, s2) -> Integer.compare(s1.getVma(), s2.getVma()));
		for (FwImageMemSection sec : sections) {
			MemSegment seg = mapSectionToSegment(sec);
			// TODO: skip sections that do not belong to any
			// segment defined by the passed mem layout
			if (seg != null) {
				secMap.get(seg).add(sec);
			}
		}
	}

	public IMemLayout getMemLayout() {
		return memLayout;
	}

	public Collection<FwImageMemSection> getSections() {
		return sections;
	}

	public Collection<FwImageMemSection> getSectionsFromSegment(MemSegment seg) {
		return secMap.get(seg);
	}

	private MemSegment mapSectionToSegment(FwImageMemSection sec) {
		for (MemSegment segment : memLayout.getMemSegments()) {
			if (sec.isInSegment(segment))
				return segment;
		}
		return null;
	}

}
