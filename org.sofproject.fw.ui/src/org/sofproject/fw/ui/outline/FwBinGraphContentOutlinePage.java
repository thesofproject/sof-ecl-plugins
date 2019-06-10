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
package org.sofproject.fw.ui.outline;

import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider.IStyledLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.contentoutline.ContentOutlinePage;
import org.sofproject.fw.memmap.DspMemoryRegion;
import org.sofproject.fw.model.FwBinGraph;
import org.sofproject.fw.ui.editor.FwBinEditor;

public class FwBinGraphContentOutlinePage extends ContentOutlinePage {

	private FwBinGraph fwBinModel;

	public FwBinGraphContentOutlinePage(FwBinEditor editor) {
		fwBinModel = editor.getFwBinModel();
	}

	@Override
	public void createControl(Composite parent) {
		super.createControl(parent);
		TreeViewer viewer = getTreeViewer();
		viewer.setContentProvider(new FwBinGraphContentProvider());
		viewer.setLabelProvider(new DelegatingStyledCellLabelProvider(new FwBinGraphLabelProvider()));
		viewer.addSelectionChangedListener(this);
		viewer.setInput(fwBinModel.getMemoryMap());
	}

	/**
	 * Two objects here: segmment (either from binary or created like 'Others'), mem
	 * section (direct obj from mem map)
	 */
	class FwBinGraphLabelProvider extends LabelProvider implements IStyledLabelProvider {

		@Override
		public StyledString getStyledText(Object element) {
			if (element instanceof DspMemoryRegion) {
				DspMemoryRegion reg = (DspMemoryRegion) element;
				String baseAddrHex = String.format("%08x", reg.getBaseAddr());
				StyledString s = new StyledString(baseAddrHex, StyledString.DECORATIONS_STYLER);
				s.append(" ").append(reg.getName());

				// bytes and used bytes
				if (reg.getUsedBytes() > 0 || reg.getSizeBytes() > 0) {
					StringBuffer sb = new StringBuffer();
					if (reg.getUsedBytes() > 0)
						sb.append(reg.getUsedBytes());
					if (!reg.getHideFree() && reg.getSizeBytes() > 0) {
						int delta = reg.getSizeBytes() - reg.getUsedBytes();
						if (reg.getUsedBytes() > 0)
							sb.append(delta >= 0 ? "+" : "-");
						sb.append(delta);
					}

					s.append(" (" + sb.toString() + ")", StyledString.COUNTER_STYLER);
				}
				return s;
			}
			return new StyledString(element.toString());
		}

	}
}
