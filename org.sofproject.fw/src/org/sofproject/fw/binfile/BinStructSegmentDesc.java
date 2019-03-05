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

import org.sofproject.core.binfile.BinBitField;
import org.sofproject.core.binfile.BinInteger;
import org.sofproject.core.binfile.BinStruct;

public class BinStructSegmentDesc extends BinStruct {
	public BinStructSegmentDesc() {
		super("segment_desc");
		BinInteger flags = new BinInteger("flags");
		flags.addChildItem(new BinBitField("contents", 0, 0));
		flags.addChildItem(new BinBitField("alloc", 1, 1));
		flags.addChildItem(new BinBitField("load", 2, 2));
		flags.addChildItem(new BinBitField("readonly", 3, 3));
		flags.addChildItem(new BinBitField("code", 4, 4));
		flags.addChildItem(new BinBitField("data", 5, 5));
		// rsvd 7-6
		flags.addChildItem(new BinBitField("type", 11, 8));
		// rsvd 15-12
		flags.addChildItem(new BinBitField("length", 31, 16));
		addChildItem(flags);
		addChildItem(new BinInteger("v_base_addr", true));
		addChildItem(new BinInteger("file_offset"));
	}
}
