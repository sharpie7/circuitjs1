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

public class Color
{
    public final static Color white = new Color(255, 255, 255);
    public final static Color lightGray = new Color(192, 192, 192);
    public final static Color gray = new Color(128, 128, 128);
    public final static Color GRAY = new Color(128, 128, 128);
    public final static Color dark_gray = new Color(64, 64, 64);
    public final static Color darkGray = new Color(64, 64, 64);
    public final static Color black = new Color(0, 0, 0);
    public final static Color red = new Color(255, 0, 0);
    public final static Color pink = new Color(255, 175, 175);
    public final static Color orange = new Color(255, 200, 0);
    public final static Color yellow = new Color(255, 255, 0);
    public final static Color green = new Color(0, 255, 0);
    public final static Color magenta = new Color(255, 0, 255);
    public final static Color cyan = new Color(0, 255, 255);
    public final static Color blue = new Color(0, 0, 255);
    public static final Color NONE = new Color("");
    
    private int r, g, b;
    
    // only for special cases, like no color, or maybe named colors
    private String colorText = null;

    public Color (String colorText) {
        this.colorText = colorText;
        if (colorText.startsWith("#") && colorText.length() == 7) {
            String rs = colorText.substring(1, 3);
            String gs = colorText.substring(3, 5);
            String bs = colorText.substring(5, 7);
            r = Integer.parseInt(rs, 16);
            g = Integer.parseInt(gs, 16);
            b = Integer.parseInt(bs, 16);
        }
    }

    // create mixture of c1 and c2
    public Color (Color c1, Color c2, double mix) {
	double m0 = 1-mix;
	this.r = (int) (c1.r*m0 + c2.r*mix);
	this.g = (int) (c1.g*m0 + c2.g*mix);
	this.b = (int) (c1.b*m0 + c2.b*mix);
    }
    
    public Color (int r, int g, int b)
    {
        this.r = r;
        this.g = g;
        this.b = b;
    }

    public int getRed ()
    {
        return r;
    }

    public int getGreen ()
    {
        return g;
    }

    public int getBlue ()
    {
        return b;
    }

    public String getHexValue ()
    {
        if (colorText != null) {
            return colorText;
        }

        return "#"
            + pad(Integer.toHexString(r))
            + pad(Integer.toHexString(g))
            + pad(Integer.toHexString(b));
    }

    private String pad (String in)
    {
        if (in.length() == 0) {
            return "00";
        }
        if (in.length() == 1) {
            return "0" + in;
        }
        return in;
    }

    public String toString ()
    {
        if (colorText != null) {
            return colorText;
        }
        return "red=" + r + ", green=" + g + ", blue=" + b;
    }
}