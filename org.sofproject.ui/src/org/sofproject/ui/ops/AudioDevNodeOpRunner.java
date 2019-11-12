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

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.window.Window;
import org.sofproject.core.AudioDevNodeProject;
import org.sofproject.core.connection.AudioDevNodeConnection;
import org.sofproject.core.ops.IRemoteOp;
import org.sofproject.ui.handlers.AudioDevNodeLoginDialog;

public class AudioDevNodeOpRunner {

	public static void runOp(IRemoteOp op) {
		try {
			AudioDevNodeConnection conn = op.getConnection();
			AudioDevNodeProject proj = conn.getProject();

			if (!conn.isConnected()) {
				AudioDevNodeLoginDialog dlg = new AudioDevNodeLoginDialog(null, proj.getProject().getName(),
						proj.getAddress());
				if (dlg.open() == Window.OK) {
					conn.connect(dlg.getLogin(), dlg.getPass());
				} else {
					return;
				}
			}

			// TODO: should refresh the viewer if present and method is called
			// from outside

			if (!conn.isConnected()) {
				/* authentication failure ? */
				return;
			}
			new ProgressMonitorDialog(null).run(true, op.isCancelable(), new IRunnableWithProgress() {

				@Override
				public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
					op.run(monitor);
				}
			});
		} catch (CoreException | InvocationTargetException e) {
			MessageDialog.openError(null, "Operation failed: ", e.getMessage());
		} catch (InterruptedException e) {
			MessageDialog.openInformation(null, "Operation canceled", "Operation canceled");
		}

	}
}
