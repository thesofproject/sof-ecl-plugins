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

package org.sofproject.fw.ui.ops;

import java.io.OutputStream;

import org.sofproject.core.connection.AudioDevNodeConnection;
import org.sofproject.core.ops.IRemoteOp;
import org.sofproject.core.ops.IRemoteOpsProvider;
import org.sofproject.fw.ui.views.SofOpLoggerStream;
import org.sofproject.core.ops.AudioDevSshRunCmdOperation;

public class SofRemoteOpsProvider implements IRemoteOpsProvider {

	public static final String OPEN_LOGGER_OP = "org.sofproject.ui.ops.openlogger";
	public static final String IMPORT_SOF_FILES_OP = "org.sofproject.ui.ops.importsoffiles";

	public static final String[] OPS = { OPEN_LOGGER_OP, IMPORT_SOF_FILES_OP };

	@Override
	public String[] getRemoteOpsIds() {
		return OPS;
	}

	@Override
	public String getRemoteOpDisplayName(String opId) {
		switch (opId) {
		case OPEN_LOGGER_OP:
			return "Open SOF logger";
		case IMPORT_SOF_FILES_OP:
			return "Import SOF files";
		default:
			return null;
		}
	}

	@Override
	public IRemoteOp createRemoteOp(String opId, AudioDevNodeConnection conn) {
		switch (opId) {
		case OPEN_LOGGER_OP:
			OutputStream os = SofOpLoggerStream.create(conn);
			return new AudioDevSshRunCmdOperation(conn, opId, "./run-logger.sh", os);
		case IMPORT_SOF_FILES_OP:
			return new SofSshImportOperation(conn);
		default:
			return null;
		}
	}

}
