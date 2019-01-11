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

import org.sofproject.core.binfile.BinContainer;
import org.sofproject.core.binfile.BinItem;

public class BinBlock extends BinContainer {

	public BinBlock(String name) {
		super(name);
		addChildItem(new BinStructHdr("hdr"));
	}

	@Override
	public BinItem read(ByteBuffer bb) {
		// read the static part (header)	
		super.read(bb);
		
		// read the child items
		BinStructHdr hdr = (BinStructHdr) getChildItem("hdr");
		switch ((BinEnumBlockType.Type) hdr.getChildValue("type")) {
		case MANIFEST:
			setName("manifest-block");
			addChildItem(new BinStructManifest().read(bb));
			setDispSize(1);
			break;
		case DAPM_WIDGET:
			setName("dapm-widget-block");
			for (int i = 0; i < (Integer) hdr.getChildValue("count"); ++i) {
				addChildItem(new BinStructDapmWidget().read(bb));
			}
			setDispSize((Integer) hdr.getChildValue("count"));
			break;
		case PCM:
			setName("pcm-block");
			for (int i = 0; i < (Integer) hdr.getChildValue("count"); ++i) {
				addChildItem(new BinStructPcm().read(bb));
			}
			setDispSize((Integer) hdr.getChildValue("count"));
			break;			
		case BACKEND_LINK:
			setName("be-link-block");
			for (int i = 0; i < (Integer) hdr.getChildValue("count"); ++i) {
				addChildItem(new BinStructLinkConfig().read(bb));
			}
			setDispSize((Integer) hdr.getChildValue("count"));
			break;			
		case DAPM_GRAPH:
			setName("dapm-graph-block");
			for (int i = 0; i < (Integer) hdr.getChildValue("count"); ++i) {
				addChildItem(new BinStructDapmGraph().read(bb));
			}
			setDispSize((Integer) hdr.getChildValue("count"));
			break;			
		case MIXER:
			// TODO:
		default:
			// unknown block, skip its payload
			bb.position(bb.position() + (Integer) hdr.getChildValue("payload_size"));
			break;
		}
		return this;
	}
	
	
}
