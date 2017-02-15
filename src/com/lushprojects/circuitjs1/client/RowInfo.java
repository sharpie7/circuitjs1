/*    
    Copyright (C) Paul Falstad and Iain Sharp
    
    This file is part of CircuitJS1.

    CircuitJS1 is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 2 of the License, or
    (at your option) any later version.

    CircuitJS1 is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with CircuitJS1.  If not, see <http://www.gnu.org/licenses/>.
*/

package com.lushprojects.circuitjs1.client;

// info about each row/column of the matrix for simplification purposes
    class RowInfo {
	static final int ROW_NORMAL = 0;  // ordinary value
	static final int ROW_CONST  = 1;  // value is constant
	int type, mapCol, mapRow;
	double value;
	boolean rsChanges; // row's right side changes
	boolean lsChanges; // row's left side changes
	boolean dropRow;   // row is not needed in matrix
	RowInfo() { type = ROW_NORMAL; }
    }
