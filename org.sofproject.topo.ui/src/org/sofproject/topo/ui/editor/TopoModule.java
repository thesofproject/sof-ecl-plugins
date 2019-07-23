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

package org.sofproject.topo.ui.editor;

import org.eclipse.gef.common.adapt.AdapterKey;
import org.eclipse.gef.mvc.fx.behaviors.ConnectionClickableAreaBehavior;
import org.eclipse.gef.mvc.fx.handlers.ConnectedSupport;
import org.eclipse.gef.mvc.fx.handlers.HoverOnHoverHandler;
import org.eclipse.gef.mvc.fx.parts.DefaultFocusFeedbackPartFactory;
import org.eclipse.gef.mvc.fx.parts.DefaultHoverFeedbackPartFactory;
import org.eclipse.gef.mvc.fx.parts.DefaultSelectionFeedbackPartFactory;
import org.eclipse.gef.mvc.fx.policies.TransformPolicy;
import org.eclipse.gef.mvc.fx.providers.GeometricOutlineProvider;
import org.eclipse.gef.mvc.fx.providers.ShapeBoundsProvider;
import org.eclipse.gef.mvc.fx.providers.ShapeOutlineProvider;
import org.eclipse.gef.zest.fx.ZestFxModule;
import org.eclipse.gef.zest.fx.behaviors.EdgeLayoutBehavior;
import org.eclipse.gef.zest.fx.behaviors.NodeLayoutBehavior;
import org.eclipse.gef.zest.fx.handlers.OpenNestedGraphOnDoubleClickHandler;
import org.eclipse.gef.zest.fx.handlers.TranslateSelectedAndRelocateLabelsOnDragHandler;
import org.eclipse.gef.zest.fx.providers.NodePartAnchorProvider;
import org.sofproject.topo.ui.behaviors.NewTopoConnectionFeedbackBehavior;
import org.sofproject.topo.ui.editor.policies.TopoEdgeOnClickHandler;
import org.sofproject.topo.ui.editor.policies.TopoEditorOnClickHandler;
import org.sofproject.topo.ui.editor.policies.TopoNodeOnClickHandler;
import org.sofproject.topo.ui.models.TopoItemCreationModel;
import org.sofproject.topo.ui.parts.TopoGraphAnchorProvider;
import org.sofproject.topo.ui.parts.TopoPartsFactory;
import org.sofproject.topo.ui.parts.feedback.NewTopoConnectionFeedbackPartFactory;

import com.google.inject.multibindings.MapBinder;

public class TopoModule extends ZestFxModule {

	@Override
	protected void bindIContentPartFactoryAsContentViewerAdapter(MapBinder<AdapterKey<?>, Object> amb) {
		amb.addBinding(AdapterKey.defaultRole()).to(TopoPartsFactory.class);
	}

	@Override
	protected void bindIViewerAdaptersForContentViewer(MapBinder<AdapterKey<?>, Object> amb) {
		super.bindIViewerAdaptersForContentViewer(amb);
		amb.addBinding(AdapterKey.defaultRole()).to(TopoItemCreationModel.class);
		amb.addBinding(AdapterKey.role(NewTopoConnectionFeedbackBehavior.NEW_TOPO_CONNECTION_FEEDBACK_PART_FACTORY)).to(NewTopoConnectionFeedbackPartFactory.class);
	}

	@Override
	protected void bindIRootPartAdaptersForContentViewer(MapBinder<AdapterKey<?>, Object> amb) {
		super.bindIRootPartAdaptersForContentViewer(amb);
		amb.addBinding(AdapterKey.defaultRole()).to(TopoEditorOnClickHandler.class);
		amb.addBinding(AdapterKey.defaultRole()).to(NewTopoConnectionFeedbackBehavior.class);
	}

	@Override
	protected void bindNodePartAdapters(MapBinder<AdapterKey<?>, Object> amb) {
		amb.addBinding(AdapterKey.defaultRole()).to(NodeLayoutBehavior.class);
		amb.addBinding(AdapterKey.defaultRole()).to(TranslateSelectedAndRelocateLabelsOnDragHandler.class);
		amb.addBinding(AdapterKey.role("open-nested-graph")).to(OpenNestedGraphOnDoubleClickHandler.class);
		amb.addBinding(AdapterKey.defaultRole()).to(TransformPolicy.class);
		amb.addBinding(AdapterKey.defaultRole()).to(NodePartAnchorProvider.class);
		amb.addBinding(AdapterKey.role(DefaultSelectionFeedbackPartFactory.SELECTION_FEEDBACK_GEOMETRY_PROVIDER))
				.to(ShapeBoundsProvider.class);
		amb.addBinding(AdapterKey.role(DefaultSelectionFeedbackPartFactory.SELECTION_LINK_FEEDBACK_GEOMETRY_PROVIDER))
				.to(ShapeBoundsProvider.class);
		amb.addBinding(AdapterKey.role(DefaultHoverFeedbackPartFactory.HOVER_FEEDBACK_GEOMETRY_PROVIDER))
				.to(ShapeBoundsProvider.class);
		amb.addBinding(AdapterKey.role(DefaultFocusFeedbackPartFactory.FOCUS_FEEDBACK_GEOMETRY_PROVIDER))
				.to(ShapeBoundsProvider.class);
		amb.addBinding(AdapterKey.defaultRole()).to(HoverOnHoverHandler.class);
		amb.addBinding(AdapterKey.defaultRole()).to(ConnectedSupport.class);

		// local add-ons
		amb.addBinding(AdapterKey.defaultRole()).to(TopoNodeOnClickHandler.class);
		amb.addBinding(AdapterKey.defaultRole()).to(TopoGraphAnchorProvider.class);
		amb.addBinding(AdapterKey.defaultRole()).to(ShapeOutlineProvider.class);
	}

	@Override
	protected void bindEdgePartAdapters(MapBinder<AdapterKey<?>, Object> amb) {
		amb.addBinding(AdapterKey.defaultRole()).to(EdgeLayoutBehavior.class);
		amb.addBinding(AdapterKey.role(DefaultSelectionFeedbackPartFactory.SELECTION_LINK_FEEDBACK_GEOMETRY_PROVIDER))
				.to(GeometricOutlineProvider.class);
		amb.addBinding(AdapterKey.role(DefaultSelectionFeedbackPartFactory.SELECTION_FEEDBACK_GEOMETRY_PROVIDER))
				.to(GeometricOutlineProvider.class);
		amb.addBinding(AdapterKey.role(DefaultHoverFeedbackPartFactory.HOVER_FEEDBACK_GEOMETRY_PROVIDER))
				.to(GeometricOutlineProvider.class);
		amb.addBinding(AdapterKey.role(DefaultFocusFeedbackPartFactory.FOCUS_FEEDBACK_GEOMETRY_PROVIDER))
				.to(GeometricOutlineProvider.class);
		amb.addBinding(AdapterKey.defaultRole()).to(ConnectionClickableAreaBehavior.class);
		amb.addBinding(AdapterKey.defaultRole()).to(TranslateSelectedAndRelocateLabelsOnDragHandler.class);
		amb.addBinding(AdapterKey.defaultRole()).to(HoverOnHoverHandler.class);

		// local add-ons
		amb.addBinding(AdapterKey.defaultRole()).to(TopoEdgeOnClickHandler.class);
	}

}
