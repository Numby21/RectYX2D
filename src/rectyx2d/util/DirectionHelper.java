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
package rectyx2d.util;

import static rectyx2d.util.Objects.isEquals;
import static rectyx2d.util.Objects.isNotEquals;
import static rectyx2d.util.Objects.isEqualsBooleans;

/**
 *
 * @author Renaud LE CLERRE
 */
public class DirectionHelper {
	
	public static boolean isDirectionContainsX(DirectionEnum direction) {
		return isEqualsBooleans(isDirectionContainsLeft(direction), true)
		|| isEqualsBooleans(isDirectionContainsRight(direction), true);
	}
	
	public static boolean isDirectionContainsY(DirectionEnum direction) {
		return isEqualsBooleans(isDirectionContainsTop(direction), true)
		|| isEqualsBooleans(isDirectionContainsBottom(direction), true);
	}
	
	public static boolean isDirectionContainsSlowMoveX(DirectionEnum direction) {
		return isEquals(direction, DirectionEnum.TopLeft)
		|| isEquals(direction, DirectionEnum.TopRight)
		|| isEquals(direction, DirectionEnum.BottomLeft)
		|| isEquals(direction, DirectionEnum.BottomRight);
	}
	
	public static boolean isDirectionContainsSlowMoveY(DirectionEnum direction) {
		return isEquals(direction, DirectionEnum.LeftTop)
		|| isEquals(direction, DirectionEnum.RightTop)
		|| isEquals(direction, DirectionEnum.LeftBottom)
		|| isEquals(direction, DirectionEnum.RightBottom);
	}
	
	public static boolean isDirectionContainsLeft(DirectionEnum direction) {
		return isEquals(direction, DirectionEnum.Left)
		|| isEquals(direction, DirectionEnum.LeftTop)
		|| isEquals(direction, DirectionEnum.LeftTop45)
		|| isEquals(direction, DirectionEnum.TopLeft)
		|| isEquals(direction, DirectionEnum.LeftBottom)
		|| isEquals(direction, DirectionEnum.LeftBottom45)
		|| isEquals(direction, DirectionEnum.BottomLeft);
	}
	
	public static boolean isDirectionContainsRight(DirectionEnum direction) {
		return isEquals(direction, DirectionEnum.Right)
		|| isEquals(direction, DirectionEnum.RightTop)
		|| isEquals(direction, DirectionEnum.RightTop45)
		|| isEquals(direction, DirectionEnum.TopRight)
		|| isEquals(direction, DirectionEnum.RightBottom)
		|| isEquals(direction, DirectionEnum.RightBottom45)
		|| isEquals(direction, DirectionEnum.BottomRight);
	}
	
	public static boolean isDirectionContainsTop(DirectionEnum direction) {
		return isEquals(direction, DirectionEnum.Top)
		|| isEquals(direction, DirectionEnum.TopLeft)
		|| isEquals(direction, DirectionEnum.LeftTop45)
		|| isEquals(direction, DirectionEnum.LeftTop)
		|| isEquals(direction, DirectionEnum.TopRight)
		|| isEquals(direction, DirectionEnum.RightTop45)
		|| isEquals(direction, DirectionEnum.RightTop);
	}
	
	public static boolean isDirectionContainsBottom(DirectionEnum direction) {
		return isEquals(direction, DirectionEnum.Bottom)
		|| isEquals(direction, DirectionEnum.BottomLeft)
		|| isEquals(direction, DirectionEnum.LeftBottom45)
		|| isEquals(direction, DirectionEnum.LeftBottom)
		|| isEquals(direction, DirectionEnum.BottomRight)
		|| isEquals(direction, DirectionEnum.RightBottom45)
		|| isEquals(direction, DirectionEnum.RightBottom);
	}
	
	public static boolean isDirectionContains45(DirectionEnum direction) {
		return isEquals(direction, DirectionEnum.LeftTop45)
		|| isEquals(direction, DirectionEnum.RightTop45)
		|| isEquals(direction, DirectionEnum.LeftBottom45)
		|| isEquals(direction, DirectionEnum.RightBottom45);
	}
	
	public static DirectionEnum createDirectionInverted(DirectionEnum direction) {
		DirectionEnum directionInverted = null;
		if (isEquals(direction, DirectionEnum.Left))
			directionInverted = DirectionEnum.Right;
		else if (isEquals(direction, DirectionEnum.LeftTop))
			directionInverted = DirectionEnum.RightBottom;
		else if (isEquals(direction, DirectionEnum.LeftTop45))
			directionInverted = DirectionEnum.RightBottom45;
		else if (isEquals(direction, DirectionEnum.TopLeft))
			directionInverted = DirectionEnum.BottomRight;
		else if (isEquals(direction, DirectionEnum.Top))
			directionInverted = DirectionEnum.Bottom;
		else if (isEquals(direction, DirectionEnum.TopRight))
			directionInverted = DirectionEnum.BottomLeft;
		else if (isEquals(direction, DirectionEnum.RightTop45))
			directionInverted = DirectionEnum.LeftBottom45;
		else if (isEquals(direction, DirectionEnum.RightTop))
			directionInverted = DirectionEnum.LeftBottom;
		else if (isEquals(direction, DirectionEnum.Right))
			directionInverted = DirectionEnum.Left;
		else if (isEquals(direction, DirectionEnum.RightBottom))
			directionInverted = DirectionEnum.LeftTop;
		else if (isEquals(direction, DirectionEnum.RightBottom45))
			directionInverted = DirectionEnum.LeftTop45;
		else if (isEquals(direction, DirectionEnum.BottomRight))
			directionInverted = DirectionEnum.TopLeft;
		else if (isEquals(direction, DirectionEnum.Bottom))
			directionInverted = DirectionEnum.Top;
		else if (isEquals(direction, DirectionEnum.BottomLeft))
			directionInverted = DirectionEnum.TopRight;
		else if (isEquals(direction, DirectionEnum.LeftBottom45))
			directionInverted = DirectionEnum.RightTop45;
		else if (isEquals(direction, DirectionEnum.LeftBottom))
			directionInverted = DirectionEnum.RightTop;
		return directionInverted;
	}
	
	public static DirectionEnum createDirectionMerged(DirectionEnum direction0, DirectionEnum direction1) {
		DirectionEnum directionMerged = null;
		if (isEquals(direction0, DirectionEnum.Left)) {
			if (isEquals(direction1, DirectionEnum.Top))
				directionMerged = DirectionEnum.LeftTop45;
			else if (isEquals(direction1, DirectionEnum.Bottom))
				directionMerged = DirectionEnum.LeftBottom45;
		} else if (isEquals(direction0, DirectionEnum.Right)) {
			if (isEquals(direction1, DirectionEnum.Top))
				directionMerged = DirectionEnum.RightTop45;
			else if (isEquals(direction1, DirectionEnum.Bottom))
				directionMerged = DirectionEnum.RightBottom45;
		} else if (isEquals(direction0, DirectionEnum.Top)) {
			if (isEquals(direction1, DirectionEnum.Left))
				directionMerged = DirectionEnum.LeftTop45;
			else if (isEquals(direction1, DirectionEnum.Right))
				directionMerged = DirectionEnum.RightTop45;
		} else if (isEquals(direction0, DirectionEnum.Bottom)) {
			if (isEquals(direction1, DirectionEnum.Left))
				directionMerged = DirectionEnum.LeftBottom45;
			else if (isEquals(direction1, DirectionEnum.Right))
				directionMerged = DirectionEnum.RightBottom45;
		}
		return directionMerged;
	}
	
}
