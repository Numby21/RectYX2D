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

/**
 *
 * @author Renaud LE CLERRE
 */
public class Objects {
	
	public static boolean isEquals(java.lang.Object obj0, java.lang.Object obj1) {
		return java.util.Objects.equals(obj0, obj1);
	}
	
	public static boolean isNotEquals(java.lang.Object obj0, java.lang.Object obj1) {
		return !isEquals(obj0, obj1);
	}
	
	public static boolean isEqualsBooleans(boolean bool0, boolean bool1) {
		return bool0 == bool1;
	}
	
	public static boolean isNotEqualsBooleans(boolean bool0, boolean bool1) {
		return !isEqualsBooleans(bool0, bool1);
	}
	
	public static boolean isEqualsInts(int int0, int int1) {
		return int0 == int1;
	}
	
	public static boolean isNotEqualsInts(int int0, int int1) {
		return !isEqualsInts(int0, int1);
	}
	
	public static boolean isEqualsLongs(long long0, long long1) {
		return long0 == long1;
	}
	
	public static boolean isNotEqualsLongs(long long0, long long1) {
		return !isEqualsLongs(long0, long1);
	}
	
	public static boolean isEqualsFloats(float float0, float float1) {
		return float0 == float1;
	}
	
	public static boolean isNotEqualsFloats(float float0, float float1) {
		return !isEqualsFloats(float0, float1);
	}
	
}
