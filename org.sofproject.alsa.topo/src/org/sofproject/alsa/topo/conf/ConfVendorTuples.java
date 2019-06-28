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

/**
 * Reference to tokens followed by a one or more arrays of tuples. Tuple arrays
 * are embedded and identified by the name that specifies type of the content.
 */
public class ConfVendorTuples extends ConfElement {

	public static final String TOKENS = "tokens";
	public static final String TUPLES = "tuples";
	public static final String STRING_NAME = "string";
	public static final String WORD_NAME = "word";
	public static final String SHORT_NAME = "short";
	public static final String BYTE_NAME = "byte";

	/**
	 *
	 * @param name Unique name
	 * @formatter:off
	 */
	public ConfVendorTuples(String name) {
		super(name, Arrays.asList(
				new ConfReference("tokens")));
	}

	/*
	 * (1) When re-building from binary:
	 *
	 * When tuple is added by integer id needs to find and assign vendor tokens.
	 * once tokens are assigned, tuples id'ed by value from another token array
	 * are rejected.
	 *
	 * (2) When adding from the editor:
	 *
	 * Another ctor needs to take the tokens array and accept tuples by string id.
	 *
	 * In any case still may contain tuples of different types as long as they
	 * all match a single token array.
	 */

	public void addString(int tokenId, String value) {
		ConfInteger token = findToken(tokenId);
		getTuples(STRING_NAME).addAttribute(new ConfString(token.getName(), value));
	}

	public void addWord(int tokenId, int value) {
		ConfInteger token = findToken(tokenId);
		getTuples(WORD_NAME).addAttribute(new ConfInteger(token.getName(), value));
	}

	public void addShort(int tokenId, int value) {
		ConfInteger token = findToken(tokenId);
		getTuples(SHORT_NAME).addAttribute(new ConfInteger(token.getName(), value));
	}

	public void addByte(int tokenId, int value) {
		ConfInteger token = findToken(tokenId);
		getTuples(BYTE_NAME).addAttribute(new ConfInteger(token.getName(), value));
	}

	// TODO: add() for other types...

	private ConfInteger findToken(int tokenId) {
		ConfReference tokens = (ConfReference) getAttribute(TOKENS);
		if (tokens == null)
			throw new RuntimeException("No tokens assigned");
		ConfVendorTokens vendorTokens = (ConfVendorTokens) tokens.getValue();
		ConfInteger token = vendorTokens.find(tokenId);
		if (token == null)
			throw new RuntimeException("No token with id " + tokenId + " found");
		return token;
	}

	private ConfElement getTuples(String typeName) {
		ConfElement tuples = getChildElement(TUPLES, typeName);
		if (tuples == null) {
			tuples = new ConfElement(typeName, null);
			tuples.setSectionName(TUPLES);
			addChild(tuples);
		}
		return tuples;
	}

}
