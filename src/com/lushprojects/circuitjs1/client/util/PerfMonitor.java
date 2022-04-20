package com.lushprojects.circuitjs1.client.util;

import java.util.*;

public class PerfMonitor {
    
    String rootCtxName;
    PerfEntry rootCtx;
    PerfEntry ctx;
    
    public PerfMonitor() {
    
    }

    public void startContext(String name) {
        PerfEntry newEntry = startNewEntry(ctx);
        if (ctx == null) {
            ctx = newEntry;
            if (rootCtx == null) {
                rootCtxName = name;
                rootCtx = ctx;
            }
        } else {
            if (ctx.AddChild(name, newEntry)) {
                ctx = newEntry;
            }
        }
    }
    
    public void stopContext() {
        if (ctx != null) {
            ctx.endTime = getTime();
            ctx.length = ctx.endTime - ctx.startTime;
            ctx = ctx.parent;
        }
    }
    
    private PerfEntry startNewEntry(PerfEntry parent) {
        PerfEntry newEntry = new PerfEntry(parent);
        newEntry.startTime = getTime();
        return newEntry;
    }
    
    public static StringBuilder buildString(PerfMonitor mon) {
        StringBuilder sb = new StringBuilder();
        buildStringInternal(sb, mon.rootCtxName, mon.rootCtx, 0);
        return sb;
    } 
    
    private static void buildStringInternal(StringBuilder sb, String name, PerfEntry entry, int depth) {
        for (int x = 0; x < depth; x++) {
            sb.append("-");
        }
        sb.append(name);
        sb.append(": ");
        sb.append(entry.length);
        sb.append("\n");
        Set<String> keys = entry.children.keySet();
        for (String key : keys){
            PerfEntry child = entry.children.get(key);
            buildStringInternal(sb, key, child, depth + 1);
        }
    }
    
    private static native float getTime() /*-{
        // https://stackoverflow.com/questions/6875625
        if (window.performance.now) {
            return window.performance.now();
        } else {
            if (window.performance.webkitNow) {
                return window.performance.webkitNow();
            } else {
                return new Date().getTime();
            }
        }
    }-*/;

    class PerfEntry {
    
        public PerfEntry parent;
        public HashMap<String, PerfEntry> children;
            
        public float startTime;
        public float endTime;
        public float length;
        
        public PerfEntry(PerfEntry p) {
            parent = p;
            children = new HashMap<String, PerfEntry>();
        }
        
        public boolean AddChild(String name, PerfEntry entry) {
            if (!children.containsKey(name)) {
                children.put(name, entry);
                return true;
            }
            return false;
        }
        
        public PerfEntry GetChild(String name) {
            if (children.containsKey(name)) {
                return children.get(name);
            }
            return null;
        }
    }
    
}
