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

package org.sofproject.fw.model;

import java.io.IOException;
import java.io.InputStream;

import org.sofproject.core.binfile.BinArray;
import org.sofproject.core.binfile.BinFile;
import org.sofproject.core.binfile.BinFileReader;
import org.sofproject.core.binfile.BinInteger;
import org.sofproject.core.binfile.BinStruct;
import org.sofproject.fw.binfile.FwBinBlockFactory;
import org.sofproject.fw.memmap.AplMemLayout;
import org.sofproject.fw.memmap.DspMemLayout;
import org.sofproject.fw.memmap.DspMemoryMap;
import org.sofproject.fw.memmap.DspMemoryRegion;
import org.sofproject.fw.memmap.SectionHeaderListReader;

public class FwBinFactory {

	/**
	 * Graph creator.
	 *
	 * @param fileName
	 * @param availableSize
	 * @param inputStream   Opened by environment specific stream reader.
	 * @return
	 */
	public static FwBinGraph readBinary(String fileName, int availableSize, InputStream inputStream, String mmFileName,
			InputStream mmInputStream) {
		try {
			// reading the fw binary file...
			BinFileReader reader = new BinFileReader(fileName, availableSize, inputStream);
			FwBinBlockFactory f = new FwBinBlockFactory();
			BinFile fwBin = reader.read(f);
			f.dispose();

			// TODO: determine target platform and associate appropriate mem layout
			// (based on fileName prefix e.g. apl- cnl-)
			DspMemLayout memLayout = new AplMemLayout();
			DspMemoryMap mm = new DspMemoryMap(memLayout);

			populateSegments(mm, fwBin);

			if (mmFileName != null) {
				// reading the memory map (.map from objdump -h) file ...
				SectionHeaderListReader mmReader = new SectionHeaderListReader(mmInputStream, mm);
				mmReader.read();
			}

			return new FwBinGraph(fwBin, mm);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	private static void populateSegments(DspMemoryMap memMap, BinFile fwBin) {
		BinStruct mft = (BinStruct) fwBin.getChildItem("fw manifest");
		BinArray<BinStruct> moduleEntries = (BinArray<BinStruct>) mft.getChildItem("module_entries");
		for (BinStruct me : moduleEntries.getItems()) {
			BinArray<BinStruct> segments = (BinArray<BinStruct>) me.getChildItem("segments");
			for (BinStruct seg : segments.getItems()) {
				BinInteger flags = (BinInteger) seg.getChildItem("flags");
				int baseAddr = (Integer) seg.getChildValue("v_base_addr");
				int length = ((Integer) flags.getChildValue("length")) * 4096;
				String name = (String) me.getChildValue("name")
						+ FwBinGraph.segmentIndexToString((Integer) flags.getChildValue("type"));

				memMap.addSegment(new DspMemoryRegion(name, baseAddr, length));
			}
		}
	}

}
