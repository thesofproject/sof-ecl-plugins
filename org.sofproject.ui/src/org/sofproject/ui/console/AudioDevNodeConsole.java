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

package org.sofproject.ui.console;

import org.eclipse.jface.resource.FontDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;
import org.sofproject.core.AudioDevNodeProject;

public class AudioDevNodeConsole extends MessageConsole {
	public static final String TYPE = "audiodevnodeconsole";
	public static final String TYPE_DMESG = TYPE + ".dmesg";
	public static final String CONSOLE_NAME = "AudioDevNodeConsole";

	AudioDevNodeProject audioDevNodeProj; // parent project

	public AudioDevNodeConsole(String name, String type, AudioDevNodeProject audioDevNodeProj) {
		super(name, type, null /*SofResources.getSofIcon()*/, true);
		this.audioDevNodeProj = audioDevNodeProj;
		FontDescriptor fd = JFaceResources.getTextFontDescriptor().increaseHeight(-1);
		setFont(fd.createFont(getFont().getDevice()));
	}

	public AudioDevNodeProject getAudioDevNodeProj() {
		return audioDevNodeProj;
	}

	private static MessageConsole getConsole(String name, String type, AudioDevNodeProject audioDevNodeProj) {
		IConsoleManager cm = ConsolePlugin.getDefault().getConsoleManager();
		for (IConsole c : cm.getConsoles()) {
			if (name.equals(c.getName()))
				return (MessageConsole) c;
		}
		// no one found
		MessageConsole nc = new AudioDevNodeConsole(name, type, audioDevNodeProj);
		cm.addConsoles(new IConsole[] { nc });
		cm.showConsoleView(nc);
		return nc;
	}

	public static MessageConsoleStream getDefaultConsoleStream(AudioDevNodeProject audioDevNodeProj, String type) {
		return getConsole(CONSOLE_NAME, type, audioDevNodeProj).newMessageStream();
	}

	public static MessageConsoleStream getConsoleStream(String id, String type, AudioDevNodeProject audioDevNodeProj) {
		return getConsole(CONSOLE_NAME + "." + id, type, audioDevNodeProj).newMessageStream();
	}
}
