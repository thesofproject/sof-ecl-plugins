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

package org.sofproject.alsa.topo.binfile;

import java.nio.ByteBuffer;

import org.sofproject.core.binfile.BinArray;
import org.sofproject.core.binfile.BinByteArray;
import org.sofproject.core.binfile.BinInteger;
import org.sofproject.core.binfile.BinItem;
import org.sofproject.core.binfile.BinStruct;

public class BinStructKcontrol extends BinStruct {

	public BinStructKcontrol() {
		this("kcontrol");
	}

	public BinStructKcontrol(String name) {
		super(name);

		addChildItem(new BinStructKcontrolHeader("hdr"));
	}

	@Override
	public BinItem read(ByteBuffer bb) {
		int beginPos = bb.position();
		super.read(bb);

		BinStructKcontrolHeader hdr = (BinStructKcontrolHeader) getChildValue("hdr");
		BinStructIoOps ioOps = (BinStructIoOps) hdr.getChildValue("ops");

		// move right after the header
		int hdrSize = (Integer) hdr.getChildValue("size");
		bb.position(beginPos + hdrSize);

		// TODO: move to inherited classes
		switch ((BinEnumCtlInfo.Type) ioOps.getChildValue("info")) {
		case VOLSW:
			readMixer(bb);
			break;
		case BYTES:
			readBytes(bb);
			break;
		default:
			throw new IllegalArgumentException("kcontrol not parsed :(");
		}
		return this;
	}
	private void readMixer(ByteBuffer bb) {
		BinInteger size = new BinInteger("size");
		size.read(bb);
		addChildItem(size);
		BinInteger min = new BinInteger("min");
		min.read(bb);
		addChildItem(min);

		BinInteger max = new BinInteger("max");
		max.read(bb);
		addChildItem(max);

		BinInteger platformMax = new BinInteger("platform_max");
		platformMax.read(bb);
		addChildItem(platformMax);

		BinInteger invert = new BinInteger("invert");
		invert.read(bb);
		addChildItem(invert);

		BinInteger numChannels = new BinInteger("num_channels");
		numChannels.read(bb);
		addChildItem(numChannels);

		BinArray<BinStructChannel> channel = new BinArray<>(BinStructChannel.class, "channel", BinTopoCommons.MAX_CHAN);
		channel.read(bb);
		addChildItem(channel);
		
		BinInteger privBytesSize = new BinInteger("priv_bytes_size");
		privBytesSize.read(bb);
		addChildItem(privBytesSize);
		BinByteArray privBytes = new BinByteArray("priv_bytes", privBytesSize.getValue());
		privBytes.read(bb);
		addChildItem(privBytes);		
	}
	
	private void readBytes(ByteBuffer bb) {
		BinInteger size = new BinInteger("size");
		size.read(bb);
		addChildItem(size);
		BinInteger max = new BinInteger("max");
		max.read(bb);
		addChildItem(max);
		BinInteger mask = new BinInteger("mask");
		mask.read(bb);
		addChildItem(mask);
		BinInteger base = new BinInteger("base");
		base.read(bb);
		addChildItem(base);
		BinInteger numRegs = new BinInteger("num_regs");
		numRegs.read(bb);
		addChildItem(numRegs);
		BinStructIoOps extOps = new BinStructIoOps("ext_ops");
		extOps.read(bb);
		addChildItem(extOps);
		BinInteger privBytesSize = new BinInteger("priv_bytes_size");
		privBytesSize.read(bb);
		addChildItem(privBytesSize);
		BinByteArray privBytes = new BinByteArray("priv_bytes", privBytesSize.getValue());
		privBytes.read(bb);
		addChildItem(privBytes);
	}
}
