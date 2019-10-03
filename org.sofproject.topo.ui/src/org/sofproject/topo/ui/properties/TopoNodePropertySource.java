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

package org.sofproject.topo.ui.properties;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.views.properties.ComboBoxPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.eclipse.ui.views.properties.PropertySheet;
import org.eclipse.ui.views.properties.PropertySheetPage;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;
import org.sofproject.topo.ui.graph.GefTopoNode;
import org.sofproject.topo.ui.graph.ITopoElement;
import org.sofproject.topo.ui.graph.ITopoNode;
import org.sofproject.topo.ui.graph.ITopoNodeAttribute;
import org.sofproject.topo.ui.graph.ITopoNodeAttribute.Type;

public class TopoNodePropertySource implements IPropertySource, PropertyChangeListener {

	private GefTopoNode item;
	private List<IPropertyDescriptor> properties;
	boolean changed = false;

	public TopoNodePropertySource(GefTopoNode item) {
		this.item = item;
		item.addPropertyChangeListener(this);
		properties = new ArrayList<IPropertyDescriptor>();
		buildProperties();
	}

	private void buildProperties() {
		ITopoNode modelNode = item.getTopoModelNode();
		addAttributes(modelNode.getAttributes());
		for (ITopoElement element : modelNode.getChildElements()) {
			addAttributes(element.getAttributes());
		}
	}

	private void addAttributes(Collection<? extends ITopoNodeAttribute> attributes) {
		for (ITopoNodeAttribute attr : attributes) {
			PropertyDescriptor pd = null;
			if (attr.isReadOnly()) {
				pd = new PropertyDescriptor(attr, String.format("[%s]", attr.getName()));
			} else {
				switch (attr.getNodeAtrributeType()) {
				case NODE_A_STRING:
				case NODE_A_INTEGER:
					pd = new TextPropertyDescriptor(attr, attr.getName());
					break;
				case NODE_A_BOOLEAN:
					String[] values = { "false", "true" };
					pd = new ComboBoxPropertyDescriptor(attr, attr.getName(),
							values);
					break;
				// TODO: other types
				default:
					pd = new PropertyDescriptor(attr, attr.getName());
					break;
				}
			}
			if (pd != null) {
				pd.setCategory(attr.getCategory());
				properties.add(pd);
			}
		}
	}

	@Override
	public Object getEditableValue() {
		return this;
	}

	@Override
	public IPropertyDescriptor[] getPropertyDescriptors() {
		if (changed) {
			properties.clear();
			buildProperties();
			changed = false;
		}
		return properties.toArray(new IPropertyDescriptor[0]);
	}

	@Override
	public Object getPropertyValue(Object id) {
		if (id instanceof ITopoNodeAttribute) {
			ITopoNodeAttribute attr = (ITopoNodeAttribute) id;
			if (attr.getNodeAtrributeType() == Type.NODE_A_BOOLEAN) {
				if (attr.isReadOnly()) {
					return (Boolean) attr.getValue() ? "true" : "false";
				} else {
					return (Boolean)attr.getValue() ? 1 : 0;
				}
			} else {
				return attr.getStringValue();
			}
		}
		return null;
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
		// TODO: should go through some specialized validation method
		if (id instanceof ITopoNodeAttribute) {
			((ITopoNodeAttribute) id).setValue(value);
			// TODO: refresh outline via notification
		}
	}

	@Override
	public void propertyChange(PropertyChangeEvent arg0) {
		changed = true;
		String id = "org.eclipse.ui.views.PropertySheet";
		IViewPart view = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(id);
		if (view instanceof PropertySheet) {
			PropertySheet propertySheet = (PropertySheet) view;
			((PropertySheetPage) propertySheet.getCurrentPage()).refresh();
		}
	}
}
