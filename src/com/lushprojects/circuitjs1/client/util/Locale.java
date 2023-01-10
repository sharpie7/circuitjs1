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

package com.lushprojects.circuitjs1.client.util;

import java.util.HashMap;

import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;

public class Locale {

    public static HashMap<String, String> localizationMap;

    public static String ohmString = "\u03a9";
    public static String muString = "\u03bc";

    public static String LS(String s) {
        if (s == null)
            return null;

        if (s.length() == 0) { // empty strings trip up the 'if (ix != s.length() - 1)' below
            return s;
        }

        String sm = localizationMap.get(s);
        if (sm != null)
            return sm;

        // use trailing ~ to differentiate strings that are the same in English but need
        // different translations.
        // remove these if there's no translation.
        int ix = s.indexOf('~');
        if (ix != s.length() - 1)
            return s;

        s = s.substring(0, ix);
        sm = localizationMap.get(s);
        if (sm != null)
            return sm;

        return s;
    }

    public static SafeHtml LSHTML(String s) {
        return SafeHtmlUtils.fromTrustedString(LS(s));
    }

}
