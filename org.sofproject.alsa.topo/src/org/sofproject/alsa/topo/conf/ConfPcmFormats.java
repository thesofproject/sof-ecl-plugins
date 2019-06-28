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

public class ConfPcmFormats extends ConfBitSet {

	public static final String[][] BIT_NAMES = {
			{ "S8", "signed 8-bit" },
			{ "U8", "unsigned 8-bit" },
			{ "S16_LE", "signed 16-bit Little Endian" },
			{ "S16_BE", "signed 16-bit Big Endian" },
			{ "U16_LE", "unsigned 16-bit Little Endian" },
			{ "U16_BE", "unsigned 16-bit Big Endian" },
			{ "S24_LE", "signed 24-bit Little Endian" },
			{ "S24_BE", "signed 24-bit Big Endian" },
			{ "U24_LE", "unsigned 24-bit Little Endian" },
			{ "U24_BE", "unsigned 24-bit Big Endian" },
			{ "S32_LE", "signed 32-bit LE" },
			{ "S32_BE", "signed 32-bit BE" },
			{ "U32_LE", "unsigned 32-bit LE" },
			{ "U32_BE", "unsigned 32-bit BE" },
			{ "FLOAT_LE", "4-byte float, IEEE-754 32-bit, range -1.0 to 1.0" },
			{ "FLOAT_BE", "4-byte float, IEEE-754 32-bit, range -1.0 to 1.0" },
			{ "FLOAT64_LE", "8-byte float, IEEE-754 64-bit, range -1.0 to 1.0" },
			{ "FLOAT64_BE", "8-byte float, IEEE-754 64-bit, range -1.0 to 1.0" },
			{ "IEC958_SUBFRAME_LE", "IEC-958 subframe, Little Endian" },
			{ "IEC958_SUBFRAME_BE", "IEC-958 subframe, Big Endian" },
			{ "MU_LAW", "mu-law"},
			{ "A_LAW", "A-law" },
			{ "IMA_ADPCM", "IMA ADPCM" },
			{ "MPEG", "MPEG" },
			{ "GSM", "GSM" },
			{ "S20_LE", "signed 20-bit, in 4 bytes LSB justified, LE" },
			{ "S20_BE", "signed 20-bit, in 4 bytes LSB justified, BE" },
			{ "U20_LE", "unsigned 20-bit, in four bytes, LSB justified, LE" },
			{ "U20_BE", "unsigned 20-bit, in four bytes, LSB justified, BE" },
			{ "", "" }, // gap
			{ "", "" },
			{ "SPECIAL", "special" },
			{ "S24_3LE", "signed 24-bit, in three bytes, LE" },
			{ "S24_3BE", "signed 24-bit, in three bytes, BE" },
			{ "U24_3LE", "unsigned 24-bit, in three bytes, LE" },
			{ "U24_3BE", "unsgined 24-bit, in three bytes, BE" },
			{ "S20_3LE", "signed 20-bit, in three bytes, LE" },
			{ "S20_3BE", "signed 20-bit, in three bytes, BE" },
			{ "U20_3LE", "unsigned 20-bit, in three bytes, LE" },
			{ "U20_3BE", "unsigned 20-bit, in three bytes, BE" },
			{ "S18_3LE", "signed 18-bit, in three bytes, LE" },
			{ "S18_3BE", "signed 18-bit, in three bytes, BE" },
			{ "U18_3LE", "unsigned 18-bit, in three bytes, LE" },
			{ "U18_3BE", "unsigned 18-bit, in three bytes, BE" },
			{ "G723_24", "8 samples in 3 bytes" },
			{ "G723_24_1B", "1 sample in 1 byte" },
			{ "G723_40", "8 Samples in 5 bytes" },
			{ "G723_40_1B", "1 sample in 1 byte" },
			{ "DSD_U8", "DSD, 1-byte samples DSD (x8)" },
			{ "DSD_U16_LE", "DSD, 2-byte samples DSD (x16), little endian" },
			{ "DSD_U32_LE", "DSD, 4-byte samples DSD (x32), little endian" },
			{ "DSD_U16_BE", "DSD, 2-byte samples DSD (x16), big endian" },
			{ "DSD_U32_BE", "DSD, 4-byte samples DSD (x32), big endian" }
	};

	public ConfPcmFormats(String name) {
		super(name, BIT_NAMES);
	}
}
