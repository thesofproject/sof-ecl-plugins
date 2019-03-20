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
package org.sofproject.ui.console;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.debug.ui.console.FileLink;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.ui.console.IPatternMatchListenerDelegate;
import org.eclipse.ui.console.PatternMatchEvent;
import org.eclipse.ui.console.TextConsole;

public class LogSourceMatcher implements IPatternMatchListenerDelegate {

	private SofConsole console;

	IProject sofSrcProject;

	@Override
	public void connect(TextConsole console) {
//		System.out.println("LogSourceMatcher: connecting to console " + console.getName());
		this.console = (SofConsole) console;

		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IWorkspaceRoot root = workspace.getRoot();

		sofSrcProject = root.getProject(this.console.getSofNodeProj().getSrcProjName());
	}

	@Override
	public void disconnect() {
		this.console = null;
	}

	@Override
	public void matchFound(PatternMatchEvent event) {
		if (event.getLength() == 0)
			return;
		try {

			String line = console.getDocument().get(event.getOffset(), event.getLength());
			int locBeginPos = line.lastIndexOf('(');
			int locEndPos = line.lastIndexOf(')');
			if (locBeginPos > -1 && locEndPos > -1 && locEndPos > locBeginPos) {
				locBeginPos++;
				String locStr = line.substring(locBeginPos, locEndPos);
				String[] locTok = locStr.split(":");
				String fileName = locTok[0];
				int lineNo = Integer.parseInt(locTok[1]);

				IFile file = sofSrcProject.getFile(fileName);
				FileLink fileLink = new FileLink(file, null, -1, -1, lineNo);
				console.addHyperlink(fileLink, event.getOffset() + locBeginPos, locEndPos - locBeginPos);
			}
		} catch (BadLocationException e) {
		}
	}

}
