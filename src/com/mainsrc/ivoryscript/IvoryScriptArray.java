package com.mainsrc.ivoryscript;

import java.util.ArrayList;
import java.util.List;

public class IvoryScriptArray {
    private final List<Object> elements;

    public IvoryScriptArray(List<Object> elements) {
        this.elements = new ArrayList<>(elements);
    }

    public Object get(int index) {
        if (index < 0 || index >= elements.size()) {
            throw new Interpreter.RuntimeError(null, "Array index out of bounds.");
        }
        return elements.get(index);
    }

    public void set(int index, Object value) {
        if (index < 0 || index >= elements.size()) {
            throw new Interpreter.RuntimeError(null, "Array index out of bounds.");
        }
        elements.set(index, value);
    }

    public void add(Object value) {
        elements.add(value);
    }

    public int length() {
        return elements.size();
    }

    public List<Object> getElements() {
        return elements;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < elements.size(); i++) {
            if (i > 0) sb.append(", ");
            sb.append(Interpreter.stringify(elements.get(i)));
        }
        sb.append("]");
        return sb.toString();
    }
}

