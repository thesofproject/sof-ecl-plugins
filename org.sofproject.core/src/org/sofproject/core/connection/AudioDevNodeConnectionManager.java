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

package org.sofproject.core.connection;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.sofproject.core.AudioDevNodeProject;
import org.sofproject.core.IAudioDevNodeConst;

public class AudioDevNodeConnectionManager extends Observable {

	private static AudioDevNodeConnectionManager fInstance;

	private Map<AudioDevNodeProject, AudioDevNodeConnection> connections = new HashMap<>();

	private AudioDevNodeConnectionManager() {
		collectProjects();
	}

	private void collectProjects() {
		try {
			IWorkspace workspace = ResourcesPlugin.getWorkspace();
			for (IProject project : workspace.getRoot().getProjects()) {
				if (!project.hasNature(IAudioDevNodeConst.AUDIO_DEV_NODE_NATURE_ID))
					continue;
				AudioDevNodeProject sofNodeProject = new AudioDevNodeProject(project);
				if (sofNodeProject != null)
					createConnection(sofNodeProject);
			}
			workspace.addResourceChangeListener(new IResourceChangeListener() {

				@Override
				public void resourceChanged(IResourceChangeEvent event) {
					if (event.getResource() instanceof IProject) {
						IProject project = (IProject) event.getResource();
						for (AudioDevNodeProject sofNodeProject : connections.keySet()) {
							if (sofNodeProject.getProject() == project) {
								deleteConnection(sofNodeProject);
								return;
							}
						}
					}

				}
			}, IResourceChangeEvent.PRE_CLOSE | IResourceChangeEvent.PRE_DELETE);
		} catch (CoreException e) {
			// TODO:
		}
	}

	public static AudioDevNodeConnectionManager getInstance() {
		if (fInstance == null)
			fInstance = new AudioDevNodeConnectionManager();
		return fInstance;
	}

	public AudioDevNodeConnection createConnection(AudioDevNodeProject project) {
		AudioDevNodeConnection conn = connections.get(project);
		if (conn == null) {
			conn = new AudioDevNodeConnection(project);
			connections.put(project, conn);
		}
		setChanged();
		notifyObservers();
		return conn;
	}

	public void deleteConnection(AudioDevNodeProject project) {
		AudioDevNodeConnection conn = connections.get(project);
		if (conn == null)
			return;
		connections.remove(project);
		setChanged();
		notifyObservers(null);
		conn.close();
	}

	public Collection<AudioDevNodeConnection> getConnections() {
		return connections.values();
	}

	public AudioDevNodeConnection getConnection(IProject project) {
		for (AudioDevNodeProject snProj : connections.keySet()) {
			if (snProj.getProject() == project) {
				return connections.get(snProj);
			}
		}
		return null;
	}

	public void shutdown() {
		// TODO: disconnect all active connections
	}

}
