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

package org.sofproject.alsa.topo.ui.properties;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.sofproject.alsa.topo.model.AlsaTopoNode;
import org.sofproject.alsa.topo.ui.graph.AlsaTopoZestGraphBuilder;
import org.sofproject.alsa.topo.ui.graph.AlsaTopoZestGraphBuilder.BinFileNode;

import javafx.util.Pair;

public class AlsaTplgPropertySource implements IPropertySource {

	private AlsaTopoNode topoNode;
	private List<IPropertyDescriptor> properties;

	public AlsaTplgPropertySource(BinFileNode item) {
		properties = new ArrayList<IPropertyDescriptor>();
		topoNode = AlsaTopoZestGraphBuilder.getModelNode(item);
		for (String group : topoNode.getAttributeGroups()) {
			for (String attr : topoNode.getAttributeNamesFromGroup(group)) {
				PropertyDescriptor pd = new PropertyDescriptor(new Pair<String, String>(group, attr), attr);
				pd.setCategory(group);
				properties.add(pd);
			}
		}
	}

	@Override
	public Object getEditableValue() {
		return null;
	}

	@Override
	public IPropertyDescriptor[] getPropertyDescriptors() {
		return properties.toArray(new IPropertyDescriptor[0]);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object getPropertyValue(Object id) {
		Pair<String, String> ga = (Pair<String, String>) id;
		return topoNode.getAttribute(ga.getKey(), ga.getValue());
	}

	@Override
	public boolean isPropertySet(Object id) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void resetPropertyValue(Object id) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setPropertyValue(Object id, Object value) {
		// TODO Auto-generated method stub

	}
}
