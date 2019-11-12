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

package org.sofproject.fw;

public interface ISofNodeConst {

	/**
	 * ID of the 'sof-node' project.
	 */
	String SOF_NODE_CORE_ID = "org.sofproject.fw";

	/**
	 * ID of the 'sof-node' project natue.
	 */
	String SOF_NODE_EXTENSION_ID = SOF_NODE_CORE_ID + ".sofextension";

	/**
	 * Default name for local project directory to keep a copy of fw binary files.
	 */
	String BIN_FOLDER = "fw-bin";

	/**
	 * Default name for local project directory to keep a copy of topology binary
	 * files.
	 */
	String TPLG_FOLDER = "topology";

	/**
	 * Extension used to distinguish remote fw binary files.
	 */
	String FW_BIN_FILE_EXT = "bin";

	/**
	 * Extension used to distinguish remote topology binary files.
	 */
	String TPLG_FILE_EXT = "tplg";

	/**
	 * Custom project property to store path to the remote files.
	 */
	String SOF_PROJ_PROP_REMOTE_RES_PATH = "remoteResPath";

	/**
	 * Default path to the remote files.
	 */
	String SOF_PROJ_DEFAULT_REMOTE_PATH = "/lib/firmware/intel";

	/**
	 * Custom project property to store name of the project with source code.
	 */
	String SOF_PROJ_PROP_SRC_PROJ_NAME = "srcProjName";

	/**
	 * Default name of the project with source code.
	 */
	String SOF_PROJ_DEFAULT_SRC_PROJ = "sof";
}
