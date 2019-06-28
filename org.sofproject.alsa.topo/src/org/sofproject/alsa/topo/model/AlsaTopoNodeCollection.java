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

package org.sofproject.alsa.topo.model;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import org.sofproject.alsa.topo.conf.ConfElement;

/**
 * TODO: remove dummy ConfElement assigned as there is no real conf item below,
 * once another abstraction level is introduced.
 *
 * @param <T>
 */
public class AlsaTopoNodeCollection<T extends AlsaTopoNode> extends AlsaTopoNode {

	private String name;
	private Map<String, T> elements = new LinkedHashMap<>();

	public AlsaTopoNodeCollection(String name) {
		super(new ConfElement(name, null));
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}

	public void add(T element) {
		elements.put(element.getName(), element);
	}

	public T get(String name) {
		return elements.get(name);
	}

	public Collection<T> getElements() {
		return elements.values();
	}

	public int size() {
		return elements.size();
	}

	@Override
	public String toString() {
		return name;
	}
}
