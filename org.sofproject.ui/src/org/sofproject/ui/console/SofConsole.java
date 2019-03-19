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

import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;
import org.sofproject.core.SofNodeProject;

public class SofConsole extends MessageConsole {
	public static final String TYPE = "sofconsole";
	public static final String TYPE_DMESG = TYPE + ".dmesg";
	public static final String TYPE_LOG = TYPE + ".log";
	public static final String CONSOLE_NAME = "SofConsole";

	SofNodeProject sofNodeProj; // parent project

	public SofConsole(String name, String type, SofNodeProject sofNodeProj) {
		super(name, type, null, true);
		this.sofNodeProj = sofNodeProj;
	}

	public SofNodeProject getSofNodeProj() {
		return sofNodeProj;
	}

	private static MessageConsole getConsole(String name, String type, SofNodeProject sofNodeProj) {
		IConsoleManager cm = ConsolePlugin.getDefault().getConsoleManager();
		for (IConsole c : cm.getConsoles()) {
			if (name.equals(c.getName()))
				return (MessageConsole) c;
		}
		// no one found
		MessageConsole nc = new SofConsole(name, type, sofNodeProj);
		cm.addConsoles(new IConsole[] { nc });
		return nc;
	}

	public static MessageConsoleStream getDefaultConsoleStream(SofNodeProject sofNodeProj, String type) {
		return getConsole(CONSOLE_NAME, type, sofNodeProj).newMessageStream();
	}

	public static MessageConsoleStream getConsoleStream(String id, String type, SofNodeProject sofNodeProj) {
		return getConsole(CONSOLE_NAME + "." + id, type, sofNodeProj).newMessageStream();
	}
}
