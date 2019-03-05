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

import org.sofproject.core.binfile.BinByteArray;
import org.sofproject.core.binfile.BinDateBdl;
import org.sofproject.core.binfile.BinInteger;
import org.sofproject.core.binfile.BinIntegerArray;
import org.sofproject.core.binfile.BinItem;
import org.sofproject.core.binfile.BinSigInteger;
import org.sofproject.core.binfile.BinStruct;

public class BinStructCssManifestHeader extends BinStruct {

	public BinStructCssManifestHeader() {
		super("css manifest header");
		addChildItem(new BinInteger("type"));
		addChildItem(new BinInteger("header_len_dw"));
		addChildItem(new BinInteger("ver", true));
		addChildItem(new BinInteger("css_mod_id"));
		addChildItem(new BinInteger("mod_vendor", true));
		addChildItem(new BinDateBdl("date"));
		addChildItem(new BinInteger("size"));
		addChildItem(new BinSigInteger("header_id"));
		addChildItem(new BinInteger("padding"));
		addChildItem(new BinStructFwVersion("fw_version"));
		addChildItem(new BinInteger("svn"));
		addChildItem(new BinIntegerArray("reserved", 18));
		addChildItem(new BinInteger("modulus_size"));
		addChildItem(new BinInteger("exponent_size"));
		addChildItem(new BinByteArray("modulus", 256));
		addChildItem(new BinByteArray("exponent", 4));
		addChildItem(new BinByteArray("signature", 256));
	}

	@Override
	public BinItem read(ByteBuffer bb) {
		int beginPos = bb.position();
		super.read(bb);
		// skip the remaining part...
		bb.position(beginPos + (int) getChildValue("header_len_dw") * 4);
		return this;
	}

}
