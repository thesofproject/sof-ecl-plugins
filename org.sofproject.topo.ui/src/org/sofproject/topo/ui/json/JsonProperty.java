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

package org.sofproject.topo.ui.json;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize
public class JsonProperty {

	private String name;
	private String description;
	private String version;
	private String type;
	private String template;
	private Parameters parameters;

	public JsonProperty(String name, String description, String version, String type) {
		this.name = name;
		this.description = description;
		this.version = version;
		this.type = type;

		// set default parameters
		this.parameters = new Parameters("object", new Properties());
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public String getVersion() {
		return version;
	}

	public String getType() {
		return type;
	}

	public String getTemplate() {
		return template;
	}

	public Parameters getParameters() {
		return parameters;
	}

	public void setTemplate(String newTemplate) {
		this.template = newTemplate;
	}

	public void setParameters(Parameters parameters) {
		this.parameters = parameters;
	}
}

@JsonSerialize
class Parameters {
	private String type;
	private Properties properties;

	public Parameters(String type, Properties properties) {
		this.type = type;
		this.properties = properties;
	}

	public String getType() {
		return type;
	}

	public Properties getProperties() {
		return properties;
	}
}

@JsonSerialize
class Properties {
	public Properties() {
	}
}
