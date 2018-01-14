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

import java.util.Vector;

class TextElm extends GraphicElm {
    String text;
    Vector<String> lines;
    int size;
    final int FLAG_CENTER = 1;
    final int FLAG_BAR = 2;
    final int FLAG_ESCAPE = 4;
    public TextElm(int xx, int yy) {
	super(xx, yy);
	text = "hello";
	lines = new Vector<String>();
	lines.add(text);
	size = 24;
    }
    public TextElm(int xa, int ya, int xb, int yb, int f,
		   StringTokenizer st) {
	super(xa, ya, xb, yb, f);
	size = new Integer(st.nextToken()).intValue();
	text = st.nextToken();
	if ((flags & FLAG_ESCAPE) == 0) {
	    // old-style dump before escape/unescape
	    while (st.hasMoreTokens())
		text += ' ' + st.nextToken();
	    text=text.replaceAll("%2[bB]", "+");
	} else {
	    // new-style dump
	    text = CustomLogicModel.unescape(text); 
	}
	split();
    }
    void split() {
	int i;
	lines = new Vector<String>();
	StringBuffer sb = new StringBuffer(text);
	for (i = 0; i < sb.length(); i++) {
	    char c = sb.charAt(i);
	    if (c == '\\') {
		sb.deleteCharAt(i);
		c = sb.charAt(i);
		if (c == 'n') {
		    lines.add(sb.substring(0, i));
		    sb.delete(0, i+1);
		    i = -1;
		    continue;
		}
	    }
	}
	lines.add(sb.toString());
    }
    String dump() {
	flags |= FLAG_ESCAPE;
	return super.dump() + " " + size + " " + CustomLogicModel.escape(text);
	//return super.dump() + " " + size + " " + text;
    }
    int getDumpType() { return 'x'; }
    void drag(int xx, int yy) {
	x = xx;
	y = yy;
	x2 = xx+16;
	y2 = yy;
    }
    void draw(Graphics g) {
	//Graphics2D g2 = (Graphics2D)g;
	//g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
	//	RenderingHints.VALUE_ANTIALIAS_ON);
	Font oldfont = g.getFont();
    g.setColor(needsHighlight() ? selectColor : lightGrayColor);
	Font f = new Font("SansSerif", 0, size);
	g.setFont(f);
//	FontMetrics fm = g.getFontMetrics();
	int i;
	int maxw = -1;
	for (i = 0; i != lines.size(); i++) {
//	    int w = fm.stringWidth((String) (lines.elementAt(i)));
		int w= (int)g.context.measureText((String) (lines.elementAt(i))).getWidth();
	    if (w > maxw)
		maxw = w;
	}
	int cury = y;
	setBbox(x, y, x, y);
	for (i = 0; i != lines.size(); i++) {
	    String s = (String) (lines.elementAt(i));
	    s = CirSim.LS(s);
	    int sw=(int)g.context.measureText(s).getWidth();
	    if ((flags & FLAG_CENTER) != 0)
		x = (g.context.getCanvas().getWidth()-sw)/2;
	    g.drawString(s, x, cury);
	    if ((flags & FLAG_BAR) != 0) {
		int by = cury-g.currentFontSize;
		g.drawLine(x, by, x+sw-1, by);
	    }
	    adjustBbox(x, cury-g.currentFontSize,
		       x+sw, cury+3);
	    cury += g.currentFontSize+3;
	}
	x2 = boundingBox.x + boundingBox.width;
	y2 = boundingBox.y + boundingBox.height;
	g.setFont(oldfont);
    }
    public EditInfo getEditInfo(int n) {
	if (n == 0) {
	    EditInfo ei = new EditInfo("Text", 0, -1, -1);
	    ei.text = text;
	    return ei;
	}
	if (n == 1)
	    return new EditInfo("Size", size, 5, 100);
	if (n == 2) {
	    EditInfo ei = new EditInfo("", 0, -1, -1);
	    ei.checkbox =
		new Checkbox("Center", (flags & FLAG_CENTER) != 0);
	    return ei;
	}
	if (n == 3) {
	    EditInfo ei = new EditInfo("", 0, -1, -1);
	    ei.checkbox =
		new Checkbox("Draw Bar On Top", (flags & FLAG_BAR) != 0);
	    return ei;
	}
	return null;
    }
    public void setEditValue(int n, EditInfo ei) {
	if (n == 0) {
	    text = ei.textf.getText();
	    split();
	}
	if (n == 1)
	    size = (int) ei.value;
	if (n == 3) {
	    if (ei.checkbox.getState())
		flags |= FLAG_BAR;
	    else
		flags &= ~FLAG_BAR;
	}
	if (n == 2) {
	    if (ei.checkbox.getState())
		flags |= FLAG_CENTER;
	    else
		flags &= ~FLAG_CENTER;
	}
    }
    boolean isCenteredText() { return (flags & FLAG_CENTER) != 0; }
    void getInfo(String arr[]) {
	arr[0] = text;
    }
    @Override
    int getShortcut() { return 't'; }
}

