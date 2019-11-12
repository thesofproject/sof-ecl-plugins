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

package org.sofproject.fw.ui.resources;

import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.resource.ImageDescriptor;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

import javafx.scene.paint.Color;

/**
 * Colors used by other SOF resources (the documentation for example).
 */
public class SofResources {
	public static final Color SOF_GREY = Color.rgb(214, 214, 222);
	public static final Color SOF_LIGHT_GREY = Color.rgb(235, 235, 235);
	public static final Color SOF_YELLOW = Color.rgb(246, 237, 128);
	public static final Color SOF_RED = Color.rgb(240, 87, 114);
	public static final Color SOF_LIGHT_RED = Color.rgb(250, 199, 207);

	/**
	 * Used to fill rectangles/blocks.
	 */
	public static final Color SOF_BLUE = Color.rgb(111, 204, 221);

	/**
	 * Lighter blue.
	 */
	public static final Color SOF_LIGHT_BLUE = Color.rgb(183, 229, 238);

	/**
	 * Darker blue for rectangle/block borders.
	 */
	public static final Color SOF_DARK_BLUE = Color.rgb(80, 149, 162);

	/**
	 * Used for main lines and labels.
	 */
	public static final Color SOF_DARK_VIOLET = Color.rgb(51, 51, 91);

	public static final Color SOF_GREEN = Color.rgb(146, 208, 80);

	public static ImageDescriptor getSofIcon() {
		Bundle bundle = FrameworkUtil.getBundle(SofResources.class);
		URL url = FileLocator.find(bundle, new Path("icons/sof-icon.png"));
		return ImageDescriptor.createFromURL(url);
	}
}
