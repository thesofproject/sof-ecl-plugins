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

package org.sofproject.ui.ops;

import org.eclipse.ui.console.MessageConsoleStream;
import org.sofproject.core.connection.AudioDevNodeConnection;
import org.sofproject.core.ops.AudioDevSshRunCmdOperation;
import org.sofproject.core.ops.IRemoteOp;
import org.sofproject.core.ops.IRemoteOpsProvider;
import org.sofproject.ui.console.AudioDevNodeConsole;

public class AudioDevRemoteOpsProvider implements IRemoteOpsProvider {

	public static final String OPEN_DMESG_OP = "org.sofproject.ui.ops.opendmesg";

	public static final String[] OPS = { OPEN_DMESG_OP };

	@Override
	public String[] getRemoteOpsIds() {
		return OPS;
	}

	@Override
	public String getRemoteOpDisplayName(String opId) {
		switch (opId) {
		case OPEN_DMESG_OP:
			return "Open Dmesg";
		default:
			return null;
		}
	}

	@Override
	public IRemoteOp createRemoteOp(String opId, AudioDevNodeConnection conn) {
		switch (opId) {
		case OPEN_DMESG_OP:
			MessageConsoleStream mcs = AudioDevNodeConsole.getConsoleStream(
					conn.getProject().getProject().getName() + ".dmesg", AudioDevNodeConsole.TYPE_DMESG, conn.getProject());
			return new AudioDevSshRunCmdOperation(conn, opId, "dmesg -w", mcs);
		default:
			return null;
		}
	}

}
