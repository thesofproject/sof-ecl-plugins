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

package org.sofproject.fw.ui.visuals;

import org.sofproject.core.memmap.FwImageMemMap;
import org.sofproject.core.memmap.FwImageMemSection;
import org.sofproject.core.memmap.MemSegment;
import org.sofproject.fw.ui.graph.FwBinZestGraphBuilder;
import org.sofproject.fw.ui.resources.FwBinResources;

import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;

public class FwMemoryMapVisual extends Region {
	private static final double HORIZONTAL_PADDING = 5d;
	private static final double VERTICAL_SPACING = 2d;
	private static final double HORIZONTAL_SPACING = 5d;

	private static final double MM_NODE_WIDTH = 1000d;
	private static final double MM_NODE_SEG_HEIGHT = 100d;

	private Text nameText;
	private VBox topBox;
	private VBox memSegmentBox;

	public FwMemoryMapVisual(org.eclipse.gef.graph.Node node) {
		topBox = new VBox(VERTICAL_SPACING);

		topBox.prefWidthProperty().bind(widthProperty());
		topBox.prefHeightProperty().bind(heightProperty());

		FwImageMemMap mm = FwBinZestGraphBuilder.getModelMemMap(node).getMemMap();

		nameText = new Text("FW Memory Map - " + mm.getMemLayout().getName());
		nameText.setTextOrigin(VPos.TOP);
		nameText.setFont(FwBinResources.getGraphBoldFont());

		memSegmentBox = new VBox();
		for (MemSegment segment : mm.getMemLayout().getMemSegments()) {
			HBox segBox = new HBox(HORIZONTAL_SPACING);

			// segment properties box on the left size
			VBox segTextBox = new VBox();
			Text segName = new Text(segment.getName());
			segName.setTextOrigin(VPos.TOP);
			Text segBaseAddr = new Text(String.format("0x%08x", segment.getBaseAddr()));
			segBaseAddr.setTextOrigin(VPos.TOP);
			Text segSize = new Text(String.format("0x%x", segment.getSize()));
			segSize.setTextOrigin(VPos.TOP);
			segTextBox.getChildren().addAll(segName, segBaseAddr, segSize);

			// colorful memory sections visualization on the right size
			int curAddr = segment.getBaseAddr();
			HBox sectionBox = new HBox();
			for (FwImageMemSection sec : mm.getSectionsFromSegment(segment)) {

				if (!sec.allocsMem())
					continue;

				// Insert spacers in memory gaps.
				if (curAddr < sec.getVma()) {
					double spaceW = (MM_NODE_WIDTH * (sec.getVma() - curAddr) / segment.getSize());
					Rectangle r = new Rectangle(spaceW, MM_NODE_SEG_HEIGHT - 2 * HORIZONTAL_PADDING, Color.LIGHTGREY);
					r.setStrokeWidth(0d);
					r.setFill(Color.WHITE);
					sectionBox.getChildren().add(r);
				}
				curAddr = sec.getVma() + sec.getSize();

				double width = (MM_NODE_WIDTH * sec.getSize() / segment.getSize());
				if (width >= 1.0) {
					Rectangle r = new Rectangle(width - 1, MM_NODE_SEG_HEIGHT - 2 * HORIZONTAL_PADDING,
							Color.LIGHTGREY);
					r.setStroke(Color.DARKGREY);
					r.setStrokeWidth(.5d);
					if (sec.hasAttr("code")) {
						r.setFill(Color.LIGHTGREEN);
					} else if (sec.hasAttr("data")) {
						r.setFill(Color.LIGHTSALMON);
					} else if (sec.isSystemHeap()) {
						r.setFill(Color.CORNFLOWERBLUE);
					} else if (sec.isHeap()) {
						r.setFill(Color.LIGHTBLUE);
					} else if (sec.isStack()) {
						r.setFill(Color.LIGHTYELLOW);
					}
					Pane oneSecBox = new Pane();
					oneSecBox.setMinWidth(r.getWidth());
					oneSecBox.setMinHeight(r.getHeight());
					Text secName = new Text(sec.getName());
					secName.getTransforms().add(new Scale(0.7, 0.7));
					secName.getTransforms().add(new Rotate(90.0));
					oneSecBox.getChildren().addAll(new Group(r), new Group(secName));
					sectionBox.getChildren().add(oneSecBox);
				}
			}
			segBox.getChildren().addAll(segTextBox, sectionBox);

			memSegmentBox.getChildren().add(segBox);
		}

		topBox.getChildren().addAll(nameText, memSegmentBox);
		topBox.setAlignment(Pos.CENTER_LEFT);

		setMinSize(USE_COMPUTED_SIZE, USE_COMPUTED_SIZE);

		getChildren().add(topBox);
	}

	@Override
	public double computeMinHeight(double width) {
		return topBox.minHeight(width);
	}

	@Override
	public double computeMinWidth(double height) {
		return memSegmentBox.getMinWidth() + HORIZONTAL_PADDING * 2;
	}

	@Override
	protected double computePrefHeight(double width) {
		return minHeight(width);
	}

	@Override
	protected double computePrefWidth(double height) {
		return minWidth(height);
	}

	@Override
	public Orientation getContentBias() {
		return Orientation.HORIZONTAL;
	}

	public Text getNameText() {
		return nameText;
	}

	public void setName(String name) {
		this.nameText.setText(name);
	}
}
