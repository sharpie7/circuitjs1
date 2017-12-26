//	  Extracted from file
//    Copyright 1995-2006 Sun Microsystems, Inc.  All Rights Reserved
//
//    This program is free software: you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation, either version 2 of the License, or
//    (at your option) any later version.
//
//    This program is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU General Public License for more details.
//
//    You should have received a copy of the GNU General Public License
//    along with this program.  If not, see <http://www.gnu.org/licenses/>.

package com.lushprojects.circuitjs1.client;

// via http://grepcode.com/file/repository.grepcode.com/java/root/jdk/openjdk/6-b14/java/awt/Polygon.java

class Polygon {
//	ArrayList<Point> poly;
	
	private static final int MIN_LENGTH = 4;
	public int npoints;
	public int xpoints[];
	public int ypoints[];
	
	
	public Polygon(){
//		poly = new ArrayList<Point>();
        xpoints = new int[MIN_LENGTH];
        ypoints = new int[MIN_LENGTH];
	}
	
//	public void addPoint(int x, int y){
//		poly.add(new Point(x,y));
//	}
	
    public void addPoint(int x, int y) {
        if (npoints >= xpoints.length || npoints >= ypoints.length) {
            int newLength = npoints * 2;
            // Make sure that newLength will be greater than MIN_LENGTH and
            // aligned to the power of 2
            if (newLength < MIN_LENGTH) {
                newLength = MIN_LENGTH;
            } else if ((newLength & (newLength - 1)) != 0) {
                newLength = Integer.highestOneBit(newLength);
            }

            xpoints = expand(xpoints, newLength);
            ypoints = expand(ypoints, newLength);
        }
        xpoints[npoints] = x;
        ypoints[npoints] = y;
        npoints++;
//        if (bounds != null) {
//            updateBounds(x, y);
//        }
    }
    
    private int[] expand(int[] in, int newlen) {
    	int[] out=new int[newlen];
    	for(int i=0; i<in.length; i++)
    		out[i]=in[i];
    	return out;
    }
	
}