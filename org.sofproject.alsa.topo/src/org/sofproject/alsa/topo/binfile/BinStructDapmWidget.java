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

import org.sofproject.core.binfile.BinArray;
import org.sofproject.core.binfile.BinInteger;
import org.sofproject.core.binfile.BinShort;
import org.sofproject.core.binfile.BinString;
import org.sofproject.core.binfile.BinStruct;

public class BinStructDapmWidget  extends BinStruct {

	public BinStructDapmWidget() {
		super("dapm widget");
		addChildItem(new BinInteger("size"));
		addChildItem(new BinEnumDapmWidgetType("id"));
		addChildItem(new BinString("name", BinTopoCommons.ELEM_ID_NAME_MAXLEN));
		addChildItem(new BinString("sname", BinTopoCommons.ELEM_ID_NAME_MAXLEN));
		addChildItem(new BinInteger("reg"));
		addChildItem(new BinInteger("shift"));
		addChildItem(new BinInteger("mask"));
		addChildItem(new BinInteger("subseq"));
		addChildItem(new BinInteger("invert"));
		addChildItem(new BinInteger("ignore_suspend"));
		addChildItem(new BinShort("event_flags"));
		addChildItem(new BinShort("event_type"));
		BinInteger numKctrl = new BinInteger("num_kcontrols"); 
		addChildItem(numKctrl);
		addChildItem(new BinStructPrivate("priv"));
		addChildItem(new BinArray<BinStructKcontrol>(BinStructKcontrol.class, "kcontrols", numKctrl));
	}

	@Override
	public String getValueString() {
		return (String) getChildValue("name");
	}

}
