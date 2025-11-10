package com.mainsrc.ivoryscript;

import java.util.HashMap;
import java.util.Map;

public class IvoryScriptDictionary {
    private final Map<String, Object> entries;

    public IvoryScriptDictionary() {
        this.entries = new HashMap<>();
    }

    public Object get(String key) {
        return entries.get(key);
    }

    public void set(String key, Object value) {
        entries.put(key, value);
    }

    public boolean containsKey(String key) {
        return entries.containsKey(key);
    }

    public Map<String, Object> getEntries() {
        return entries;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("{");
        boolean first = true;
        for (Map.Entry<String, Object> entry : entries.entrySet()) {
            if (!first) sb.append(", ");
            first = false;
            sb.append("\"").append(entry.getKey()).append("\": ");
            sb.append(Interpreter.stringify(entry.getValue()));
        }
        sb.append("}");
        return sb.toString();
    }
}

