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

package org.sofproject.gst.topo.plugins;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.sofproject.gst.topo.model.GstBoolean;
import org.sofproject.gst.topo.model.GstDouble;
import org.sofproject.gst.topo.model.GstInteger;
import org.sofproject.gst.topo.model.GstString;

public class GstPluginDb {

	public static final String DB_DIR = "gst-model";
	public static final int PROP_VALUE_OFFSET = 27;

	private Map<String, GstPlugin> plugins = new LinkedHashMap<>();

	private Map<String, GstElement> allElements = new LinkedHashMap<>();

	public Collection<GstPlugin> getPlugins() {
		return plugins.values();
	}

	public Collection<GstElement> getAllElements() {
		return allElements.values();
	}

	public GstElement getElement(String qName) {
		return allElements.get(qName);
	}

	public GstElement getElementByName(String name) {
		for (GstPlugin plg : plugins.values()) {
			GstElement elem = plg.getElement(name);
			if (elem != null)
				return elem;
		}
		return null; // not found
	}

	private void createFlatElementList() {
		for (GstPlugin plg : plugins.values()) {
			for (GstElement elem : plg.getElements()) {
				allElements.put(String.format("%s.%s", plg.getName(), elem.getName()), elem);
			}
		}
	}

	public void createFrom(IProject project, IProgressMonitor monitor) {
		try {
			IFolder dbDir = project.getFolder(DB_DIR);
			if (!dbDir.exists())
				return;

			monitor.beginTask("Reading plugins description", dbDir.members().length);

			for (IResource mbr : dbDir.members()) {
				if (mbr instanceof IFolder) {
					monitor.subTask("Reading plugin " + mbr.getName());
					addPluginFrom((IFolder) mbr);
					monitor.worked(1);
				}
			}
			createFlatElementList();
		} catch (CoreException e) {
			// TODO: handle this core exception
			e.printStackTrace();
		}
	}

	private void addPluginFrom(IFolder dir) {
		try {
			IFile descFile = dir.getFile(dir.getName() + ".txt");
			GstPlugin plg = readPluginFrom(descFile);

			for (IResource f : dir.members()) {
				if (!(f instanceof IFile) || !f.getFileExtension().equals("txt"))
					continue;
				String[] nameSegs = f.getName().split("\\.");
				if (nameSegs.length != 3)
					continue; // expects plugin_name.element_name.txt
				readElementFrom(plg, nameSegs[1], (IFile) f);
			}
			plugins.put(plg.getName(), plg);
		} catch (CoreException | IOException e) {
			// TODO: handle here and skip plug-in or re-throw and fail?
			e.printStackTrace();
		}
	}

	private GstPlugin readPluginFrom(IFile descFile) throws CoreException, IOException {
		BufferedReader in = new BufferedReader(new InputStreamReader(descFile.getContents()));
		GstPlugin plg = new GstPlugin();
		in.readLine(); // skip first line "Plugin Details:"
		plg.setName(in.readLine().substring(PROP_VALUE_OFFSET)); // "Name"
		plg.setDescription(in.readLine().substring(PROP_VALUE_OFFSET)); // "Description"
		return plg;
	}

	private void readElementProperties(GstElement elem, BufferedReader in) throws IOException {
		String nextLine = in.readLine();
		while (nextLine != null && !nextLine.isEmpty()) {
//			System.out.println(">" + nextLine + "<");
			if (nextLine.equals("Element Signals:")) // no more properties
				return;

			int nameDescSep = nextLine.indexOf(':');
			String propName = nextLine.substring(0, nameDescSep).trim();
			String propDesc = nextLine.substring(nameDescSep + 2);

//			System.out.println("Got property " + propName + " | " + propDesc);

			String flagsLine = in.readLine();
			boolean readOnly = !flagsLine.contains("writable");

			// TODO: caps
			if (propName.equals("caps")) {
				// read (and ignore atm) the caps, until empty line is detected
				do {
					nextLine = in.readLine();
				} while (nextLine != null && !nextLine.isEmpty());
				if (nextLine == null) // caps might be the last one in the file
					break;
				// current line is empty separator, read next valid one and continue
				nextLine = in.readLine();
				continue;
			}

			String typeLine = in.readLine().trim();
			String[] typeTok = typeLine.split(" ");
			if (typeTok[0].equals("String.")) {
				String defVal = "null";
				int bqPos = typeLine.indexOf("\"");
				if (bqPos != -1) {
					int eqPos = typeLine.indexOf("\"", bqPos + 1);
					defVal = typeLine.substring(bqPos + 1, eqPos);
				}
				elem.addProperty(new GstString(propName, propDesc, readOnly, defVal));
			} else if (typeTok[0].equals("Boolean.")) {
				boolean defVal = typeTok[2].equals("true");
				elem.addProperty(new GstBoolean(propName, propDesc, readOnly, defVal));
			} else if (typeTok[0].equals("Integer.")) {
				int minVal = Integer.parseInt(typeTok[2]);
				int maxVal = Integer.parseInt(typeTok[4]);
				int defVal = Integer.parseInt(typeTok[6]);
				elem.addProperty(new GstInteger(propName, propDesc, readOnly, minVal, maxVal, defVal));
			} else if (typeTok[0].equals("Unsigned") && typeTok[1].equals("Integer.")) {
				// TODO: need Long here
//				int minVal = Integer.parseInt(typeTok[3]);
//				int maxVal = Integer.parseInt(typeTok[5]);
//				int defVal = Integer.parseInt(typeTok[7]);
//				elem.addProperty(new GstInteger(propName, propDesc, readOnly, minVal, maxVal, defVal));
			} else if (typeTok[0].equals("Double.")) {

				// TODO:

//				double minVal = Double.parseDouble(typeTok[2]);
				double minVal = 0;
				double maxVal = 0;
//				double maxVal = Double.parseDouble(typeTok[4]);
				typeTok[typeTok.length - 1] = typeTok[typeTok.length - 1].replace(',', '.');
				double defVal = Double.parseDouble(typeTok[typeTok.length - 1]);
				elem.addProperty(new GstDouble(propName, propDesc, readOnly, minVal, maxVal, defVal));
			} else if (typeTok[0].equals("Enum") || typeTok[0].equals("Flags")) {
				String enumValLine = in.readLine().trim();
				while (enumValLine != null && enumValLine.length() > 0 && enumValLine.charAt(0) == '(') {
					enumValLine = in.readLine();
					if (enumValLine != null) {
						enumValLine = enumValLine.trim();
					}
				}
				nextLine = enumValLine;
				continue;
			}
			nextLine = in.readLine();
		}
	}

	private void readElementFrom(GstPlugin plg, String name, IFile descFile) throws CoreException, IOException {
		InputStream is = descFile.getContents();
		if (is.available() == 0) // empty file?
			return;
		BufferedReader in = new BufferedReader(new InputStreamReader(is));
		GstElement elem = new GstElement(name, plg);

//		System.out.println("Reading from " + plg.getName() + "." + elem.getName());

		in.readLine(); // skip "Factory Details:"
		in.readLine(); // skip "Rank"
		elem.setLongName(in.readLine().substring(PROP_VALUE_OFFSET));
		elem.setKlass(in.readLine().substring(PROP_VALUE_OFFSET));
		elem.setDescription(in.readLine().substring(PROP_VALUE_OFFSET));

		String nextLine = in.readLine();
		while (nextLine != null) {
			if (nextLine.equals("Element Properties:")) {
				readElementProperties(elem, in);
				break;
			}
			nextLine = in.readLine();
		}

		plg.addElement(elem);
	}
}
