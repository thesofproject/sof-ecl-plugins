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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Complete map of DSP memory areas divided into categories
 *
 * \ | Bringup.text | Bringup.rodata | Bringup.bss | Fw.text \ | 1st code
 * section from mem map | 2nd code section from mem map | ... | Fw.rodata |
 * Fw.bss \ | 1st section within bss from mem map | 2nd section within bss from
 * mem map | ... | Others (total num here, styled) | Free (total num here,
 * styled)
 */
public class DspMemoryMap {

	/**
	 * Memory layout of the target dsp platform.
	 */
	DspMemLayout memLayout;

	/**
	 * Flat list of sections. The list is might be populated out of order.
	 */
	private List<FwImageMemSection> sections = new ArrayList<>();

	/**
	 * Direct map between top level memory areas and fw image sections.
	 */
	private Map<DspMemoryRegion, List<FwImageMemSection>> sectionMap = new HashMap<>();

	/**
	 * Flat list of fw modules' segments as defined per manifest.
	 */
	private List<DspMemoryRegion> fwMftSegments = new ArrayList<>();

	public DspMemoryMap(DspMemLayout memLayout) {
		this.memLayout = memLayout;
		for (DspMemoryRegion topLevelRegion : memLayout.getMemRegions()) {
			sectionMap.put(topLevelRegion, new ArrayList<>());
		}
	}

	public void addSection(FwImageMemSection section) {
		sections.add(section);
	}

	/**
	 * Adds a large segment, one defined by the fw manifest to the appropriate top
	 * level memory area (L2 HP, L2 LP, ...).
	 *
	 * @param region
	 */
	public void addSegment(DspMemoryRegion region) {
		for (DspMemoryRegion topLevel : memLayout.getMemRegions()) {
			if (topLevel.containsAddr(region.getBaseAddr())) {
				topLevel.addNested(region);
			}
		}
	}

	/**
	 * Called once all sections are added to sort them by virtual memory address and
	 * map them into the DSP memory areas.
	 */
	public void endOfMemMap() {
		// add 'Others' region for each top level section in the outline
		// and set the address range to the entire top level section
		// to make sure that any FwImageMemSection that does not fit
		// into any region defined by the Fw Manifest will be eventually
		// added to Others
		for (DspMemoryRegion topLevel : memLayout.getMemRegions()) {
			topLevel.addNested(new DspMemoryRegion("Others", topLevel.getBaseAddr(), topLevel.getSizeBytes(), true));
		}

		// make sure fw image mem section are sorted by their VMA
		sections.sort((s1, s2) -> Integer.compare(s1.getVma(), s2.getVma()));

		// add each section to mappings
		for (FwImageMemSection sec : sections) {
			DspMemoryRegion topLevel = findRegionForAddress(sec.getVma());
			if (topLevel != null) {
				// add to the direct topLevel -> fw image mem section mapping
				sectionMap.get(topLevel).add(sec);

				// add to the outline
				for (DspMemoryRegion group : topLevel.getNested()) {
					if (group.containsAddr(sec.getVma())) {
						group.addNested(new DspMemoryRegion(sec.getName(), sec.getVma(), sec.getSize()));
						group.addUsedBytes(sec.getSize());
						break;
					}
				}
			}
		}
	}

	public DspMemLayout getMemLayout() {
		return memLayout;
	}

	public Collection<FwImageMemSection> getSections() {
		return sections;
	}

	public Collection<FwImageMemSection> getSectionsFromArea(DspMemoryRegion area) {
		return sectionMap.get(area);
	}

	private DspMemoryRegion findRegionForAddress(int addr) {
		for (DspMemoryRegion region : memLayout.getMemRegions()) {
			if (region.containsAddr(addr))
				return region;
		}
		return null;
	}

}
