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

package org.sofproject.fw.ui.editor;

import java.io.IOException;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.gef.graph.Graph;
import org.eclipse.gef.mvc.fx.ui.MvcFxUiModule;
import org.eclipse.gef.mvc.fx.ui.parts.AbstractFXEditor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.views.properties.IPropertySheetPage;
import org.sofproject.core.binfile.BinFile;
import org.sofproject.fw.model.FwBinFactory;
import org.sofproject.fw.model.FwBinGraph;
import org.sofproject.fw.ui.graph.FwBinZestGraphBuilder;
import org.sofproject.ui.editor.IBinFileEditor;
import org.sofproject.ui.properties.SofPropertySheetPage;

import com.google.inject.Guice;
import com.google.inject.util.Modules;

public class FwBinEditor extends AbstractFXEditor implements IBinFileEditor {

	private FwBinGraph fwBinModel = null;
	private Graph zestGraph;

	public FwBinEditor() {
		super(Guice.createInjector(Modules.override(new FwBinModule()).with(new MvcFxUiModule())));

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
	public boolean isDirty() {
		// TODO: graph manipulations ignored at the moment
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

		setPartName("Fw Binary " + input.getName());

		// TODO: should be run as runnable with progress, as non-blocking operation
		if (input instanceof IFileEditorInput) {
			IFileEditorInput fileInput = (IFileEditorInput) input;
			try {
				// get IFile for mem map - might not be available
				IContainer parentFolder = fileInput.getFile().getParent();
				String binFileName = fileInput.getName();
				String mmFileName = binFileName.substring(0, binFileName.lastIndexOf('.'));
				IFile mmFile = parentFolder.getFile(new Path(mmFileName + ".lmap"));

				fwBinModel = FwBinFactory.readBinary(fileInput.getName(), fileInput.getFile().getContents().available(),
						fileInput.getFile().getContents(),
						mmFile.exists() ? mmFile.getName() : null,
						mmFile.exists() ? mmFile.getContents() : null);
			} catch (CoreException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		if (fwBinModel != null) {
			zestGraph = new FwBinZestGraphBuilder().build(fwBinModel);
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

	@Override
	public BinFile getBinFile() {
		return fwBinModel.getBinFile();
	}

}
