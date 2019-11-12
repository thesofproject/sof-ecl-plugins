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

package org.sofproject.ui.wizards;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkingSet;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.sofproject.core.AudioDevNodeProject;
import org.sofproject.core.IAudioDevExtension;

public class NewAudioDevNodeWizard extends Wizard implements INewWizard {
	private NewAudioDevNodeCreationPage connPage;
	private AudioDevNodeProject audioDevNodeProject;

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		audioDevNodeProject = new AudioDevNodeProject(); // new project with defaults to init pages

		super.addPages();
		connPage = new NewAudioDevNodeCreationPage("NewConnection", audioDevNodeProject);
		connPage.setTitle("New Connection");
		connPage.setDescription("Create new connection to Audio Development node");
		connPage.setImageDescriptor(ImageDescriptor.createFromFile(getClass(), "/icons/audio-dev-icon.png"));
		addPage(connPage);
		connPage.init(selection);

		for (IConfigurationElement cfg : Platform.getExtensionRegistry()
				.getConfigurationElementsFor("org.sofproject.ui.newnodepageproviders")) {
			try {
				Object provider = cfg.createExecutableExtension("class");
				if (provider instanceof INewNodeExtensionPageProvider) {
					INewNodeExtensionPageProvider pageProvider = (INewNodeExtensionPageProvider) provider;
					IAudioDevExtension ext  = audioDevNodeProject.getExtension(pageProvider.getExtensionId());
					addPage(pageProvider.createNewPage(ext));
				}
			} catch (CoreException e) {
				e.printStackTrace();
			}
		}

	}

	@Override
	public boolean performFinish() {
		try {
			WorkspaceModifyOperation op = new WorkspaceModifyOperation() {

				@Override
				protected void execute(IProgressMonitor monitor)
						throws CoreException, InvocationTargetException, InterruptedException {
					createConnection(monitor != null ? monitor : new NullProgressMonitor());
				}
			};
			getContainer().run(false, true, op);
		} catch (InvocationTargetException x) {
			return false;
		} catch (InterruptedException x) {
			return false;
		}
		return true;
	}

	protected void createConnection(IProgressMonitor monitor) throws CoreException {
		audioDevNodeProject.setAddress(connPage.getAddress());
		// TODO: set port

		for (IWizardPage page : getPages()) {
			if (page instanceof INewNodeExtensionPage) {
				((INewNodeExtensionPage) page).commitSettings();
			}
		}

		IProject project = audioDevNodeProject.create(connPage.getProjectName(), connPage.getLocationPath(), monitor);
		IWorkingSet[] workingSets = connPage.getSelectedWorkingSets();
		PlatformUI.getWorkbench().getWorkingSetManager().addToWorkingSets(project, workingSets);
	}
}
