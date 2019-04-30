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

import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;

public class ScopePopupMenu {
    
    private MenuBar m;
    private MenuItem removeScopeItem;
    private CheckboxMenuItem maxScaleItem;
    private MenuItem stackItem;
    private MenuItem unstackItem;
    private MenuItem combineItem;
    private MenuItem removePlotItem;
    private MenuItem resetItem;
    private MenuItem propertiesItem;
    private MenuItem dockItem;
    private MenuItem undockItem;
    
    ScopePopupMenu() {
	 m = new MenuBar(true);
	 m.addItem(removeScopeItem = new CheckboxAlignedMenuItem(CirSim.LS("Remove Scope"),new MyCommand("scopepop", "remove")));
	 m.addItem(dockItem = new CheckboxAlignedMenuItem(CirSim.LS("Dock Scope"),new MyCommand("scopepop", "dock")));
	 m.addItem(undockItem = new CheckboxAlignedMenuItem(CirSim.LS("Undock Scope"),new MyCommand("scopepop", "undock")));
	 m.addItem(maxScaleItem = new CheckboxMenuItem(CirSim.LS("Max Scale"), new MyCommand("scopepop", "maxscale")));
	 m.addItem(stackItem = new CheckboxAlignedMenuItem(CirSim.LS("Stack"), new MyCommand("scopepop", "stack")));
	 m.addItem(unstackItem = new CheckboxAlignedMenuItem(CirSim.LS("Unstack"), new MyCommand("scopepop", "unstack")));
	 m.addItem(combineItem = new CheckboxAlignedMenuItem(CirSim.LS("Combine"), new MyCommand("scopepop", "combine")));
	 m.addItem(removePlotItem = new CheckboxAlignedMenuItem(CirSim.LS("Remove Plot"),new MyCommand("scopepop", "removeplot")));
	 m.addItem(resetItem = new CheckboxAlignedMenuItem(CirSim.LS("Reset"), new MyCommand("scopepop", "reset")));
	 m.addItem(propertiesItem = new CheckboxAlignedMenuItem(CirSim.LS("Properties..."), new MyCommand("scopepop", "properties")));
    }
    
    void doScopePopupChecks( boolean floating, Scope s) {
	maxScaleItem.setState(s.maxScale);
	stackItem.setVisible(!floating);
	unstackItem.setVisible(!floating);
	combineItem.setVisible(!floating);
	dockItem.setVisible(floating);
	undockItem.setVisible(!floating);
    }
    
    
    public MenuBar getMenuBar() {
	return m;
    }
}
