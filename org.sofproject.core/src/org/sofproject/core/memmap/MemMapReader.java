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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MemMapReader {

	private String fileName;
	private InputStream is;

	private enum ReadState {
		SEARCHING_IDX, READING_SECTION, READING_SECTION_ATTRIBS,
	};

	public MemMapReader(String fileName, InputStream inputStream) {
		this.fileName = fileName;
		this.is = inputStream;
	}

	/*
	 * Idx Name Size VMA LMA File off Algn 0 .MemoryExceptionVector.text 00000006
	 * befe0400 befe0400 00000494 2**2 CONTENTS, ALLOC, LOAD, READONLY, CODE
	 */
	public FwImageMemMap read() throws IOException {
		// TODO: determine target platform and associate appropriate mem layout
		FwImageMemMap mm = new FwImageMemMap(fileName, new AplMemLayout());
		BufferedReader br = new BufferedReader(new InputStreamReader(is));

		String line;
		ReadState rs = ReadState.SEARCHING_IDX;
		FwImageMemSection sec = null;
		while ((line = br.readLine()) != null) {
			if (line.isEmpty())
				continue;
			String[] tokens = line.split("\\s+");
			switch (rs) {
			case SEARCHING_IDX:
				if (tokens.length > 0 && tokens[0].equals("Idx"))
					rs = ReadState.READING_SECTION;
				break;

			case READING_SECTION:
				// begin from the 1-st token, 0-th is empty
				int idx = Integer.parseInt(tokens[1]);
				int size = Integer.parseUnsignedInt(tokens[3], 16);
				int vma = Integer.parseUnsignedInt(tokens[4], 16);
				int lma = Integer.parseUnsignedInt(tokens[5], 16);
				int fileOffs = Integer.parseUnsignedInt(tokens[6], 16);
				sec = new FwImageMemSection(idx, tokens[2], size, vma, lma, fileOffs, 0 /* TODO alignment */);
				rs = ReadState.READING_SECTION_ATTRIBS;
				break;

			case READING_SECTION_ATTRIBS:
				for (String attr : tokens) {
					if (!attr.isEmpty() && sec != null) {
						attr = attr.replace(',', ' ').trim();
						sec.addAttr(attr);
					}
				}
				mm.addSection(sec);
				sec = null;
				rs = ReadState.READING_SECTION;
				break;
			}
		}
		mm.endOfMemMap();

		return mm;
	}
}
