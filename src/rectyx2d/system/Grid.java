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
import java.util.List;
import rectyx2d.util.DirectionEnum;
import static rectyx2d.util.Objects.isEquals;
import static rectyx2d.util.Objects.isNotEquals;

/**
 * Contains majority of the engine {@code Object} and {@code ObjectMovable}.
 * It is designed to move like that all contained {@code Object} and {@code ObjectMovable} can move with it by easy calculations.
 * 
 * @author Renaud LE CLERRE
 */
public class Grid extends ObjectMovable {
	
	private List<ObjectMovable> objectsMovables;
	private List<Object> objects;
	
	public Grid(
		int id,
		String name,
		int customType,
		Coordinates coordinates,
		DirectionEnum buildDirection, 
		List<ObjectMovable> objectsMovables,
		List<Object> objects
	) {
		super(
			id, 
			name, 
			customType, 
			coordinates, 
			buildDirection, 
			true,
			false,
			true,
			false,
			true,
			false,
			false
		);
		this.objectsMovables = objectsMovables;
		this.objects = objects;
	}
	
	public List<ObjectMovable> getObjectsMovables() {
		return objectsMovables;
	}

	public void setObjectsMovables(List<ObjectMovable> objectsMovables) {
		this.objectsMovables = objectsMovables;
	}

	public List<Object> getObjects() {
		return objects;
	}
	
	public void setObjects(List<Object> objects) {
		this.objects = objects;
	}
	
	//====================
	// region methods
	//====================

	public List<Object> findAllObjects() {
		List<Object> objects = Collections.synchronizedList(new ArrayList<>());
		if (isNotEquals(this.objects, null))
			objects.addAll(this.objects);
		if (isNotEquals(this.objectsMovables, null))
			objects.addAll(this.objectsMovables);
		return objects;
	}
	
	public Object findObjectByCoordinatesCenter(int x, int y, List<Integer> customsTypes) {
		Object objExisting = null;
		for (Object obj : this.objects) {
			Coordinates objCoordinates = obj.getCoordinates();
			if (objCoordinates.createLeft() <= x && objCoordinates.createRight() >= x
			&& objCoordinates.createTop() <= y && objCoordinates.createBottom() >= y) {
				if (isEquals(customsTypes, null) || isNotEquals(customsTypes, null) && customsTypes.contains(obj.getCustomType())) {
					objExisting = obj;
					break;
				}
			}
		}
		return objExisting;
	}
	
	@Override
	void clear() {
		if (isNotEquals(this.objectsMovables, null)) {
			for (ObjectMovable objectMovable : this.objectsMovables)
				objectMovable.clear();
		}
		super.clear();
	}

	//====================
	// end region methods
	//====================
	
}
