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

package org.sofproject.alsa.topo.ui.outline;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.sofproject.alsa.topo.conf.ConfItem;
import org.sofproject.alsa.topo.model.AlsaTopoGraph;
import org.sofproject.alsa.topo.model.AlsaTopoNode;
import org.sofproject.alsa.topo.model.AlsaTopoNodeCollection;

public class AlsaTopoGraphContentProvider implements ITreeContentProvider {

	@Override
	public Object[] getElements(Object inputElement) {
		if (inputElement instanceof AlsaTopoGraph) {
			AlsaTopoGraph topo = (AlsaTopoGraph) inputElement;
			return topo.getSections().toArray();
		}
		return null;
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof AlsaTopoNodeCollection<?>) {
			AlsaTopoNodeCollection<?> col = (AlsaTopoNodeCollection<?>) parentElement;
			return col.getElements().toArray();
		} else if (parentElement instanceof AlsaTopoNode) {
			AlsaTopoNode node = (AlsaTopoNode) parentElement;
			return node.getConfItems().toArray();
		} else if (parentElement instanceof ConfItem) {
			ConfItem item = (ConfItem) parentElement;
			return item.getChildren().toArray();
		}
		return null;
	}

	@Override
	public Object getParent(Object element) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		if (element instanceof AlsaTopoNodeCollection<?>) {
			return !((AlsaTopoNodeCollection<?>) element).getElements().isEmpty();
		} else if (element instanceof AlsaTopoNode) {
			return !((AlsaTopoNode) element).getConfItems().isEmpty();
		} else if (element instanceof ConfItem) {
			return ((ConfItem) element).hasChildren();
		}
		return false;
	}
}
