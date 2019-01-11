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

package org.sofproject.fw.binfile;

import java.nio.ByteBuffer;

import org.sofproject.core.binfile.BinInteger;
import org.sofproject.core.binfile.BinItem;
import org.sofproject.core.binfile.BinShort;
import org.sofproject.core.binfile.BinSigInteger;
import org.sofproject.core.binfile.BinStruct;

public class BinStructCseManifestHeader extends BinStruct {

	public BinStructCseManifestHeader() {
		super("header");
		addChildItem(new BinSigInteger("signature"));
		addChildItem(new BinInteger("number_of_modules"));
		addChildItem(new BinShort("ver"));
	}

	@Override
	public BinItem read(ByteBuffer bb) {
		int beginPos = bb.position();
		super.read(bb);
		short ver = (short) getChildValue("ver");
		int offset = 0;
		if (ver == 0x101) {
			// 
		} else if (ver == 0x102) {
			offset = 1;
		} else {
			throw new RuntimeException("Unknown CSE Manifest Header version");
		}
		bb.position(beginPos + 4 * (offset + 7));
		BinInteger hdrSize = new BinInteger("hdr_size");
		hdrSize.read(bb);
		addChildItem(hdrSize);
		bb.position(beginPos + 4 * (offset + 19));
		BinInteger mftLength = new BinInteger("mft_length");
		mftLength.read(bb);
		addChildItem(mftLength);
		bb.position(beginPos + hdrSize.getValue());
		return this;
	}

}
