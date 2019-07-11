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

import java.io.IOException;
import java.io.Writer;

public class ConfCtlAccess extends ConfBitSet {

	/**
	 * @formatter:off
	 */
	public static final String[][] BIT_NAMES = {
		{"read", "read"},
		{"write", "write"},
		{"volatile", "volatile"},
		{"timestamp", "timestamp"},
		{"tlv_read", "TLV read"},
		{"tlv_write", "TLV write"},
		{"tlv_command", "TLV command"},
		{"rsvd_7", "rsvd_7"},
		{"inactive", "inactive"},
		{"lock", "lock"},
		{"owner", "owner"},
		{"rsvd_11", "rsvd_11"},
		{"rsvd_12", "rsvd_12"},
		{"rsvd_13", "rsvd_13"},
		{"rsvd_14", "rsvd_14"},
		{"rsvd_15", "rsvd_15"},
		{"rsvd_16", "rsvd_16"},
		{"rsvd_17", "rsvd_17"},
		{"rsvd_18", "rsvd_18"},
		{"rsvd_19", "rsvd_19"},
		{"rsvd_20", "rsvd_20"},
		{"rsvd_21", "rsvd_21"},
		{"rsvd_22", "rsvd_22"},
		{"rsvd_23", "rsvd_23"},
		{"rsvd_24", "rsvd_24"},
		{"rsvd_25", "rsvd_25"},
		{"rsvd_26", "rsvd_26"},
		{"rsvd_27", "rsvd_27"},
		{"tlv_callback", "TLV callback"},
		{"user?", "User space"},
	};

	public ConfCtlAccess(String name) {
		super(name, BIT_NAMES);
	}

	@Override
	public void serialize(Writer writer, String indent) throws IOException {
		if (isChanged()) {
			writer.write(indent);
			writer.write(getName());
			writer.write(" [\n");

			long mask = 1;
			String nameIndent = indent + "   ";
			for (int i = 0; i < 64; i++) {
				if ((mask & (Long)getValue()) != 0) {
					writer.write(nameIndent);
					writer.write(bitNames[i][0]);
					writer.write('\n');
				}
				mask <<= 1;
			}

			writer.write(indent);
			writer.write("]\n");
		}
	}
}
