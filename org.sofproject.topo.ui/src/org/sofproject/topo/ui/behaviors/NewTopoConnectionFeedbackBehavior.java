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

package org.sofproject.topo.ui.behaviors;

import org.eclipse.gef.mvc.fx.behaviors.AbstractBehavior;
import org.eclipse.gef.mvc.fx.parts.IFeedbackPartFactory;
import org.eclipse.gef.mvc.fx.viewer.IViewer;
import org.sofproject.topo.ui.models.TopoItemCreationModel;
import org.sofproject.topo.ui.parts.TopoNodePart;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

public class NewTopoConnectionFeedbackBehavior extends AbstractBehavior {

	public static final String NEW_TOPO_CONNECTION_FEEDBACK_PART_FACTORY = "NEW_TOPO_CONNECTION_FEEDBACK_PART_FACTORY";

	@Override
	protected IFeedbackPartFactory getFeedbackPartFactory(IViewer viewer) {
		return getFeedbackPartFactory(viewer, NEW_TOPO_CONNECTION_FEEDBACK_PART_FACTORY);
	}

	@Override
	protected void doActivate() {
		TopoItemCreationModel model = getHost().getRoot().getViewer().getAdapter(TopoItemCreationModel.class);
		model.getSourceProperty().addListener(new ChangeListener<TopoNodePart>() {
			@Override
			public void changed(ObservableValue<? extends TopoNodePart> o, TopoNodePart oldVal, TopoNodePart newVal) {
				if (newVal != null) {
					addFeedback(newVal);
				} else {
					clearFeedback();
				}
			}
		});
		super.doActivate();
	}
}
