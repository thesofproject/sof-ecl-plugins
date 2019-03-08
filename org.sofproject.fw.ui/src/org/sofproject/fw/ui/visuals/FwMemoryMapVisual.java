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

import org.eclipse.gef.fx.nodes.GeometryNode;
import org.eclipse.gef.geometry.planar.RoundedRectangle;
import org.sofproject.fw.ui.graph.FwBinZestGraphBuilder;
import org.sofproject.fw.ui.resources.FwBinResources;

import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

public class FwMemoryMapVisual extends Region {
	private static final double HORIZONTAL_PADDING = 5d;
	private static final double VERTICAL_PADDING = 5d;
	private static final double VERTICAL_SPACING = 2d;

	private Text nameText;
	private GeometryNode<RoundedRectangle> shape;
	private VBox topBox;

	public FwMemoryMapVisual(org.eclipse.gef.graph.Node node) {
		GeometryNode<RoundedRectangle> shape = new GeometryNode<>(new RoundedRectangle(0, 0, 500, 70, 4, 4));

		shape.setFill(FwBinZestGraphBuilder.getNodeColor(node));
		shape.setStroke(FwBinZestGraphBuilder.getNodeBorderColor(node));
		shape.setStrokeWidth(FwBinZestGraphBuilder.getNodeBorderWidth(node));

		topBox = new VBox(VERTICAL_SPACING);
		topBox.setPadding(new Insets(VERTICAL_PADDING, HORIZONTAL_PADDING, VERTICAL_PADDING, HORIZONTAL_PADDING));

		shape.prefWidthProperty().bind(widthProperty());
		shape.prefHeightProperty().bind(heightProperty());
		topBox.prefWidthProperty().bind(widthProperty());
		topBox.prefHeightProperty().bind(heightProperty());

		nameText = new Text("FW Memory Map");
		nameText.setTextOrigin(VPos.TOP);
		nameText.setFont(FwBinResources.getGraphBoldFont());

		topBox.getChildren().addAll(nameText);
		topBox.setAlignment(Pos.CENTER);

		setMinSize(USE_COMPUTED_SIZE, USE_COMPUTED_SIZE);

		getChildren().addAll(new Group(shape), new Group(topBox));
	}

	@Override
	public double computeMinHeight(double width) {
		return topBox.minHeight(width);
	}

	@Override
	public double computeMinWidth(double height) {
		double maxW = nameText.getLayoutBounds().getWidth();
		return maxW + HORIZONTAL_PADDING * 2;
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

	public GeometryNode<?> getGeometryNode() {
		return shape;
	}

	public Text getNameText() {
		return nameText;
	}

	public void setColor(Color color) {
		shape.setFill(color);
	}

	public void setName(String name) {
		this.nameText.setText(name);
	}
}
