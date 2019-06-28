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

package org.sofproject.alsa.topo.conf;

import java.util.Arrays;

public class ConfWidget extends ConfElementWithData {

	/**
	 * There are also optional reference attributes "mixer" (array) and "mixer"
	 * (array).
	 *
	 * TODO: optional "enum" control.
	 *
	 * @param name Unique name of the widget.
	 *
	 * @formatter:off
	 */
	public ConfWidget(String name) {
		super(name,
				Arrays.asList(
						new ConfInteger("index"),
						new ConfWidgetType("type"),
						new ConfString("stream_name"),
						new ConfInteger("reg"),
						new ConfInteger("shift"),
						new ConfInteger("invert"),
						new ConfInteger("subseq"),
						new ConfInteger("event_type"),
						new ConfInteger("event_flags"),
						new ConfRefArray("mixer"),
						new ConfRefArray("bytes")));
	}

	public String getType() {
		return (String) getAttributeValue("type");
	}

	public void addMixer(ConfControlMixer mixer) {
		((ConfRefArray) getAttribute("mixer")).addRefValue(mixer);
	}

	public void addBytes(ConfControlBytes bytes) {
		((ConfRefArray) getAttribute("bytes")).addRefValue(bytes);
	}

}
