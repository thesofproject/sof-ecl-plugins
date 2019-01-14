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

package org.sofproject.alsa.topo.ui.editor;

import java.io.IOException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.gef.graph.Graph;
import org.eclipse.gef.mvc.fx.ui.MvcFxUiModule;
import org.eclipse.gef.mvc.fx.ui.actions.FitToViewportAction;
import org.eclipse.gef.mvc.fx.ui.actions.FitToViewportActionGroup;
import org.eclipse.gef.mvc.fx.ui.actions.ScrollActionGroup;
import org.eclipse.gef.mvc.fx.ui.actions.ZoomActionGroup;
import org.eclipse.gef.mvc.fx.ui.parts.AbstractFXEditor;
import org.eclipse.gef.mvc.fx.viewer.IViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.views.properties.IPropertySheetPage;
import org.sofproject.alsa.topo.model.AlsaTopoFactory;
import org.sofproject.alsa.topo.model.AlsaTopoGraph;
import org.sofproject.alsa.topo.ui.graph.AlsaTopoZestGraphBuilder;
import org.sofproject.core.binfile.BinFile;
import org.sofproject.ui.editor.IBinFileEditor;
import org.sofproject.ui.properties.SofPropertySheetPage;

import com.google.inject.Guice;
import com.google.inject.util.Modules;

public class AlsaTopoEditor extends AbstractFXEditor implements IBinFileEditor {

	private ZoomActionGroup zoomAg;
	private FitToViewportActionGroup fitToViewportAg;
	private ScrollActionGroup scrollAg;

	private AlsaTopoGraph topoModel = null;
	private Graph zestGraph;

	public AlsaTopoEditor() {
		super(Guice
				.createInjector(Modules.override(new AlsaTopoModule()).with(new MvcFxUiModule())));
	}

	@Override
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);

		IViewer viewer = getContentViewer();
		IActionBars actionBars = getEditorSite().getActionBars();
		
		zoomAg = new ZoomActionGroup(new FitToViewportAction());
		viewer.setAdapter(zoomAg);
		zoomAg.fillActionBars(actionBars);
		
		fitToViewportAg = new FitToViewportActionGroup();
		viewer.setAdapter(fitToViewportAg);
		fitToViewportAg.fillActionBars(actionBars);
		
		scrollAg = new ScrollActionGroup();
		viewer.setAdapter(scrollAg);
		scrollAg.fillActionBars(actionBars);

		// IToolBarManager mgr = actionBars.getToolBarManager();
		// mgr.add(new Separator());
		// mgr.add(new Separator());
	}

	@Override
	public void dispose() {
		IViewer viewer = getContentViewer();
		if (zoomAg != null) {
			viewer.unsetAdapter(zoomAg);
			zoomAg.dispose();
			zoomAg = null;
		}
		if (scrollAg != null) {
			viewer.unsetAdapter(scrollAg);
			scrollAg.dispose();
			scrollAg = null;
		}
		if (fitToViewportAg != null) {
			viewer.unsetAdapter(fitToViewportAg);
			fitToViewportAg.dispose();
			fitToViewportAg = null;
		}
		super.dispose();
	}

	@Override
	public boolean isDirty() {
		// TODO: graph manipulations ignored at the moment
		return false;
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		// TODO Auto-generated method stub

	}

	@Override
	public void doSaveAs() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isSaveAsAllowed() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void init(IEditorSite site, IEditorInput input) {
		try {
			super.init(site, input);
		} catch (PartInitException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		setPartName("Alsa topo " + input.getName());

		// TODO: should be run as runnable with progress, as non-blocking operation
		if (input instanceof IFileEditorInput) {
			IFileEditorInput fileInput = (IFileEditorInput) input;
			try {
				topoModel = AlsaTopoFactory.readTopo(fileInput.getName(),
						fileInput.getFile().getContents().available(),
						fileInput.getFile().getContents());
			} catch (CoreException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		if (topoModel != null) {
			zestGraph = new AlsaTopoZestGraphBuilder().build(topoModel);
		}
		getContentViewer().getContents().setAll(zestGraph);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Object getAdapter(final Class key) {
		if (key == IPropertySheetPage.class) {
			return new SofPropertySheetPage();
		}
		return super.getAdapter(key);
	}

	public AlsaTopoGraph getTopoModel() {
		return topoModel;
	}

	@Override
	public BinFile getBinFile() {
		return topoModel.getBinTopology();
	}
}
