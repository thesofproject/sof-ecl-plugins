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

package org.sofproject.gst.topo.ops;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.eclipse.core.runtime.IProgressMonitor;
import org.sofproject.core.AudioDevNodeProject;
import org.sofproject.core.connection.AudioDevNodeConnection;
import org.sofproject.core.ops.SimpleRemoteOp;
import org.sofproject.topo.ui.json.JsonProperty;
import org.sofproject.topo.ui.json.JsonUtils;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

public class GstDockerOperation extends SimpleRemoteOp {

	private JsonUtils jsonUtils;

	public GstDockerOperation(AudioDevNodeConnection conn, JsonUtils jsonUtils) {
		super(conn);
		this.jsonUtils = jsonUtils;
	}

	@Override
	public boolean isCancelable() {
		return true;
	}

	@Override
	public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
		monitor.beginTask("Sending json to docker", 100);

		try {
			if (!conn.isConnected()) {
				throw new InvocationTargetException(new IllegalStateException("Node not connected"));
			}

			JsonProperty jsonProperty = jsonUtils.getJsonProperty();

			AudioDevNodeProject proj = conn.getProject();
			Session session = conn.getSession();
			ChannelExec channel = (ChannelExec) session.openChannel("exec");
			channel.setInputStream(null);

			String jsonFileName = "pipeline.json";
			String projectPath = proj.getProject().getLocation().toString();
			Path partPathToJson = Paths.get(jsonProperty.getType().toLowerCase(), jsonProperty.getName(), jsonProperty.getVersion());
			Path fullPathToJson = Paths.get(projectPath, partPathToJson.toString(), jsonFileName);
			String linuxPartPathToJson = partPathToJson.toString();
			linuxPartPathToJson = linuxPartPathToJson.replace("\\", "/");

			BufferedReader reader = new BufferedReader(new FileReader(fullPathToJson.toString()));
			String currentLine = reader.readLine();
			reader.close();

			channel.setPty(true); // for ctrl+c sending
			String command = String.format(
					"docker exec -t video_analytics_serving_gstreamer /bin/bash -c \"cd pipelines; mkdir -p %s; cd %s; touch %s; echo \'%s\' > %s\"",
					linuxPartPathToJson, linuxPartPathToJson, jsonFileName, currentLine.replace("\"", "\\\""), jsonFileName);
			channel.setCommand(command);

			channel.connect();

			while (!channel.isEOF())
				;
			channel.disconnect();
			monitor.beginTask("Sending json to docker", 1000);

		} catch (JSchException | IOException e) {
			throw new InvocationTargetException(e);
		}
	}

}
