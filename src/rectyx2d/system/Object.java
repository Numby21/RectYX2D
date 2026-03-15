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

import rectyx2d.util.DirectionEnum;
import static rectyx2d.util.Objects.isEqualsBooleans;

/**
 * Base object of RectYX2D.
 * This object is not designed to move and does not do collisions with others {@code Object}.
 * 
 * @author Renaud LE CLERRE
 */
public class Object {
	
	//====================
	// region Properties
	//====================
	
	protected int id;
	protected String name;

	protected int customType;
	protected Coordinates coordinates;
	private DirectionEnum buildDirection;
	
	protected boolean isCalcPrimary = false;
	protected boolean isCalcEnterIntoCalcDimensions = true;
	protected boolean isCalcEnterIntoScreen = false;
	protected boolean isCalcEnterIntoCenterScreen = false;
	protected boolean isCalcCollisionSelf = false;
	protected boolean isCalcCollisionFromOthers = false;
	
	protected boolean isBlock = false;
	
	//====================
	// end region Properties
	//====================
	
	//====================
	// region Constructors
	//====================
	
	public Object(
		int id, 
		String name, 
		int customType, 
		Coordinates coordinates, 
		DirectionEnum buildDirection
	) {
		this.id = id;
		this.name = name;
		this.customType = customType;
		this.coordinates = coordinates;
		this.buildDirection = buildDirection;
	}
	
	public Object(
		int id, 
		String name, 
		int customType, 
		Coordinates coordinates, 
		DirectionEnum buildDirection,
		boolean isCalcPrimary,
		boolean isCalcEnterIntoCalcDimensions,
		boolean isCalcEnterIntoScreen,
		boolean isCalcEnterIntoCenterScreen,
		boolean isCalcCollisionSelf,
		boolean isCalcCollisionFromOthers,
		boolean isBlock
	) {
		this(id, name, customType, coordinates, buildDirection);
		this.isCalcPrimary = isCalcPrimary;
		this.isCalcEnterIntoCalcDimensions = isCalcEnterIntoCalcDimensions;
		this.isCalcEnterIntoScreen = isCalcEnterIntoScreen;
		this.isCalcEnterIntoCenterScreen = isCalcEnterIntoCenterScreen;
		this.isCalcCollisionSelf = isCalcCollisionSelf;
		this.isCalcCollisionFromOthers = isCalcCollisionFromOthers;
		this.isBlock = isBlock;
	}
	
	//====================
	// end region Constructors
	//====================
	
	//====================
	// region Getters
	//====================
	
	public DirectionEnum getBuildDirection() {
		return buildDirection;
	}
	
	//====================
	// end region Getters
	//====================

	//====================
	// region Getters & Setters
	//====================

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getCustomType() {
		return customType;
	}

	public void setCustomType(int customType) {
		this.customType = customType;
	}

	public Coordinates getCoordinates() {
		return coordinates;
	}

	public void setCoordinates(Coordinates coordinates) {
		this.coordinates = coordinates;
	}

	public boolean getIsCalcPrimary() {
		return isCalcPrimary;
	}

	public void setIsCalcPrimary(boolean isCalcPrimary) {
		this.isCalcPrimary = isCalcPrimary;
	}

	public boolean getIsCalcEnterIntoCalcDimensions() {
		return isCalcEnterIntoCalcDimensions;
	}

	public void setIsCalcEnterIntoCalcDimensions(boolean isCalcEnterIntoCalcDimensions) {
		this.isCalcEnterIntoCalcDimensions = isCalcEnterIntoCalcDimensions;
	}
	
	public boolean getIsCalcEnterIntoScreen() {
		return isCalcEnterIntoScreen;
	}

	public void setIsCalcEnterIntoScreen(boolean isCalcEnterIntoScreen) {
		this.isCalcEnterIntoScreen = isCalcEnterIntoScreen;
	}

	public boolean getIsCalcEnterIntoCenterScreen() {
		return isCalcEnterIntoCenterScreen;
	}

	public void setIsCalcEnterIntoCenterScreen(boolean isCalcEnterIntoCenterScreen) {
		this.isCalcEnterIntoCenterScreen = isCalcEnterIntoCenterScreen;
	}
	
	public boolean getIsCalcCollisionSelf() {
		return isCalcCollisionSelf;
	}

	public void setIsCalcCollisionSelf(boolean isCalcCollisionSelf) {
		this.isCalcCollisionSelf = isCalcCollisionSelf;
	}

	public boolean getIsCalcCollisionFromOthers() {
		return isCalcCollisionFromOthers;
	}

	public void setIsCalcCollisionFromOthers(boolean isCalcCollisionFromOthers) {
		this.isCalcCollisionFromOthers = isCalcCollisionFromOthers;
	}

	public boolean getIsBlock() {
		return isBlock;
	}

	public void setIsBlock(boolean isBlock) {
		this.isBlock = isBlock;
	}
	
	//====================
	// end region Getters & Setters
	//====================
	
	//====================
	// region Methods
	//====================
	
	public boolean isCalcAnything() {
		return isEqualsBooleans(isCalcCollisionSelf, true) 
		|| isEqualsBooleans(isCalcCollisionFromOthers, true) 
		|| isEqualsBooleans(isCalcEnterIntoCalcDimensions, true) 
		|| isEqualsBooleans(isCalcEnterIntoScreen, true) 
		|| isEqualsBooleans(isCalcEnterIntoCenterScreen, true);
	}
	
	public boolean isCollisionLeftTop45(
		Object obj, 
		float coordinateLeftPlusSelf, 
		float coordinateTopPlusSelf, 
		float coordinateLeftPlus, 
		float coordinateTopPlus
	) {
		Coordinates objCoordinates = obj.getCoordinates();
		return this.coordinates.createLeft() + coordinateLeftPlusSelf <= objCoordinates.createRight() + coordinateLeftPlus 
		&& this.coordinates.createTop() + coordinateTopPlusSelf <= objCoordinates.createBottom() + coordinateTopPlus 
		&& this.coordinates.createLeft() + coordinateLeftPlusSelf >= objCoordinates.createLeft() + coordinateLeftPlus 
		&& this.coordinates.createTop() + coordinateTopPlusSelf >= objCoordinates.createTop() + coordinateTopPlus;
	}
	
	public boolean isCollisionRightTop45(
		Object obj, 
		float coordinateLeftPlusSelf, 
		float coordinateTopPlusSelf, 
		float coordinateLeftPlus, 
		float coordinateTopPlus
	) {
		Coordinates objCoordinates = obj.getCoordinates();
		return this.coordinates.createRight() + coordinateLeftPlusSelf <= objCoordinates.createRight() + coordinateLeftPlus 
		&& this.coordinates.createTop() + coordinateTopPlusSelf <= objCoordinates.createBottom() + coordinateTopPlus 
		&& this.coordinates.createRight() + coordinateLeftPlusSelf >= objCoordinates.createLeft() + coordinateLeftPlus 
		&& this.coordinates.createTop() + coordinateTopPlusSelf >= objCoordinates.createTop() + coordinateTopPlus;
	}
	
	public boolean isCollisionLeftBottom45(
		Object obj, 
		float coordinateLeftPlusSelf, 
		float coordinateTopPlusSelf, 
		float coordinateLeftPlus, 
		float coordinateTopPlus
	) {
		Coordinates objCoordinates = obj.getCoordinates();
		return this.coordinates.createLeft() + coordinateLeftPlusSelf <= objCoordinates.createRight() + coordinateLeftPlus 
		&& this.coordinates.createBottom() + coordinateTopPlusSelf >= objCoordinates.createTop() + coordinateTopPlus 
		&& this.coordinates.createLeft() + coordinateLeftPlusSelf >= objCoordinates.createLeft() + coordinateLeftPlus 
		&& this.coordinates.createBottom() + coordinateTopPlusSelf <= objCoordinates.createBottom() + coordinateTopPlus;
	}
	
	public boolean isCollisionRightBottom45(
		Object obj, 
		float coordinateLeftPlusSelf, 
		float coordinateTopPlusSelf, 
		float coordinateLeftPlus, 
		float coordinateTopPlus
	) {
		Coordinates objCoordinates = obj.getCoordinates();
		return this.coordinates.createRight() + coordinateLeftPlusSelf <= objCoordinates.createRight() + coordinateLeftPlus 
		&& this.coordinates.createBottom() + coordinateTopPlusSelf >= objCoordinates.createTop() + coordinateTopPlus 
		&& this.coordinates.createRight() + coordinateLeftPlusSelf >= objCoordinates.createLeft() + coordinateLeftPlus 
		&& this.coordinates.createBottom() + coordinateTopPlusSelf <= objCoordinates.createBottom() + coordinateTopPlus;
	}
	
	public boolean isCollisionToAny45(
		Object obj, 
		float coordinateLeftPlusSelf, 
		float coordinateTopPlusSelf, 
		float coordinateLeftPlus, 
		float coordinateTopPlus
	) {
		return isCollisionLeftTop45(obj, coordinateLeftPlusSelf, coordinateTopPlusSelf, coordinateLeftPlus, coordinateTopPlus)
		|| isCollisionRightTop45(obj, coordinateLeftPlusSelf, coordinateTopPlusSelf, coordinateLeftPlus, coordinateTopPlus)
		|| isCollisionLeftBottom45(obj, coordinateLeftPlusSelf, coordinateTopPlusSelf, coordinateLeftPlus, coordinateTopPlus)
		|| isCollisionRightBottom45(obj, coordinateLeftPlusSelf, coordinateTopPlusSelf, coordinateLeftPlus, coordinateTopPlus);
	}
	
	public boolean isCollisionToObject(
		Object obj, 
		float coordinateLeftPlusSelf, 
		float coordinateTopPlusSelf, 
		float coordinateLeftPlus, 
		float coordinateTopPlus
	) {
		Coordinates objCoordinates = obj.getCoordinates();
		return this.coordinates.createLeft() + coordinateLeftPlusSelf <= objCoordinates.createRight() + coordinateLeftPlus
		&& this.coordinates.createRight() + coordinateLeftPlusSelf >= objCoordinates.createLeft() + coordinateLeftPlus
		&& this.coordinates.createTop() + coordinateTopPlusSelf <= objCoordinates.createBottom() + coordinateTopPlus
		&& this.coordinates.createBottom() + coordinateTopPlusSelf >= objCoordinates.createTop() + coordinateTopPlus;
	}
	
	//====================
	// end region Methods
	//====================
	
}
