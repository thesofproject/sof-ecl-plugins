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
package org.sofproject.fw.keys;

import java.util.Arrays;

public class SimpleFwKeyResolver {

	// c1:f9:e5:e7:d6:ed:5e:7c:1b:07:d2:eb:aa:68:e1:
	// 00:85
	public static byte[] OTC_PUBLIC_KEY = { (byte) 0x85, 0x00, (byte) 0xe1, 0x68, (byte) 0xaa, (byte) 0xeb, (byte) 0xd2,
			0x07, 0x1b, 0x7c };

	// 96:d6:6d:af:b5:0d:71:b6:03:90:ae:d4:64:74:58:
	// f4:1f
	public static byte[] CAVS_PUBLIC_APL_KEY = { 0x1f, (byte) 0xf4, 0x58, 0x74, 0x64, (byte) 0xd4, (byte) 0xae,
			(byte) 0x90, 0x03, (byte) 0xb6 };

	// f4:25:1d:bc:7d:a7:c2:97:89:72:29:7e:1e:14:3e:
	// a0:41
	public static byte[] CAVS_PUBLIC_CNL_KEY = { 0x41, (byte) 0xa0, 0x3e, 0x14, 0x1e, 0x7e, 0x29, 0x72, (byte) 0x89,
			(byte) 0x97 };

	class KeyEntry {
		byte[] keyBegin;
		String keyName;

		KeyEntry(byte[] keyBegin, String keyName) {
			this.keyBegin = keyBegin;
			this.keyName = keyName;
		}
	}

	private static SimpleFwKeyResolver instance = null;

	private SimpleFwKeyResolver() {
	}

	private KeyEntry[] keys = { new KeyEntry(OTC_PUBLIC_KEY, "Intel OTC key"),
			new KeyEntry(CAVS_PUBLIC_APL_KEY, "Intel APL production key"),
			new KeyEntry(CAVS_PUBLIC_CNL_KEY, "Intel CNL production key") };

	private static SimpleFwKeyResolver getInstance() {
		if (instance == null)
			instance = new SimpleFwKeyResolver();
		return instance;
	}

	public static String searchModulusName(byte[] modulus) {
		byte[] mod10 = Arrays.copyOf(modulus, 10);
		for (KeyEntry key : getInstance().keys) {
			if (Arrays.equals(key.keyBegin, mod10))
				return key.keyName;
		}
		return getFrontHex(modulus);
	}

	private static String getFrontHex(byte[] modulus) {
		int i = 0;
		StringBuffer sb = new StringBuffer("[");
		for (byte b : modulus) {
			sb.append(String.format("%02x ", b));
			if (++i == 10) {
				sb.append("...");
				break;
			}
		}
		sb.append("]");
		return sb.toString();
	}
}
