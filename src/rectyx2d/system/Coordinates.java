/*
 * Copyright 2026 Renaud LE CLERRE
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package rectyx2d.system;

import rectyx2d.util.LayoutIndexEnum;

/**
 *
 * @author Renaud LE CLERRE
 */
public class Coordinates {
	
	private Dimensions dimensions;
	private float x;
	private float y;
	private float z;
	private LayoutIndexEnum layoutIndex;
	private boolean isInsideCalcDimensions = false;
	private boolean isInsideScreen = false;
	
	public Coordinates(
		Dimensions dimensions, 
		float x, 
		float y, 
		float z, 
		LayoutIndexEnum layoutIndex
	) {
		this.dimensions = dimensions;
		this.x = x;
		this.y = y;
		this.z = z;
		this.layoutIndex = layoutIndex;
	}
	
	//====================
	// region Getters & Setters
	//====================

	public Dimensions getDimensions() {
		return dimensions;
	}

	public void setDimensions(Dimensions dimensions) {
		this.dimensions = dimensions;
	}

	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}

	public float getZ() {
		return z;
	}

	public void setZ(float z) {
		this.z = z;
	}

	public LayoutIndexEnum getLayoutIndex() {
		return layoutIndex;
	}

	public void setLayoutIndex(LayoutIndexEnum layoutIndex) {
		this.layoutIndex = layoutIndex;
	}

	public boolean getIsInsideCalcDimensions() {
		return isInsideCalcDimensions;
	}

	public void setIsInsideCalcDimensions(boolean isInsideCalcDimensions) {
		this.isInsideCalcDimensions = isInsideCalcDimensions;
	}
	
	public boolean getIsInsideScreen() {
		return isInsideScreen;
	}

	public void setIsInsideScreen(boolean isInsideScreen) {
		this.isInsideScreen = isInsideScreen;
	}
	
	//====================
	// end region Getters & Setters
	//====================

	//====================
	// region methods
	//====================

	public float createLeft() {
		return x;
	}
	
	public float createTop() {
		return y;
	}

	public float createRight() {
		return x + dimensions.getWidth();
	}

	public float createRight(float x) {
		return x + dimensions.getWidth();
	}

	public float createBottom() {
		return y + dimensions.getHeight();
	}

	public float createBottom(float y) {
		return y + dimensions.getHeight();
	}
	
	public float createLeftCenter() {
		return x + dimensions.getWidth() / 2;
	}

	public float createTopCenter() {
		return y + dimensions.getHeight() / 2;
	}

	//====================
	// end region methods
	//====================
	
}
