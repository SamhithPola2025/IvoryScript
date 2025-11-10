package com.mainsrc.ivoryscript;

import java.util.HashMap;
import java.util.Map;

public class IvoryScriptInstance {
    private final IvoryScriptClass klass;
    private final Map<String, Object> fields = new HashMap<>();

    IvoryScriptInstance(IvoryScriptClass klass) {
        this.klass = klass;
    }

    Object get(Token name) {
        if (fields.containsKey(name.lexeme)) {
            return fields.get(name.lexeme);
        }
    
        IvoryScriptFunction method = klass.findMethod(name.lexeme);
        if (method != null) {
            return method.bind(this);
        }
    
        throw new Interpreter.RuntimeError(name, "Undefined property '" + name.lexeme + "'.");
    }

    void set(Token name, Object value) {
        fields.put(name.lexeme, value);
    }

    @Override
    public String toString() {
        return "<instance of " + klass.name + ">";
    }
}