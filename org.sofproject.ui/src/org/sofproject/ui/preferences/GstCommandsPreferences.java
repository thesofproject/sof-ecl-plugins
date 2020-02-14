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

package org.sofproject.ui.preferences;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.sofproject.gst.topo.IGstNodeConst;

public class GstCommandsPreferences extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	private StringFieldEditor gstInspectfieldEditor;
	private StringFieldEditor gstLaunchfieldEditor;

	public GstCommandsPreferences() {
		super(GRID);
		setDescription("Set custom GStreamer commands");
	}

	@Override
	public boolean performOk() {
		if (gstLaunchfieldEditor.getStringValue().equals("")) {
			setErrorMessage("gst-launch command cannot be empty");
			return false;
		}

		if (gstInspectfieldEditor.getStringValue().equals("")) {
			setErrorMessage("gst-inspect command cannot be empty");
			return false;
		}
		return super.performOk();
	}

	@Override
	public void init(IWorkbench workbench) {
		IPreferenceStore store = new ScopedPreferenceStore(InstanceScope.INSTANCE,
				IGstNodeConst.GST_NODE_PREFERENCES_ID);

		store.setDefault(IGstNodeConst.GST_INSPECT_PREF_NAME, IGstNodeConst.GST_PROJ_DEFAULT_GST_INSPECT_TOOL_CMD);
		store.setDefault(IGstNodeConst.GST_LAUNCH_PREF_NAME, IGstNodeConst.GST_PROJ_DEFAULT_GST_LAUNCH_TOOL_CMD);

		setPreferenceStore(store);
	}

	@Override
	protected void createFieldEditors() {
		gstInspectfieldEditor = new StringFieldEditor(IGstNodeConst.GST_INSPECT_PREF_NAME, "Gst-inspect command: ",
				getFieldEditorParent());
		addField(gstInspectfieldEditor);

		gstLaunchfieldEditor = new StringFieldEditor(IGstNodeConst.GST_LAUNCH_PREF_NAME, "Gst-launch command: ",
				getFieldEditorParent());
		addField(gstLaunchfieldEditor);
	}
}
