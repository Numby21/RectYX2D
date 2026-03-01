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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import rectyx2d.util.CalcDimensionSizeEnum;
import rectyx2d.util.Date;
import rectyx2d.util.LayoutIndexEnum;
import static rectyx2d.util.Objects.isEquals;
import static rectyx2d.util.Objects.isNotEquals;
import static rectyx2d.util.Objects.isEqualsBooleans;
import static rectyx2d.util.Objects.isNotEqualsBooleans;
import static rectyx2d.util.Objects.isEqualsInts;

/**
 * Principal class, do majority of calculations of RectYX2D.
 * 
 * @author Renaud LE CLERRE
 */
public abstract class Engine {
	
	//====================
	// region Properties
	//====================
	
	/**
	 * System timestamp of the engine.
	 * It is updated at every iteration of the engine priority calculations.
	 */
	static long calcTime = 0;
	
	/**
	 * The system timestamp at the last engine start.
	 * It is the system time, created like the {@code calcTime}.
	 */
	private long startedSystemTime = 0;
	
	/**
	 * The timestamp at the last engine start.
	 * Provided by {@code java.util.Date}.
	 */
	private long startedTime = 0;
	
	/**
	 * The area where arrays of {@code Object} and {@code ObjectMovable} are priority calculated.
	 */
	private final Object CALC_DIMENSIONS;
	
	/**
	 * The size of the screen's device.
	 */
	private final Dimensions SCREEN_DIMENSIONS;
	
	/**
	 * The screen's device.
	 * Usage is only to calculate screen dimensions of the device, if arrays of {@code Object} are inside the screen, or similars calculations.
	 */
	private final Object SCREEN;
	
	/**
	 * Contains majority of engine objects. 
	 * It is the "world" object.
	 */
	private final Grid GRID;

	private boolean isStarted = false;
	
	/**
	 * Priority thread, calculate the engine arrays of {@code Object} and {@code ObjectMovable} inside the {@code CALC_DIMENSIONS}.
	 */
	private Thread threadEngine = null;
	
	/**
	 * Secondary thread, calculate if the engine arrays of {@code Object} and {@code ObjectMovable} are inside or outside the {@code CALC_DIMENSIONS}.
	 * Also do secondary engine calculations of array of {@code ObjectMovable} that are outside of the {@code CALC_DIMENSIONS}.
	 */
	private Thread threadObjectsToCalc = null;

	private final java.lang.Object LOCK_OBJECTS_TO_CALC = new java.lang.Object();
	private final java.lang.Object LOCK_RUNNABLES = new java.lang.Object();

	/**
	 * Array of {@code Object} that are inside the {@code CALC_DIMENSIONS}.
	 * The engine calculate it in priority.
	 */
	private Object[] objectsToCalc;
	
	/**
	 * Array of {@code ObjectMovable} that are inside the {@code CALC_DIMENSIONS}.
	 * The engine calculate it in priority.
	 */
	private ObjectMovable[] objectsMovablesToCalc;
	
	/**
	 * Array of {@code ObjectMovable} that are outside the {@code CALC_DIMENSIONS}.
	 * The engine calculate it slowly.
	 */
	private ObjectMovable[] objectsMovablesToCalcSecondary;
	
	/**
	 * List of {@code Runnable} to run at the end of an iteration of the engine priority calculations.
	 * Usage is to run some events code synchronous of the engine in the class that extends this {@code Engine} class.
	 */
	private List<Runnable> runnables = Collections.synchronizedList(new ArrayList<>());
	
	//====================
	// end region Properties
	//====================
	
	//====================
	// region Constructors
	//====================

	/**
	 * Constructs a new Engine.
	 *
	 * @param screenDimensions the screen dimensions of the device.
	 * @param calcDimensionSizeX {@code enum} of the size of the width of the {@code CALC_DIMENSIONS}. This will determine the value of the width side size {@code calcDimensionsScreenPlusX}.
	 * @param calcDimensionSizeY {@code enum} of the size of the height of the {@code CALC_DIMENSIONS}. This will determine the value of the height side size {@code calcDimensionsScreenPlusY}.
	 * @param isCalcDimensionsSizesEquals if true, the bigger size created beetween the width side size {@code calcDimensionsScreenPlusX} and the height side size {@code calcDimensionsScreenPlusY} of the {@code CALC_DIMENSIONS} will be applied to both width and height sides sizes.
	 * @param grid the {@code GRID} object that contains most engine objects.
	 */
	public Engine(
		Dimensions screenDimensions,
		CalcDimensionSizeEnum calcDimensionSizeX,
		CalcDimensionSizeEnum calcDimensionSizeY,
		boolean isCalcDimensionsSizesEquals,
		Grid grid
	) {
		this.startResetArrays();
		
		this.SCREEN_DIMENSIONS = screenDimensions;
		this.GRID = grid;
		
		float screenWidth = screenDimensions.getWidth();
		float screenHeight = screenDimensions.getHeight();
		
		float calcDimensionsScreenPlusX = createCalcDimensionSize(calcDimensionSizeX, screenWidth);
		float calcDimensionsScreenPlusY = createCalcDimensionSize(calcDimensionSizeY, screenHeight);
		if (isEqualsBooleans(isCalcDimensionsSizesEquals, true)) {
			if (calcDimensionsScreenPlusX > calcDimensionsScreenPlusY)
				calcDimensionsScreenPlusY = calcDimensionsScreenPlusX;
			else
				calcDimensionsScreenPlusX = calcDimensionsScreenPlusY;
		}
		
		float calcDimensionsLimitLeft = -calcDimensionsScreenPlusX;
		float calcDimensionsLimitRight = screenWidth + calcDimensionsScreenPlusX;
		float calcDimensionsLimitTop = -calcDimensionsScreenPlusY;
		float calcDimensionsLimitBottom = screenHeight + calcDimensionsScreenPlusY;
		
		float calcDimensionsLeft = calcDimensionsLimitLeft;
		float calcDimensionsTop = calcDimensionsLimitTop;
		
		if (calcDimensionsLeft < 0)
			calcDimensionsLeft = -(calcDimensionsLeft);
		if (calcDimensionsTop < 0)
			calcDimensionsTop = -(calcDimensionsTop);
		float calcDimensionsWidth = calcDimensionsLimitRight + calcDimensionsLeft;
		float calcDimensionsHeight = calcDimensionsLimitBottom + calcDimensionsTop;
		
		Dimensions calcDimensionsDimensions = new Dimensions(calcDimensionsWidth, calcDimensionsHeight);
		Coordinates calcDimensionsCoordinates = new Coordinates(
			calcDimensionsDimensions,
			calcDimensionsLimitLeft,
			calcDimensionsLimitTop,
			Integer.MIN_VALUE,
			LayoutIndexEnum.Global
		);
		this.CALC_DIMENSIONS = new Object(Integer.MIN_VALUE, null, Integer.MIN_VALUE, calcDimensionsCoordinates, null);
		
		Coordinates screenCoordinates = new Coordinates(
			screenDimensions,
			0,
			0,
			Integer.MIN_VALUE,
			LayoutIndexEnum.Global
		);
		this.SCREEN = new Object(Integer.MIN_VALUE, null, Integer.MIN_VALUE, screenCoordinates, null);
	}
	
	private float createCalcDimensionSize(CalcDimensionSizeEnum calcDimensionSize, float screenSize) {
		float calcDimensionsScreenPlus = 0;
		switch (calcDimensionSize) {
			case Size0:
				calcDimensionsScreenPlus = screenSize / 10;
				break;
			case Size1:
				calcDimensionsScreenPlus = screenSize / 8;
				break;
			case Size2:
				calcDimensionsScreenPlus = screenSize / 7;
				break;
			case Size3:
				calcDimensionsScreenPlus = screenSize / 5;
				break;
			case Size4:
				calcDimensionsScreenPlus = screenSize / 3;
				break;
			case Size5:
				calcDimensionsScreenPlus = screenSize / 2;
				break;
			case Size6:
				calcDimensionsScreenPlus = screenSize;
				break;
			case Size7:
				Double calcDouble = (double)screenSize * 1.5;
				calcDimensionsScreenPlus = calcDouble.intValue();
				break;
			case Size8:
				calcDimensionsScreenPlus = screenSize * 2;
				break;
			case Size9:
				calcDimensionsScreenPlus = screenSize * 3;
				break;
		}
		return calcDimensionsScreenPlus;
	}
	
	//====================
	// end region Constructors
	//====================

	//====================
	// region Getters
	//====================
	
	public long getStartedSystemTime() {
		return startedSystemTime;
	}

	public long getStartedTime() {
		return startedTime;
	}
	
	public float get_CALC_DIMENSIONS_coordinates_left() {
		return this.CALC_DIMENSIONS.getCoordinates().createLeft();
	}
	
	public float get_CALC_DIMENSIONS_coordinates_right() {
		return this.CALC_DIMENSIONS.getCoordinates().createRight();
	}
	
	public float get_CALC_DIMENSIONS_coordinates_top() {
		return this.CALC_DIMENSIONS.getCoordinates().createTop();
	}
	
	public float get_CALC_DIMENSIONS_coordinates_bottom() {
		return this.CALC_DIMENSIONS.getCoordinates().createBottom();
	}

	public Grid get_GRID() {
		return GRID;
	}
	
	//====================
	// end region Getters
	//====================

	//====================
	// region start/stop
	//====================
	
	private void startResetArrays() {
		this.objectsToCalc = new Object[]{};
		this.objectsMovablesToCalc = new ObjectMovable[]{};
		this.objectsMovablesToCalcSecondary = new ObjectMovable[]{};
		this.runnables.clear();
	}
	
	private void startReady() {
		calcObjectEnterIntoScreen(
			this.GRID,
			false,
			false,
			false,
			false,
			0,
			0
		);
	}
	
	/**
	 * Starts the engine.
	 */
	public void start() {
		this.isStarted = true;
		this.startedSystemTime = Date.createSystemTime();
		Engine.calcTime = this.startedSystemTime;
		this.startedTime = new java.util.Date().getTime();
		
		this.startReady();

		this.threadEngine = new Thread(new Runnable() {
			@Override
			public void run() {
				while (isEqualsBooleans(Engine.this.isStarted, true)) {
					Engine.this.calc();
				}
			}
		});
		this.threadEngine.setDaemon(false);
		this.threadEngine.setPriority(9);
		this.threadEngine.start();

		this.threadObjectsToCalc = new Thread(new Runnable() {
			@Override
			public void run() {
				long objectsToCalcLastTime = 0;
				while (isEqualsBooleans(Engine.this.isStarted, true)) {
					long systemTime = Date.createSystemTime();
					long systemTimeLess = systemTime - 275;
					if (systemTimeLess > objectsToCalcLastTime) {
						objectsToCalcLastTime = systemTime;
						calcCreateArraysObjects();
					}

					calcSecondary();

					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		});
		this.threadObjectsToCalc.setDaemon(false);
		this.threadObjectsToCalc.setPriority(1);
		this.threadObjectsToCalc.start();
	}
	
	/**
	 * Indicate if the engine is started.
	 */
	public boolean isStarted() {
		return isNotEquals(this.threadEngine, null) && isEqualsBooleans(this.threadEngine.isAlive(), true)
		&& isNotEquals(this.threadObjectsToCalc, null) && isEqualsBooleans(this.threadObjectsToCalc.isAlive(), true);
	}
	
	/**
	 * Stop the engine.
	 */
	public void stop() {
		this.isStarted = false;
	}
	
	/**
	 * Clear majority of engine's {@code Object} and {@code ObjectMovable}.
	 */
	public void clear() {
		this.startResetArrays();
		this.GRID.clear();
	}
	
	//====================
	// end region start/stop
	//====================
	
	//====================
	// region engine calc
	//====================
	
	/**
	 * Select array of {@code Object} and {@code ObjectMovable} that the engine do calculations into the {@code CALC_DIMENSIONS}.
	 * Arrays {@code objectsToCalc} and {@code objectsMovablesToCalc} contains priority objects to calculate.
	 * Array {@code objectsMovablesToCalcSecondary} contains secondary objects to calculate slowly.
	 */
	private void calcCreateArraysObjects() {
		HashSet<Object> objectsToCalcSet = new HashSet<>();
		HashSet<ObjectMovable> objectsMovablesToCalcSet = new HashSet<>();
		HashSet<ObjectMovable> objectsMovablesToCalcSecondarySet = new HashSet<>();

		Coordinates gridCoordinates = this.GRID.getCoordinates();
		final List<Object> ALL_OBJECTS = this.GRID.findAllObjects();

		HashSet<Integer> objectsMovablesIdsInside = new HashSet<>();
		List<ObjectMovable> objectsMovables = this.GRID.getObjectsMovables();
		for (ObjectMovable objectMovable : objectsMovables) {
			if (isEqualsBooleans(objectMovable.isCalcAnything(), true)) {
				boolean isInside = isObjectInsideCalcDimensions(objectMovable);
				if (isEqualsBooleans(isInside, true))
					objectsMovablesIdsInside.add(objectMovable.getId());
			}
		}
		for (Object obj : ALL_OBJECTS) {
			if (isEqualsBooleans(obj.isCalcAnything(), true)) {
				ObjectMovable objectMovable = null;
				try {
					objectMovable = (ObjectMovable) obj;
				} catch (Exception e) { }

				boolean isInside = false;
				if (isEquals(objectMovable, null))
					isInside = isObjectInsideCalcDimensions(obj);
				else if (isEqualsBooleans(objectsMovablesIdsInside.contains(objectMovable.getId()), true))
					isInside = true;

				Coordinates objCoordinates = obj.getCoordinates();
				LayoutIndexEnum objLayoutIndex = objCoordinates.getLayoutIndex();

				if (isEqualsBooleans(isInside, false) 
				&& isEquals(objectMovable, null) 
				&& isEqualsBooleans(obj.getIsCalcCollisionFromOthers(), true)) {
					for (ObjectMovable objectMovableToTry : objectsMovables) {
						Coordinates objectMovableCoordinates = objectMovableToTry.getCoordinates();
						LayoutIndexEnum objectMovableLayoutIndex = objectMovableCoordinates.getLayoutIndex();
						if (isEquals(objectMovableLayoutIndex, LayoutIndexEnum.Objects)
						&& isEqualsBooleans(objectsMovablesIdsInside.contains(objectMovableToTry.getId()), true)
						&& isEqualsBooleans(objectMovableToTry.getIsCalcCollisionSelf(), true)) {
							float screenWidthHalf = this.SCREEN_DIMENSIONS.getWidth() / 2;
							float screenWidthHalfHalf = screenWidthHalf / 2;
							float screenHeightHalf = this.SCREEN_DIMENSIONS.getHeight() / 2;
							float screenHeightHalfHalf = screenHeightHalf / 2;

							Dimensions objectMovableDimensions = objectMovableCoordinates.getDimensions();
							float widthTry = objectMovableDimensions.getWidth() + screenWidthHalf;
							float heightTry = objectMovableDimensions.getHeight() + screenHeightHalf;
							float xTry = objectMovableCoordinates.getX() - screenWidthHalfHalf;
							float yTry = objectMovableCoordinates.getY() - screenHeightHalfHalf;

							Dimensions dimensionsTry = new Dimensions(widthTry, heightTry);
							Coordinates coordinatesTry = new Coordinates(dimensionsTry, xTry, yTry, 0, LayoutIndexEnum.Objects);
							Object objTry = new Object(
								Integer.MIN_VALUE, 
								null, 
								Integer.MIN_VALUE, 
								coordinatesTry, 
								null
							);

							float coordinateLeftPlusSelf = gridCoordinates.createLeft();
							float coordinateTopPlusSelf = gridCoordinates.createTop();
							float coordinateLeftPlus = (isEquals(objLayoutIndex, LayoutIndexEnum.Objects)) ? gridCoordinates.createLeft() : 0 ;
							float coordinateTopPlus = (isEquals(objLayoutIndex, LayoutIndexEnum.Objects)) ? gridCoordinates.createTop() : 0 ;
							isInside = objTry.isCollisionToObject(
								obj, 
								coordinateLeftPlusSelf, 
								coordinateTopPlusSelf, 
								coordinateLeftPlus, 
								coordinateTopPlus
							);

							if (isEqualsBooleans(isInside, true))
								break;
						}
					}
				}

				boolean isObjCalcPrimary = obj.getIsCalcPrimary();
				if (isEqualsBooleans(isInside, true)) {
					if (isEqualsBooleans(isObjCalcPrimary, true)) {
						if (isNotEquals(objectMovable, null))
							objectsMovablesToCalcSet.add(objectMovable);
						else
							objectsToCalcSet.add(obj);
					} else if (isNotEquals(objectMovable, null))
						objectsMovablesToCalcSecondarySet.add(objectMovable);

					if (isEqualsBooleans(obj.getIsCalcEnterIntoCalcDimensions(), true)
					&& isEqualsBooleans(objCoordinates.getIsInsideCalcDimensions(), false)) {
						objCoordinates.setIsInsideCalcDimensions(true);
						this.eventEnterIntoCalcDimensions(obj);
					}
				} else if (isEqualsBooleans(isInside, false)) {
					if (isEqualsBooleans(isObjCalcPrimary, true) 
					&& isNotEquals(objectMovable, null)) {
						// Object need calc but it is outside from "calcDimensions" so it can be calc secondary
						objectsMovablesToCalcSecondarySet.add(objectMovable);
					}
					if (isEqualsBooleans(obj.getIsCalcEnterIntoCalcDimensions(), true)
					&& isEqualsBooleans(objCoordinates.getIsInsideCalcDimensions(), true)) {
						objCoordinates.setIsInsideCalcDimensions(false);
						this.eventExitFromCalcDimensions(obj);
					}
				}
			}
		}

		Object[] objectsToCalc = objectsToCalcSet.toArray(new Object[0]);
		ObjectMovable[] objectsMovablesToCalc = objectsMovablesToCalcSet.toArray(new ObjectMovable[0]);
		ObjectMovable[] objectsMovablesToCalcSecondary = objectsMovablesToCalcSecondarySet.toArray(new ObjectMovable[0]);
		synchronized (this.LOCK_OBJECTS_TO_CALC) {
			this.objectsToCalc = objectsToCalc;
			this.objectsMovablesToCalc = objectsMovablesToCalc;
			this.objectsMovablesToCalcSecondary = objectsMovablesToCalcSecondary;
		}
	}
	
	/**
	 * The engine priority calculations. 
	 * Calculations of array of {@code Object} and {@code ObjectMovable} to calculate in priority into the {@code CALC_DIMENSIONS}.
	 * Arrays {@code objectsToCalc} and {@code objectsMovablesToCalc} contains priority objects to calculate.
	 */
	private void calc() {
		long calcTime = Date.createSystemTime();
		long lessTime = calcTime - 20;
		if (lessTime > Engine.calcTime)
			calcTime = Engine.calcTime + 15;
		
		Engine.calcTime = calcTime;
		
		final Object[] OBJECTS;
		final ObjectMovable[] OBJECTS_MOVABLES;
		synchronized (this.LOCK_OBJECTS_TO_CALC) {
			OBJECTS = this.objectsToCalc;
			OBJECTS_MOVABLES = this.objectsMovablesToCalc;
		}

		calcGrid();

		for (int i = 0; i < OBJECTS_MOVABLES.length; i++) {
			ObjectMovable objectMovable = OBJECTS_MOVABLES[i];

			calcObjectMovable(objectMovable);
			
			boolean isCollisionExistWithObjects = calcObjectMovableCollisions(OBJECTS, objectMovable);
			boolean isCollisionExistWithObjectsMovables = calcObjectMovableCollisions(OBJECTS_MOVABLES, objectMovable);
			boolean isCollisionExist = isEqualsBooleans(isCollisionExistWithObjects, true) 
			|| isEqualsBooleans(isCollisionExistWithObjectsMovables, true);
			
			calcObjectMovableEndCollisions(objectMovable, isCollisionExist);

			objectMovable.createCoordinatesLastOkX();
			objectMovable.createCoordinatesLastOkY();
		}

		this.GRID.createCoordinatesLastOkX();
		this.GRID.createCoordinatesLastOkY();
		
		if (isEqualsBooleans(this.runnables.isEmpty(), false)) {
			synchronized (this.LOCK_RUNNABLES) {
				for (Runnable runnable : this.runnables) {
					runnable.run();
				}
				this.runnables.clear();
			}
		}
	}
	
	/**
	 * Calculations of array of {@code ObjectMovable} outside of the {@code CALC_DIMENSIONS}.
	 * Array {@code objectsMovablesToCalcSecondary} contains secondary objects to calculate slowly.
	 */
	private void calcSecondary() {
		final List<Object> ALL_OBJECTS = this.GRID.findAllObjects();
		final Object[] OBJECTS = ALL_OBJECTS.toArray(new Object[0]);
		for (int i = 0; i < this.objectsMovablesToCalcSecondary.length; i++) {
			ObjectMovable objectMovable = this.objectsMovablesToCalcSecondary[i];
			
			calcObjectMovable(objectMovable);
			boolean isCollisionExist = calcObjectMovableCollisions(OBJECTS, objectMovable);
			calcObjectMovableEndCollisions(objectMovable, isCollisionExist);
			
			objectMovable.createCoordinatesLastOkX();
			objectMovable.createCoordinatesLastOkY();
		}
	}
	
	/**
	 * Calculations of the {@code GRID} object.
	 */
	private void calcGrid() {
		boolean isObjIntoScreenBeforeCreateCoordinatesLeftTop45 = false;
		boolean isObjIntoScreenBeforeCreateCoordinatesRightTop45 = false;
		boolean isObjIntoScreenBeforeCreateCoordinatesLeftBottom45 = false;
		boolean isObjIntoScreenBeforeCreateCoordinatesRightBottom45 = false;
		if (isEqualsBooleans(this.GRID.getIsCalcEnterIntoScreen(), true)) {
			isObjIntoScreenBeforeCreateCoordinatesLeftTop45 = this.GRID.isCollisionLeftTop45(this.SCREEN, 0, 0, 0, 0);
			isObjIntoScreenBeforeCreateCoordinatesRightTop45 = this.GRID.isCollisionRightTop45(this.SCREEN, 0, 0, 0, 0);
			isObjIntoScreenBeforeCreateCoordinatesLeftBottom45 = this.GRID.isCollisionLeftBottom45(this.SCREEN, 0, 0, 0, 0);
			isObjIntoScreenBeforeCreateCoordinatesRightBottom45 = this.GRID.isCollisionRightBottom45(this.SCREEN, 0, 0, 0, 0);
		}
		
		boolean isTimeToMoveLeftDoneX = this.GRID.createCoordinatesX();
		if (isEqualsBooleans(isTimeToMoveLeftDoneX, true)) {
			this.eventTimeToMoveLeftDoneX(this.GRID);
		}
		boolean isTimeToMoveLeftDoneY = this.GRID.createCoordinatesY();
		if (isEqualsBooleans(isTimeToMoveLeftDoneY, true)) {
			this.eventTimeToMoveLeftDoneY(this.GRID);
		}
		
		calcObjectEnterIntoScreen(this.GRID,
			isObjIntoScreenBeforeCreateCoordinatesLeftTop45,
			isObjIntoScreenBeforeCreateCoordinatesRightTop45,
			isObjIntoScreenBeforeCreateCoordinatesLeftBottom45,
			isObjIntoScreenBeforeCreateCoordinatesRightBottom45,
			0,
			0
		);
	}
	
	private void calcObjectEnterIntoScreen(
		Object obj, 
		boolean isObjIntoScreenBeforeCreateCoordinatesLeftTop45, 
		boolean isObjIntoScreenBeforeCreateCoordinatesRightTop45, 
		boolean isObjIntoScreenBeforeCreateCoordinatesLeftBottom45, 
		boolean isObjIntoScreenBeforeCreateCoordinatesRightBottom45, 
		float coordinateLeftPlusSelf, 
		float coordinateTopPlusSelf
	) {
		if (isEqualsBooleans(obj.getIsCalcEnterIntoScreen(), true)) {
			boolean isObjInsideScreen = isObjectInsideScreen(obj);
			Coordinates objCoordinates = obj.getCoordinates();
			boolean isObjCoordinatesInsideScreen = objCoordinates.getIsInsideScreen();
			if (isNotEqualsBooleans(isObjInsideScreen, isObjCoordinatesInsideScreen)) {
				boolean isObjCollisionLeftTop45 = obj.isCollisionLeftTop45(this.SCREEN, coordinateLeftPlusSelf, coordinateTopPlusSelf, 0, 0);
				boolean isObjCollisionRightTop45 = obj.isCollisionRightTop45(this.SCREEN, coordinateLeftPlusSelf, coordinateTopPlusSelf, 0, 0);
				boolean isObjCollisionLeftBottom45 = obj.isCollisionLeftBottom45(this.SCREEN, coordinateLeftPlusSelf, coordinateTopPlusSelf, 0, 0);
				boolean isObjCollisionRightBottom45 = obj.isCollisionRightBottom45(this.SCREEN, coordinateLeftPlusSelf, coordinateTopPlusSelf, 0, 0);
				
				objCoordinates.setIsInsideScreen(isObjInsideScreen);
				if (isEqualsBooleans(isObjInsideScreen, true)) {
					this.eventEnterIntoScreen(
						obj, 
						isObjCollisionLeftTop45, 
						isObjCollisionRightTop45, 
						isObjCollisionLeftBottom45, 
						isObjCollisionRightBottom45
					);
				} else {
					this.eventExitFromScreen(
						obj, 
						isObjIntoScreenBeforeCreateCoordinatesLeftTop45, 
						isObjIntoScreenBeforeCreateCoordinatesRightTop45, 
						isObjIntoScreenBeforeCreateCoordinatesLeftBottom45, 
						isObjIntoScreenBeforeCreateCoordinatesRightBottom45
					);
				}
			}
		}
	}
	
	/**
	 * Calculations of an {@code ObjectMovable}.
	 *
	 * @param objectMovable the {@code ObjectMovable} to calculate.
	 */
	private void calcObjectMovable(ObjectMovable objectMovable) {
		Coordinates objectMovableCoordinates = objectMovable.getCoordinates();
		boolean isObjIntoScreenBeforeCreateCoordinatesLeftTop45 = false;
		boolean isObjIntoScreenBeforeCreateCoordinatesRightTop45 = false;
		boolean isObjIntoScreenBeforeCreateCoordinatesLeftBottom45 = false;
		boolean isObjIntoScreenBeforeCreateCoordinatesRightBottom45 = false;
		float coordinateLeftPlusSelf = 0;
		float coordinateTopPlusSelf = 0;
		if (isEqualsBooleans(objectMovable.getIsCalcEnterIntoScreen(), true)) {
			LayoutIndexEnum objectMovableLayoutIndex = objectMovableCoordinates.getLayoutIndex();
			coordinateLeftPlusSelf = (isEqualsInts(objectMovableLayoutIndex.ordinal(), LayoutIndexEnum.Objects.ordinal())) ? objectMovableCoordinates.createLeft() : 0 ;
			coordinateTopPlusSelf = (isEqualsInts(objectMovableLayoutIndex.ordinal(), LayoutIndexEnum.Objects.ordinal())) ? objectMovableCoordinates.createTop() : 0 ;
			isObjIntoScreenBeforeCreateCoordinatesLeftTop45 = objectMovable.isCollisionLeftTop45(this.SCREEN, coordinateLeftPlusSelf, coordinateTopPlusSelf, 0, 0);
			isObjIntoScreenBeforeCreateCoordinatesRightTop45 = objectMovable.isCollisionRightTop45(this.SCREEN, coordinateLeftPlusSelf, coordinateTopPlusSelf, 0, 0);
			isObjIntoScreenBeforeCreateCoordinatesLeftBottom45 = objectMovable.isCollisionLeftBottom45(this.SCREEN, coordinateLeftPlusSelf, coordinateTopPlusSelf, 0, 0);
			isObjIntoScreenBeforeCreateCoordinatesRightBottom45 = objectMovable.isCollisionRightBottom45(this.SCREEN, coordinateLeftPlusSelf, coordinateTopPlusSelf, 0, 0);
		}
		
		float objLeftBeforeCreateCoordinates = objectMovableCoordinates.createLeft();
		float objRightBeforeCreateCoordinates = objectMovableCoordinates.createRight();
		float objTopBeforeCreateCoordinates = objectMovableCoordinates.createTop();
		float objBottomBeforeCreateCoordinates = objectMovableCoordinates.createBottom();

		//====================
		// region objectMovable createCoordinates
		//====================
		
		boolean isTimeToMoveLeftDoneX = objectMovable.createCoordinatesX();
		if (isEqualsBooleans(isTimeToMoveLeftDoneX, true)) {
			this.eventTimeToMoveLeftDoneX(objectMovable);
		}
		boolean isTimeToMoveLeftDoneY = objectMovable.createCoordinatesY();
		if (isEqualsBooleans(isTimeToMoveLeftDoneY, true)) {
			this.eventTimeToMoveLeftDoneY(objectMovable);
		}
		
		//====================
		// end region objectMovable createCoordinates
		//====================
		
		//====================
		// region objectMovable enterIntoScreen
		//====================
		
		calcObjectEnterIntoScreen(
			objectMovable,
			isObjIntoScreenBeforeCreateCoordinatesLeftTop45,
			isObjIntoScreenBeforeCreateCoordinatesRightTop45,
			isObjIntoScreenBeforeCreateCoordinatesLeftBottom45,
			isObjIntoScreenBeforeCreateCoordinatesRightBottom45,
			coordinateLeftPlusSelf,
			coordinateTopPlusSelf
		);
		
		//====================
		// end region objectMovable enterIntoScreen
		//====================

		//====================
		// region objectMovable enterIntoCenterScreen
		//====================
		
		if (isEqualsBooleans(objectMovable.getIsCalcEnterIntoCenterScreen(), true)) {
			Coordinates gridCoordinates = this.GRID.getCoordinates();
			LayoutIndexEnum objLayoutIndex = objectMovableCoordinates.getLayoutIndex();
			float coordinateLeftPlus = (isEqualsInts(objLayoutIndex.ordinal(), LayoutIndexEnum.Objects.ordinal())) ? gridCoordinates.createLeft() : 0 ;
			float coordinateTopPlus = (isEqualsInts(objLayoutIndex.ordinal(), LayoutIndexEnum.Objects.ordinal())) ? gridCoordinates.createTop() : 0 ;
			boolean isObjEnterLeftIntoCenterScreen = isObjectEnterLeftIntoCenterScreen(objectMovable, 
				objLeftBeforeCreateCoordinates,
				coordinateLeftPlus
			);
			boolean isObjEnterRightIntoCenterScreen = isObjectEnterRightIntoCenterScreen(objectMovable, 
				objRightBeforeCreateCoordinates,
				coordinateLeftPlus
			);
			boolean isObjEnterTopIntoCenterScreen = isObjectEnterTopIntoCenterScreen(objectMovable, 
				objTopBeforeCreateCoordinates,
				coordinateTopPlus
			);
			boolean isObjEnterBottomIntoCenterScreen = isObjectEnterBottomIntoCenterScreen(objectMovable, 
				objBottomBeforeCreateCoordinates,
				coordinateTopPlus
			);

			if (isEqualsBooleans(isObjEnterLeftIntoCenterScreen, true) 
			|| isEqualsBooleans(isObjEnterRightIntoCenterScreen, true) 
			|| isEqualsBooleans(isObjEnterTopIntoCenterScreen, true) 
			|| isEqualsBooleans(isObjEnterBottomIntoCenterScreen, true)) {
				this.eventEnterIntoCenterScreen(objectMovable, 
					isObjEnterLeftIntoCenterScreen, 
					isObjEnterRightIntoCenterScreen, 
					isObjEnterTopIntoCenterScreen, 
					isObjEnterBottomIntoCenterScreen
				);
			}
		}
		
		//====================
		// end region objectMovable enterIntoCenterScreen
		//====================
	}
	
	/**
	 * Calculations of an {@code ObjectMovable} collisions.
	 *
	 * @param OBJECTS array that contains {@code Object} to compare with the {@code ObjectMovable} during calculations.
	 * @param objectMovable the {@code ObjectMovable} to calculate.
	 * @return {@code true} if the {@code ObjectMovable} is in collision with any other {@code Object}.
	 */
	private boolean calcObjectMovableCollisions(final Object[] OBJECTS, ObjectMovable objectMovable) {
		boolean isCollisionExist = false;
		if (isEqualsBooleans(objectMovable.getIsCalcCollisionSelf(), true)) {
			Coordinates gridCoordinates = this.GRID.getCoordinates();
			Coordinates objectMovableCoordinates = objectMovable.getCoordinates();
			LayoutIndexEnum objectMovableLayoutIndex = objectMovableCoordinates.getLayoutIndex();
			float coordinateLeftPlusSelf = (isEqualsInts(objectMovableLayoutIndex.ordinal(), LayoutIndexEnum.Objects.ordinal())) ? gridCoordinates.createLeft() : 0 ;
			float coordinateTopPlusSelf = (isEqualsInts(objectMovableLayoutIndex.ordinal(), LayoutIndexEnum.Objects.ordinal())) ? gridCoordinates.createTop() : 0 ;
			
			HashSet<Object> objectsInCollisionsToLeftTop45 = new HashSet<>();
			HashSet<Object> objectsInCollisionsToRightTop45 = new HashSet<>();
			HashSet<Object> objectsInCollisionsToLeftBottom45 = new HashSet<>();
			HashSet<Object> objectsInCollisionsToRightBottom45 = new HashSet<>();
			HashSet<Object> objectsInCollisions = new HashSet<>();

			for (int i = 0; i < OBJECTS.length; i++) {
				Object obj = OBJECTS[i];
				
				if (isNotEquals(objectMovable, obj) 
				&& isEqualsBooleans(obj.getIsCalcCollisionFromOthers(), true)) {
					Object objectInCollisionToLeftTop45ForIteration = null;
					Object objectInCollisionToRightTop45ForIteration = null;
					Object objectInCollisionToLeftBottom45ForIteration = null;
					Object objectInCollisionToRightBottom45ForIteration = null;
					Object objectInCollisionForIteration = null;

					Coordinates objCoordinates = obj.getCoordinates();
					LayoutIndexEnum objLayoutIndex = objCoordinates.getLayoutIndex();
					float coordinateLeftPlus = (isEqualsInts(objLayoutIndex.ordinal(), LayoutIndexEnum.Objects.ordinal())) ? gridCoordinates.createLeft() : 0 ;
					float coordinateTopPlus = (isEqualsInts(objLayoutIndex.ordinal(), LayoutIndexEnum.Objects.ordinal())) ? gridCoordinates.createTop() : 0 ;

					if (isEqualsBooleans(objectMovable.isCollisionLeftTop45(obj, coordinateLeftPlusSelf, coordinateTopPlusSelf, coordinateLeftPlus, coordinateTopPlus), true))
						objectInCollisionToLeftTop45ForIteration = obj;
					if (isEqualsBooleans(objectMovable.isCollisionRightTop45(obj, coordinateLeftPlusSelf, coordinateTopPlusSelf, coordinateLeftPlus, coordinateTopPlus), true))
						objectInCollisionToRightTop45ForIteration = obj;
					if (isEqualsBooleans(objectMovable.isCollisionLeftBottom45(obj, coordinateLeftPlusSelf, coordinateTopPlusSelf, coordinateLeftPlus, coordinateTopPlus), true))
						objectInCollisionToLeftBottom45ForIteration = obj;
					if (isEqualsBooleans(objectMovable.isCollisionRightBottom45(obj, coordinateLeftPlusSelf, coordinateTopPlusSelf, coordinateLeftPlus, coordinateTopPlus), true))
						objectInCollisionToRightBottom45ForIteration = obj;
					if (isEqualsBooleans(objectMovable.isCollisionToObject(obj, coordinateLeftPlusSelf, coordinateTopPlusSelf, coordinateLeftPlus, coordinateTopPlus), true))
						objectInCollisionForIteration = obj;

					if (isNotEquals(objectInCollisionToLeftTop45ForIteration, null))
						objectsInCollisionsToLeftTop45.add(objectInCollisionToLeftTop45ForIteration);
					if (isNotEquals(objectInCollisionToRightTop45ForIteration, null))
						objectsInCollisionsToRightTop45.add(objectInCollisionToRightTop45ForIteration);
					if (isNotEquals(objectInCollisionToLeftBottom45ForIteration, null))
						objectsInCollisionsToLeftBottom45.add(objectInCollisionToLeftBottom45ForIteration);
					if (isNotEquals(objectInCollisionToRightBottom45ForIteration, null))
						objectsInCollisionsToRightBottom45.add(objectInCollisionToRightBottom45ForIteration);
					if (isNotEquals(objectInCollisionForIteration, null))
						objectsInCollisions.add(objectInCollisionForIteration);
				}
			}
			if (isEqualsBooleans(objectsInCollisionsToLeftTop45.isEmpty(), false) 
			|| isEqualsBooleans(objectsInCollisionsToRightTop45.isEmpty(), false) 
			|| isEqualsBooleans(objectsInCollisionsToLeftBottom45.isEmpty(), false) 
			|| isEqualsBooleans(objectsInCollisionsToRightBottom45.isEmpty(), false)
			|| isEqualsBooleans(objectsInCollisions.isEmpty(), false)) {
				isCollisionExist = true;
				
				objectMovable.setObjectsInCollisionsToLeftTop45(objectsInCollisionsToLeftTop45);
				objectMovable.setObjectsInCollisionsToRightTop45(objectsInCollisionsToRightTop45);
				objectMovable.setObjectsInCollisionsToLeftBottom45(objectsInCollisionsToLeftBottom45);
				objectMovable.setObjectsInCollisionsToRightBottom45(objectsInCollisionsToRightBottom45);
				objectMovable.setObjectsInCollisions(objectsInCollisions);
				
				if (isEqualsBooleans(objectMovable.isInCollisionToBlock(), true)) {
					objectMovable.setIsCreateCoordinates(false);
					this.eventCollisionToBlock(objectMovable);
				} else
					this.eventCollisionToObject(objectMovable);
			}
		}
		return isCollisionExist;
	}
	
	/**
	 * Calculations the end of an {@code ObjectMovable} collisions.
	 * 
	 * @param objectMovable the {@code ObjectMovable} to calculate.
	 * @param isCollisionExist indicate if collision exists beetween the {@code ObjectMovable} and any other {@code Object}.
	 */
	private void calcObjectMovableEndCollisions(ObjectMovable objectMovable, boolean isCollisionExist) {
		if (isEqualsBooleans(isCollisionExist, false) 
		&& isEqualsBooleans(objectMovable.isInCollision(), true)) {
			this.eventEndCollisionToObject(objectMovable);
			objectMovable.setIsCreateCoordinates(true);
			objectMovable.clearObjectsInCollisions();
		}
	}
	
	//====================
	// end region engine calc
	//====================
	
	//====================
	// region create object coordinate
	//====================
	
	public float createObjectCoordinateLeftInsideCalcDimensions(float objLeft) {
		return createObjectCoordinateLeftInsideCalcDimensions(objLeft, this.GRID.getCoordinates().createLeft());
	}

	private float createObjectCoordinateLeftInsideCalcDimensions(float objLeft, float gridLeft) {
		return objLeft + gridLeft;
	}

	public float createObjectCoordinateRightInsideCalcDimensions(float objRight) {
		return createObjectCoordinateRightInsideCalcDimensions(objRight, this.GRID.getCoordinates().createLeft());
	}

	private float createObjectCoordinateRightInsideCalcDimensions(float objRight, float gridLeft) {
		return objRight + gridLeft;
	}
	
	public float createObjectCoordinateTopInsideCalcDimensions(float objTop) {
		return createObjectCoordinateTopInsideCalcDimensions(objTop, this.GRID.getCoordinates().createTop());
	}

	private float createObjectCoordinateTopInsideCalcDimensions(float objTop, float gridTop) {
		return objTop + gridTop;
	}

	public float createObjectCoordinateBottomInsideCalcDimensions(float objBottom) {
		return createObjectCoordinateBottomInsideCalcDimensions(objBottom, this.GRID.getCoordinates().createTop());
	}

	private float createObjectCoordinateBottomInsideCalcDimensions(float objBottom, float gridTop) {
		return objBottom + gridTop;
	}
	
	public float createObjectCoordinateLeftInsideGrid(float objLeft) {
		return createObjectCoordinateLeftInsideGrid(objLeft, this.GRID.getCoordinates().createLeft());
	}

	private float createObjectCoordinateLeftInsideGrid(float objLeft, float gridLeft) {
		return objLeft - gridLeft;
	}
	
	public float createObjectCoordinateRightInsideGrid(float objRight) {
		return createObjectCoordinateRightInsideGrid(objRight, this.GRID.getCoordinates().createLeft());
	}

	private float createObjectCoordinateRightInsideGrid(float objRight, float gridLeft) {
		return objRight - gridLeft;
	}
	
	public float createObjectCoordinateTopInsideGrid(float objTop) {
		return createObjectCoordinateTopInsideGrid(objTop, this.GRID.getCoordinates().createTop());
	}

	private float createObjectCoordinateTopInsideGrid(float objTop, float gridTop) {
		return objTop - gridTop;
	}
	
	public float createObjectCoordinateBottomInsideGrid(float objBottom) {
		return createObjectCoordinateBottomInsideGrid(objBottom, this.GRID.getCoordinates().createTop());
	}

	private float createObjectCoordinateBottomInsideGrid(float objBottom, float gridTop) {
		return objBottom - gridTop;
	}
	
	//====================
	// end region create object coordinate
	//====================

	//====================
	// region is object enter or exit
	//====================
	
	private boolean isObjectEnterLeftIntoCenterScreen(
		ObjectMovable objectMovable, 
		float objectMovableLeftBeforeCreateCoordinates,
		float coordinateLeftPlus
	) {
		Coordinates objectMovableCoordinates = objectMovable.getCoordinates();
		Dimensions objectMovableDimensions = objectMovableCoordinates.getDimensions();
		
		float objectMovableWidthHalf = objectMovableDimensions.getWidth() / 2;
		float screenWidthHalf = this.SCREEN_DIMENSIONS.getWidth() / 2;
		float objectMovableLimitLeft = screenWidthHalf - objectMovableWidthHalf;
		float objectLeft = objectMovableCoordinates.createLeft();
		
		return objectLeft + coordinateLeftPlus < objectMovableLimitLeft
		&& objectMovableLeftBeforeCreateCoordinates + coordinateLeftPlus >= objectMovableLimitLeft;
	}
	
	private boolean isObjectEnterRightIntoCenterScreen(
		ObjectMovable objectMovable, 
		float objectMovableRightBeforeCreateCoordinates,
		float coordinateLeftPlus
	) {
		Coordinates objectMovableCoordinates = objectMovable.getCoordinates();
		Dimensions objectMovableDimensions = objectMovableCoordinates.getDimensions();
		
		float objectMovableWidthHalf = objectMovableDimensions.getWidth() / 2;
		float screenWidthHalf = this.SCREEN_DIMENSIONS.getWidth() / 2;
		float objectMovableLimitRight = screenWidthHalf + objectMovableWidthHalf;
		float objectRight = objectMovableCoordinates.createRight();
		
		return objectRight + coordinateLeftPlus > objectMovableLimitRight 
		&& objectMovableRightBeforeCreateCoordinates + coordinateLeftPlus <= objectMovableLimitRight;
	}
	
	private boolean isObjectEnterTopIntoCenterScreen(
		ObjectMovable objectMovable, 
		float objectMovableTopBeforeCreateCoordinates,
		float coordinateTopPlus
	) {
		Coordinates objectMovableCoordinates = objectMovable.getCoordinates();
		Dimensions objectMovableDimensions = objectMovableCoordinates.getDimensions();
		float objectMovableHeightHalf = objectMovableDimensions.getHeight() / 2;
		float screenHeightHalf = this.SCREEN_DIMENSIONS.getHeight() / 2;
		float objectMovableLimitTop = screenHeightHalf - objectMovableHeightHalf;
		float objectTop = objectMovableCoordinates.createTop();
		
		return objectTop + coordinateTopPlus < objectMovableLimitTop
		&& objectMovableTopBeforeCreateCoordinates + coordinateTopPlus >= objectMovableLimitTop;
	}
	
	private boolean isObjectEnterBottomIntoCenterScreen(
		ObjectMovable objectMovable, 
		float objectMovableBottomBeforeCreateCoordinates,
		float coordinateTopPlus
	) {
		Coordinates objectMovableCoordinates = objectMovable.getCoordinates();
		Dimensions objectMovableDimensions = objectMovableCoordinates.getDimensions();
		float objectMovableHeightHalf = objectMovableDimensions.getHeight() / 2;
		float screenHeightHalf = this.SCREEN_DIMENSIONS.getHeight() / 2;
		float objectMovableLimitBottom = screenHeightHalf + objectMovableHeightHalf;
		float objectBottom = objectMovableCoordinates.createBottom();
		
		return objectBottom + coordinateTopPlus > objectMovableLimitBottom 
		&& objectMovableBottomBeforeCreateCoordinates + coordinateTopPlus <= objectMovableLimitBottom;
	}
	
	public boolean isObjectLeftIntoScreen(float objLeftInsideCalcDimensions, int screenWidth) {
		return objLeftInsideCalcDimensions >= 0 && objLeftInsideCalcDimensions <= screenWidth;
	}
	
	public boolean isObjectRightIntoScreen(float objRightInsideCalcDimensions, int screenWidth) {
		return objRightInsideCalcDimensions >= 0 && objRightInsideCalcDimensions <= screenWidth;
	}
	
	public boolean isObjectTopIntoScreen(float objTopInsideCalcDimensions, int screenHeight) {
		return objTopInsideCalcDimensions >= 0 && objTopInsideCalcDimensions <= screenHeight;
	}
	
	public boolean isObjectBottomIntoScreen(float objBottomInsideCalcDimensions, int screenHeight) {
		return objBottomInsideCalcDimensions >= 0 && objBottomInsideCalcDimensions <= screenHeight;
	}
	
	//====================
	// end region is object enter or exit
	//====================
	
	//====================
	// region is object inside
	//====================
	
	/**
	 * Indicate if an {@code Object} is inside the {@code CALC_DIMENSIONS}.
	 * 
	 * Calculate if the {@code Object} is in collision with the {@code CALC_DIMENSIONS}. If {@code true}, it means at least one corner of the {@code Object} is inside the {@code CALC_DIMENSIONS}.
	 * 
	 * @param obj the {@code Object} to calculate.
	 * @return {@code true} if the {@code Object} is inside the {@code CALC_DIMENSIONS}.
	 */
	public boolean isObjectInsideCalcDimensions(Object obj) {
		Coordinates objCoordinates = obj.getCoordinates();
		Coordinates gridCoordinates = this.GRID.getCoordinates();
		LayoutIndexEnum objLayoutIndex = objCoordinates.getLayoutIndex();
		float coordinateLeftPlusSelf = (isEquals(objLayoutIndex, LayoutIndexEnum.Objects)) ? gridCoordinates.createLeft() : 0 ;
		float coordinateTopPlusSelf = (isEquals(objLayoutIndex, LayoutIndexEnum.Objects)) ? gridCoordinates.createTop() : 0 ;
		return obj.isCollisionToObject(
			this.CALC_DIMENSIONS, 
			coordinateLeftPlusSelf, 
			coordinateTopPlusSelf, 
			0, 
			0
		);
	}
	
	/**
	 * Indicate if an {@code Object} is inside the {@code SCREEN}.
	 * 
	 * Calculate if the {@code Object} is in collision with the {@code SCREEN}. If {@code true}, this means at least one corner of the {@code Object} is inside the {@code SCREEN}.
	 * 
	 * @param obj the {@code Object} to calculate.
	 * @return {@code true} if the {@code Object} is inside the {@code SCREEN}.
	 */
	public boolean isObjectInsideScreen(Object obj) {
		Coordinates objCoordinates = obj.getCoordinates();
		Coordinates gridCoordinates = this.GRID.getCoordinates();
		LayoutIndexEnum objLayoutIndex = objCoordinates.getLayoutIndex();
		float coordinateLeftPlusSelf = (isEquals(objLayoutIndex, LayoutIndexEnum.Objects)) ? gridCoordinates.createLeft() : 0 ;
		float coordinateTopPlusSelf = (isEquals(objLayoutIndex, LayoutIndexEnum.Objects)) ? gridCoordinates.createTop() : 0 ;
		return obj.isCollisionToObject(
			this.SCREEN, 
			coordinateLeftPlusSelf, 
			coordinateTopPlusSelf, 
			0, 
			0
		);
	}
	
	//====================
	// end region is object inside
	//====================
	
	//====================
	// region abstract methods (sync events)
	//====================
	
	protected abstract void eventEnterIntoCalcDimensions(Object obj);
	
	protected abstract void eventExitFromCalcDimensions(Object obj);
	
	protected abstract void eventEnterIntoScreen(Object obj, boolean isEnterLeftTop45, boolean isEnterRightTop45, boolean isEnterLeftBottom45, boolean isEnterRightBottom45);
	
	protected abstract void eventExitFromScreen(Object obj, boolean isExitLeftTop45, boolean isExitRightTop45, boolean isExitLeftBottom45, boolean isExitRightBottom45);
	
	protected abstract void eventEnterIntoCenterScreen(ObjectMovable objectMovable, boolean isLeftEnter, boolean isRightEnter, boolean isTopEnter, boolean isBottomEnter);
	
	protected abstract void eventCollisionToBlock(ObjectMovable objectMovable);
	
	protected abstract void eventCollisionToObject(ObjectMovable objectMovable);
	
	protected abstract void eventEndCollisionToObject(ObjectMovable objectMovable);
	
	protected abstract void eventTimeToMoveLeftDoneX(ObjectMovable objectMovable);
	
	protected abstract void eventTimeToMoveLeftDoneY(ObjectMovable objectMovable);
	
	//====================
	// end region abstract methods (sync events)
	//====================
	
	/**
	 * Add a {@code Runnable} to the {@code runnables} list.
	 * 
	 * @param runnable the {@code Runnable} to add.
	 */
	protected void addRunnable(Runnable runnable) {
		synchronized (this.LOCK_RUNNABLES) {
			this.runnables.add(runnable);
		}
	}
	
}
