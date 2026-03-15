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

import java.util.HashSet;
import rectyx2d.util.DirectionEnum;
import rectyx2d.util.DirectionHelper;
import rectyx2d.util.LayoutIndexEnum;
import static rectyx2d.util.Objects.isEquals;
import static rectyx2d.util.Objects.isNotEquals;
import static rectyx2d.util.Objects.isEqualsBooleans;
import static rectyx2d.util.Objects.isEqualsInts;
import static rectyx2d.util.Objects.isNotEqualsLongs;

/**
 * Extended {@code Object} base object of RectYX2D.
 * This object is designed to move and can do collisions with others {@code Object}.
 * 
 * @author Renaud LE CLERRE
 */
public class ObjectMovable extends Object {
	
	//====================
	// region Properties
	//====================
	
	private int timeToMoveX;
	private int timeToMoveY;
	private long timeToMoveStartX;
	private long timeToMoveStartY;
	private long timeToMoveEndX;
	private long timeToMoveEndY;
	private long timeToMoveDoneX;
	private long timeToMoveDoneY;
	private long timeToMoveLeftX;
	private long timeToMoveLeftY;
	private float lengthToMoveX;
	private float lengthToMoveY;
	private float lengthToMoveDoneX;
	private float lengthToMoveDoneY;
	private float lengthToMoveLeftX;
	private float lengthToMoveLeftY;
	private DirectionEnum moveTo;
	private Coordinates coordinatesToMoveStart;
	private Coordinates coordinatesToMove;
	private Coordinates coordinatesLastOk = null;
	private boolean isCreateCoordinates = true;

	private HashSet<Object> objectsInCollisionsToLeftTop45 = new HashSet<>();
	private HashSet<Object> objectsInCollisionsToRightTop45 = new HashSet<>();
	private HashSet<Object> objectsInCollisionsToLeftBottom45 = new HashSet<>();
	private HashSet<Object> objectsInCollisionsToRightBottom45 = new HashSet<>();
	private HashSet<Object> objectsInCollisions = new HashSet<>();
	
	//====================
	// end region Properties
	//====================
	
	//====================
	// region Constructors
	//====================

	public ObjectMovable(
		int id, 
		String name, 
		int customType, 
		Coordinates coordinates, 
		DirectionEnum buildDirection
	) {
		super(
			id, 
			name, 
			customType, 
			coordinates, 
			buildDirection
		);
		this.construct();
	}
	
	public ObjectMovable(
		int id, 
		String name, 
		int customType, 
		Coordinates coordinates, 
		DirectionEnum buildDirection, 
		boolean isCalc,
		boolean isCalcEnterIntoCalcDimensions,
		boolean isCalcEnterIntoScreen,
		boolean isCalcEnterIntoCenterScreen,
		boolean isCalcCollisionSelf,
		boolean isCalcCollisionFromOthers,
		boolean isBlock
	) {
		super(
			id, 
			name, 
			customType, 
			coordinates, 
			buildDirection, 
			isCalc,
			isCalcEnterIntoCalcDimensions,
			isCalcEnterIntoScreen,
			isCalcEnterIntoCenterScreen,
			isCalcCollisionSelf,
			isCalcCollisionFromOthers,
			isBlock
		);
		this.construct();
	}

	private void construct() {
		this.stopMovesVars();
		this.stopMoveTo();
		if (isNotEquals(this.coordinates, null)) {
			Dimensions dimensions = this.coordinates.getDimensions();
			if (isNotEquals(dimensions, null)) {
				Dimensions coordinatesLastOkDimensions = new Dimensions(dimensions.getWidth(), dimensions.getHeight());
				this.coordinatesLastOk = new Coordinates(
					coordinatesLastOkDimensions, 
					this.coordinates.getX(), 
					this.coordinates.getY(), 
					this.coordinates.getZ(), 
					this.coordinates.getLayoutIndex()
				);
			}
		}
	}
	
	//====================
	// end region Constructors
	//====================
	
	//====================
	// region Getters
	//====================
	
	public int getTimeToMoveX() {
		return timeToMoveX;
	}
	
	public int getTimeToMoveY() {
		return timeToMoveY;
	}
	
	public long getTimeToMoveStartX() {
		return timeToMoveStartX;
	}
	
	public long getTimeToMoveStartY() {
		return timeToMoveStartY;
	}
	
	public long getTimeToMoveEndX() {
		return timeToMoveEndX;
	}

	public long getTimeToMoveEndY() {
		return timeToMoveEndY;
	}
	
	public long getTimeToMoveDoneX() {
		return timeToMoveDoneX;
	}

	public long getTimeToMoveDoneY() {
		return timeToMoveDoneY;
	}
	
	public long getTimeToMoveLeftX() {
		return timeToMoveLeftX;
	}

	public long getTimeToMoveLeftY() {
		return timeToMoveLeftY;
	}
	
	public float getLengthToMoveX() {
		return lengthToMoveX;
	}

	public float getLengthToMoveY() {
		return lengthToMoveY;
	}
	
	public float getLengthToMoveDoneX() {
		return lengthToMoveDoneX;
	}

	public float getLengthToMoveDoneY() {
		return lengthToMoveDoneY;
	}

	public float getLengthToMoveLeftX() {
		return lengthToMoveLeftX;
	}

	public float getLengthToMoveLeftY() {
		return lengthToMoveLeftY;
	}
	
	public DirectionEnum getMoveTo() {
		return moveTo;
	}
	
	//====================
	// end region Getters
	//====================

	//====================
	// region Getters & Setters
	//====================
	
	public boolean getIsCreateCoordinates() {
		return isCreateCoordinates;
	}

	public void setIsCreateCoordinates(boolean isCreateCoordinates) {
		this.isCreateCoordinates = isCreateCoordinates;
	}

	public HashSet<Object> getObjectsInCollisionsToLeftTop45() {
		return objectsInCollisionsToLeftTop45;
	}

	void setObjectsInCollisionsToLeftTop45(HashSet<Object> objectsInCollisionsToLeftTop45) {
		this.objectsInCollisionsToLeftTop45 = objectsInCollisionsToLeftTop45;
	}

	public HashSet<Object> getObjectsInCollisionsToRightTop45() {
		return objectsInCollisionsToRightTop45;
	}

	void setObjectsInCollisionsToRightTop45(HashSet<Object> objectsInCollisionsToRightTop45) {
		this.objectsInCollisionsToRightTop45 = objectsInCollisionsToRightTop45;
	}

	public HashSet<Object> getObjectsInCollisionsToLeftBottom45() {
		return objectsInCollisionsToLeftBottom45;
	}

	void setObjectsInCollisionsToLeftBottom45(HashSet<Object> objectsInCollisionsToLeftBottom45) {
		this.objectsInCollisionsToLeftBottom45 = objectsInCollisionsToLeftBottom45;
	}

	public HashSet<Object> getObjectsInCollisionsToRightBottom45() {
		return objectsInCollisionsToRightBottom45;
	}

	void setObjectsInCollisionsToRightBottom45(HashSet<Object> objectsInCollisionsToRightBottom45) {
		this.objectsInCollisionsToRightBottom45 = objectsInCollisionsToRightBottom45;
	}

	public HashSet<Object> getObjectsInCollisions() {
		return objectsInCollisions;
	}

	void setObjectsInCollisions(HashSet<Object> objectsInCollisions) {
		this.objectsInCollisions = objectsInCollisions;
	}

	//====================
	// end region Getters & Setters
	//====================
	
	//====================
	// region Find
	//====================
	
	public Float findCoordinatesLastOkLeft() {
		return (isNotEquals(coordinatesLastOk, null)) ? coordinatesLastOk.createLeft() : null;
	}
	
	public Float findCoordinatesLastOkRight() {
		return (isNotEquals(coordinatesLastOk, null)) ? coordinatesLastOk.createRight() : null;
	}
	
	public Float findCoordinatesLastOkTop() {
		return (isNotEquals(coordinatesLastOk, null)) ? coordinatesLastOk.createTop() : null;
	}
	
	public Float findCoordinatesLastOkBottom() {
		return (isNotEquals(coordinatesLastOk, null)) ? coordinatesLastOk.createBottom() : null;
	}
	
	public Float findCoordinatesLastOkLeftCenter() {
		return (isNotEquals(coordinatesLastOk, null)) ? coordinatesLastOk.createLeftCenter(): null;
	}
	
	public Float findCoordinatesLastOkTopCenter() {
		return (isNotEquals(coordinatesLastOk, null)) ? coordinatesLastOk.createTopCenter(): null;
	}
	
	public Float findCoordinatesLastOkDimensionsWidth() {
		Float dimension = null;
		if (isNotEquals(coordinatesLastOk, null)) {
			Dimensions dimensions = coordinatesLastOk.getDimensions();
			if (isNotEquals(dimensions, null))
				dimension = dimensions.getWidth();
		}
		return dimension;
	}
	
	public Float findCoordinatesLastOkDimensionsHeight() {
		Float dimension = null;
		if (isNotEquals(coordinatesLastOk, null)) {
			Dimensions dimensions = coordinatesLastOk.getDimensions();
			if (isNotEquals(dimensions, null))
				dimension = dimensions.getHeight();
		}
		return dimension;
	}
	
	public Float findCoordinatesToMoveStartLeft() {
		return (isNotEquals(coordinatesToMoveStart, null)) ? coordinatesToMoveStart.createLeft() : null;
	}
	
	public Float findCoordinatesToMoveStartRight() {
		return (isNotEquals(coordinatesToMoveStart, null)) ? coordinatesToMoveStart.createRight() : null;
	}
	
	public Float findCoordinatesToMoveStartTop() {
		return (isNotEquals(coordinatesToMoveStart, null)) ? coordinatesToMoveStart.createTop() : null;
	}
	
	public Float findCoordinatesToMoveStartBottom() {
		return (isNotEquals(coordinatesToMoveStart, null)) ? coordinatesToMoveStart.createBottom() : null;
	}
	
	public Float findCoordinatesToMoveStartLeftCenter() {
		return (isNotEquals(coordinatesToMoveStart, null)) ? coordinatesToMoveStart.createLeftCenter(): null;
	}
	
	public Float findCoordinatesToMoveStartTopCenter() {
		return (isNotEquals(coordinatesToMoveStart, null)) ? coordinatesToMoveStart.createTopCenter() : null;
	}
	
	public Float findCoordinatesToMoveStartDimensionsWidth() {
		Float dimension = null;
		if (isNotEquals(coordinatesToMoveStart, null)) {
			Dimensions dimensions = coordinatesToMoveStart.getDimensions();
			if (isNotEquals(dimensions, null))
				dimension = dimensions.getWidth();
		}
		return dimension;
	}
	
	public Float findCoordinatesToMoveStartDimensionsHeight() {
		Float dimension = null;
		if (isNotEquals(coordinatesToMoveStart, null)) {
			Dimensions dimensions = coordinatesToMoveStart.getDimensions();
			if (isNotEquals(dimensions, null))
				dimension = dimensions.getHeight();
		}
		return dimension;
	}
	
	public Float findCoordinatesToMoveLeft() {
		return (isNotEquals(coordinatesToMove, null)) ? coordinatesToMove.createLeft() : null;
	}
	
	public Float findCoordinatesToMoveRight() {
		return (isNotEquals(coordinatesToMove, null)) ? coordinatesToMove.createRight() : null;
	}
	
	public Float findCoordinatesToMoveTop() {
		return (isNotEquals(coordinatesToMove, null)) ? coordinatesToMove.createTop() : null;
	}
	
	public Float findCoordinatesToMoveBottom() {
		return (isNotEquals(coordinatesToMove, null)) ? coordinatesToMove.createBottom() : null;
	}
	
	public Float findCoordinatesToMoveLeftCenter() {
		return (isNotEquals(coordinatesToMove, null)) ? coordinatesToMove.createLeftCenter(): null;
	}
	
	public Float findCoordinatesToMoveTopCenter() {
		return (isNotEquals(coordinatesToMove, null)) ? coordinatesToMove.createTopCenter() : null;
	}
	
	public Float findCoordinatesToMoveDimensionsWidth() {
		Float dimension = null;
		if (isNotEquals(coordinatesToMove, null)) {
			Dimensions dimensions = coordinatesToMove.getDimensions();
			if (isNotEquals(dimensions, null))
				dimension = dimensions.getWidth();
		}
		return dimension;
	}
	
	public Float findCoordinatesToMoveDimensionsHeight() {
		Float dimension = null;
		if (isNotEquals(coordinatesToMove, null)) {
			Dimensions dimensions = coordinatesToMove.getDimensions();
			if (isNotEquals(dimensions, null))
				dimension = dimensions.getHeight();
		}
		return dimension;
	}
	
	//====================
	// end region Find
	//====================
	
	//====================
	// region is
	//====================

	public boolean isMoveToX() {
		return isNotEquals(this.moveTo, null) && isEqualsBooleans(DirectionHelper.isDirectionContainsX(this.moveTo), true);
	}

	public boolean isMoveToY() {
		return isNotEquals(this.moveTo, null) && isEqualsBooleans(DirectionHelper.isDirectionContainsY(this.moveTo), true);
	}
	
	public boolean isInCollision() {
		boolean isInCollision = false;
		if (isEqualsBooleans(objectsInCollisionsToLeftTop45.isEmpty(), false) 
		|| isEqualsBooleans(objectsInCollisionsToRightTop45.isEmpty(), false) 
		|| isEqualsBooleans(objectsInCollisionsToLeftBottom45.isEmpty(), false) 
		|| isEqualsBooleans(objectsInCollisionsToRightBottom45.isEmpty(), false) 
		|| isEqualsBooleans(objectsInCollisions.isEmpty(), false))
			isInCollision = true;
		return isInCollision;
	}
	
	public boolean isInCollisionToBlock() {
		boolean isInCollision = false;
		isInCollision = isInCollisionToBlock(this.objectsInCollisionsToLeftTop45);
		if (isEqualsBooleans(isInCollision, false))
			isInCollision = isInCollisionToBlock(this.objectsInCollisionsToRightTop45);
		if (isEqualsBooleans(isInCollision, false))
			isInCollision = isInCollisionToBlock(this.objectsInCollisionsToLeftBottom45);
		if (isEqualsBooleans(isInCollision, false))
			isInCollision = isInCollisionToBlock(this.objectsInCollisionsToRightBottom45);
		if (isEqualsBooleans(isInCollision, false))
			isInCollision = isInCollisionToBlock(this.objectsInCollisions);
		return isInCollision;
	}
	
	private boolean isInCollisionToBlock(HashSet<Object> objectsInCollisions) {
		boolean isInCollision = false;
		for (Object objectInCollision : objectsInCollisions) {
			if (isEqualsBooleans(objectInCollision.getIsBlock(), true)) {
				isInCollision = true;
				break;
			}
		}
		return isInCollision;
	}
	
	//====================
	// end region is
	//====================
	
	//====================
	// region create move
	//====================
	
	public void createMove(
		DirectionEnum moveTo,
		float lengthToMoveX,
		float lengthToMoveY,
		int timeToMoveX,
		int timeToMoveY
	) {
		this.moveTo = moveTo;
		this.lengthToMoveX = lengthToMoveX;
		this.lengthToMoveY = lengthToMoveY;
		this.timeToMoveX = timeToMoveX;
		this.timeToMoveY = timeToMoveY;
		this.createMovesVars();
	}
	
	public void createMoveX(
		DirectionEnum moveTo,
		float lengthToMoveX,
		int timeToMoveX
	) {
		this.moveTo = moveTo;
		this.lengthToMoveX = lengthToMoveX;
		this.timeToMoveX = timeToMoveX;
		this.createMovesVars();
	}
	
	public void createMoveY(
		DirectionEnum moveTo,
		float lengthToMoveY,
		int timeToMoveY
	) {
		this.moveTo = moveTo;
		this.lengthToMoveY = lengthToMoveY;
		this.timeToMoveY = timeToMoveY;
		this.createMovesVars();
	}
	
	private void createLengthToMoveDoneX() {
		if (this.timeToMoveEndX > 0 && this.timeToMoveStartX > 0) {
			long timeToMoveFromStartToEnd = this.timeToMoveEndX - this.timeToMoveStartX;
			if (isNotEqualsLongs(timeToMoveFromStartToEnd, 0)) {
				float percentDone = (float)this.timeToMoveDoneX / (float)timeToMoveFromStartToEnd;
				float toMoveFromStartToEnd = this.coordinatesToMove.getX() - this.coordinatesToMoveStart.getX();
				this.lengthToMoveDoneX = toMoveFromStartToEnd * percentDone;
			}
		}
	}
	
	private void createLengthToMoveDoneY() {
		if (this.timeToMoveEndY > 0 && this.timeToMoveStartY > 0) {
			long timeToMoveFromStartToEnd = this.timeToMoveEndY - this.timeToMoveStartY;
			if (isNotEqualsLongs(timeToMoveFromStartToEnd, 0)) {
				float percentDone = (float)this.timeToMoveDoneY / (float)timeToMoveFromStartToEnd;
				float toMoveFromStartToEnd = this.coordinatesToMove.getY() - this.coordinatesToMoveStart.getY();
				this.lengthToMoveDoneY = toMoveFromStartToEnd * percentDone;
			}
		}
	}

	private void createLengthToMoveLeftX() {
		float lengthToMoveDone = this.lengthToMoveDoneX;
		if (lengthToMoveDone < 0)
			lengthToMoveDone = lengthToMoveDone * -1;
		this.lengthToMoveLeftX = this.lengthToMoveX - lengthToMoveDone;
	}
	
	private void createLengthToMoveLeftY() {
		float lengthToMoveDone = this.lengthToMoveDoneY;
		if (lengthToMoveDone < 0)
			lengthToMoveDone = lengthToMoveDone * -1;
		this.lengthToMoveLeftY = this.lengthToMoveY - lengthToMoveDone;
	}

	private void createMovesVars() {
		long calcTime = Engine.calcTime;
		this.timeToMoveStartX = calcTime;
		this.timeToMoveStartY = calcTime;
		this.timeToMoveEndX = this.timeToMoveStartX + this.timeToMoveX;
		this.timeToMoveEndY = this.timeToMoveStartY + this.timeToMoveY;

		float coordinatesX = this.coordinates.getX();
		float coordinatesY = this.coordinates.getY();
		float coordinatesZ = this.coordinates.getZ();
		LayoutIndexEnum layoutIndex = this.coordinates.getLayoutIndex();
		Dimensions coordinatesDimensions = this.coordinates.getDimensions();
		Dimensions dimensionsToMoveStart = new Dimensions(coordinatesDimensions.getWidth(), coordinatesDimensions.getHeight());
		
		this.coordinatesToMoveStart = new Coordinates(
				dimensionsToMoveStart,
				coordinatesX,
				coordinatesY,
				coordinatesZ,
				layoutIndex
		);

		float coordinatesToMovePlusCalcX = this.lengthToMoveX;
		if (DirectionHelper.isDirectionContainsSlowMoveX(this.moveTo))
			coordinatesToMovePlusCalcX = coordinatesToMovePlusCalcX / 2;

		float coordinatesToMovePlusX = 0;
		if (isEqualsBooleans(DirectionHelper.isDirectionContainsLeft(this.moveTo), true))
			coordinatesToMovePlusX = -coordinatesToMovePlusCalcX;
		else if (isEqualsBooleans(DirectionHelper.isDirectionContainsRight(this.moveTo), true))
			coordinatesToMovePlusX = coordinatesToMovePlusCalcX;
		float coordinatesToMoveCalcX = coordinatesX + coordinatesToMovePlusX;
		
		float coordinatesToMovePlusCalcY = this.lengthToMoveY;
		if (DirectionHelper.isDirectionContainsSlowMoveY(this.moveTo))
			coordinatesToMovePlusCalcY = coordinatesToMovePlusCalcY / 2;

		float coordinatesToMovePlusY = 0;
		if (isEqualsBooleans(DirectionHelper.isDirectionContainsTop(this.moveTo), true))
			coordinatesToMovePlusY = -coordinatesToMovePlusCalcY;
		else if (isEqualsBooleans(DirectionHelper.isDirectionContainsBottom(this.moveTo), true))
			coordinatesToMovePlusY = coordinatesToMovePlusCalcY;
		float coordinatesToMoveCalcY = coordinatesY + coordinatesToMovePlusY;

		this.coordinatesToMove = new Coordinates(
				dimensionsToMoveStart,
				coordinatesToMoveCalcX,
				coordinatesToMoveCalcY,
				coordinatesZ,
				layoutIndex
		);
	}
	
	//====================
	// end region create move
	//====================
	
	//====================
	// region create coordinates
	//====================
	
	boolean createCoordinatesX() {
		boolean isTimeToMoveLeftDone = false;
		if (isEqualsBooleans(this.isCreateCoordinates, true)) {
			if (isNotEquals(this.moveTo, null) 
			&& isEqualsBooleans(DirectionHelper.isDirectionContainsX(this.moveTo), true)
			&& this.timeToMoveX > 0) {
				this.timeToMoveLeftX = this.timeToMoveEndX - Engine.calcTime;
				this.timeToMoveDoneX = this.timeToMoveX - this.timeToMoveLeftX;
				if (this.timeToMoveLeftX <= 0)
					isTimeToMoveLeftDone = true;
			}
			if (isNotEquals(this.coordinatesToMoveStart, null)) {
				this.createLengthToMoveDoneX();
				this.createLengthToMoveLeftX();

				float xNew;
				if (isEqualsBooleans(isTimeToMoveLeftDone, false))
					xNew = this.coordinatesToMoveStart.getX() + this.lengthToMoveDoneX;
				else
					xNew = this.coordinatesToMove.getX();
				this.coordinates.setX(xNew);
			}
		}
		return isTimeToMoveLeftDone;
	}
	
	boolean createCoordinatesY() {
		boolean isTimeToMoveLeftDone = false;
		if (isEqualsBooleans(this.isCreateCoordinates, true)) {
			if (isNotEquals(this.moveTo, null) 
			&& isEqualsBooleans(DirectionHelper.isDirectionContainsY(this.moveTo), true)
			&& this.timeToMoveY > 0) {
				this.timeToMoveLeftY = this.timeToMoveEndY - Engine.calcTime;
				this.timeToMoveDoneY = this.timeToMoveY - this.timeToMoveLeftY;
				if (this.timeToMoveLeftY <= 0)
					isTimeToMoveLeftDone = true;
			}
			if (isNotEquals(this.coordinatesToMoveStart, null)) {
				this.createLengthToMoveDoneY();
				this.createLengthToMoveLeftY();

				float yNew;
				if (isEqualsBooleans(isTimeToMoveLeftDone, false))
					yNew = this.coordinatesToMoveStart.getY() + this.lengthToMoveDoneY;
				else
					yNew = this.coordinatesToMove.getY();
				this.coordinates.setY(yNew);
			}
		}
		return isTimeToMoveLeftDone;
	}
	
	void createCoordinatesLastOkX() {
		Coordinates coordinatesOk = this.getCoordinates();
		if (isNotEquals(coordinatesOk, null)) {
			if (isEquals(this.coordinatesLastOk, null)) {
				Dimensions dimensionsOk = coordinatesOk.getDimensions();
				if (isNotEquals(dimensionsOk, null)) {
					Dimensions dimentionsLastOk = new Dimensions(dimensionsOk.getWidth(), dimensionsOk.getHeight());
					this.coordinatesLastOk = new Coordinates(dimentionsLastOk, coordinatesOk.getX(), coordinatesOk.getY(), coordinatesOk.getZ(), coordinatesOk.getLayoutIndex());
				}
			} else
				this.coordinatesLastOk.setX(coordinatesOk.getX());
		}
	}
	
	void createCoordinatesLastOkY() {
		Coordinates coordinatesOk = this.getCoordinates();
		if (isNotEquals(coordinatesOk, null)) {
			if (isEquals(this.coordinatesLastOk, null)) {
				Dimensions dimensionsOk = coordinatesOk.getDimensions();
				if (isNotEquals(dimensionsOk, null)) {
					Dimensions dimentionsLastOk = new Dimensions(dimensionsOk.getWidth(), dimensionsOk.getHeight());
					this.coordinatesLastOk = new Coordinates(dimentionsLastOk, coordinatesOk.getX(), coordinatesOk.getY(), coordinatesOk.getZ(), coordinatesOk.getLayoutIndex());
				}
			} else
				this.coordinatesLastOk.setY(coordinatesOk.getY());
		}
	}
	
	public void createCoordinatesFromCoordinatesLastOk() {
		this.recoverCoordinatesLastOkX();
		this.recoverCoordinatesLastOkY();
	}
	
	public void createCoordinatesFromCoordinatesLastOkX() {
		this.recoverCoordinatesLastOkX();
	}
	
	public void createCoordinatesFromCoordinatesLastOkY() {
		this.recoverCoordinatesLastOkY();
	}
	
	private void recoverCoordinatesLastOkX() {
		this.coordinates.setX(this.coordinatesLastOk.getX());
	}

	private void recoverCoordinatesLastOkY() {
		this.coordinates.setY(this.coordinatesLastOk.getY());
	}
	
	//====================
	// end region create coordinates
	//====================
	
	//====================
	// region stop move
	//====================
	
	public void stopMove() {
		stopMoveTo();
		stopMovesVars();
	}
	
	public void stopMoveX() {
		stopMoveToX();
		stopMovesVarsX();
	}
	
	public void stopMoveY() {
		stopMoveToY();
		stopMovesVarsY();
	}
	
	private void stopMoveTo() {
		moveTo = null;
	}

	private void stopMoveToX() {
		DirectionEnum moveToNew = null;
		if (isNotEquals(moveTo, null)) {
			if (isEqualsBooleans(DirectionHelper.isDirectionContainsTop(moveTo), true))
				moveToNew = DirectionEnum.Top;
			else if (isEqualsBooleans(DirectionHelper.isDirectionContainsBottom(moveTo), true))
				moveToNew = DirectionEnum.Bottom;
		}
		moveTo = moveToNew;
	}

	private void stopMoveToY() {
		DirectionEnum moveToNew = null;
		if (isNotEquals(moveTo, null)) {
			if (isEqualsBooleans(DirectionHelper.isDirectionContainsLeft(moveTo), true))
				moveToNew = DirectionEnum.Left;
			else if (isEqualsBooleans(DirectionHelper.isDirectionContainsRight(moveTo), true))
				moveToNew = DirectionEnum.Right;
		}
		moveTo = moveToNew;
	}

	private void stopMovesVars() {
		timeToMoveX = 0;
		timeToMoveY = 0;
		timeToMoveStartX = 0;
		timeToMoveStartY = 0;
		timeToMoveEndX = 0;
		timeToMoveEndY = 0;
		timeToMoveDoneX = 0;
		timeToMoveDoneY = 0;
		timeToMoveLeftX = 0;
		timeToMoveLeftY = 0;
		coordinatesToMove = null;
		coordinatesToMoveStart = null;
		lengthToMoveX = 0;
		lengthToMoveY = 0;
		lengthToMoveDoneX = 0;
		lengthToMoveDoneY = 0;
		lengthToMoveLeftX = 0;
		lengthToMoveLeftY = 0;
	}

	private void stopMovesVarsX() {
		if (isNotEquals(coordinatesToMove, null))
			coordinatesToMove.setX(coordinatesLastOk.getX());
		if (isNotEquals(coordinatesToMoveStart, null))
			coordinatesToMoveStart.setX(coordinatesLastOk.getX());
		timeToMoveX = 0;
		timeToMoveStartX = 0;
		timeToMoveEndX = 0;
		timeToMoveDoneX = 0;
		timeToMoveLeftX = 0;
		lengthToMoveX = 0;
		lengthToMoveDoneX = 0;
		lengthToMoveLeftX = 0;
	}

	private void stopMovesVarsY() {
		if (isNotEquals(coordinatesToMove, null))
			coordinatesToMove.setY(coordinatesLastOk.getY());
		if (isNotEquals(coordinatesToMoveStart, null))
			coordinatesToMoveStart.setY(coordinatesLastOk.getY());
		timeToMoveY = 0;
		timeToMoveStartY = 0;
		timeToMoveEndY = 0;
		timeToMoveDoneY = 0;
		timeToMoveLeftY = 0;
		lengthToMoveY = 0;
		lengthToMoveDoneY = 0;
		lengthToMoveLeftY = 0;
	}
	
	//====================
	// end region stop move
	//====================
	
	void clearObjectsInCollisions() {
		objectsInCollisionsToLeftTop45.clear();
		objectsInCollisionsToRightTop45.clear();
		objectsInCollisionsToLeftBottom45.clear();
		objectsInCollisionsToRightBottom45.clear();
		objectsInCollisions.clear();
	}
	
	void clear() {
		this.clearObjectsInCollisions();
	}
	
}
