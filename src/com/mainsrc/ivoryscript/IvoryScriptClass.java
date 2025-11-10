package com.mainsrc.ivoryscript;

import java.util.Map;
import java.util.List;

class IvoryScriptClass implements IvoryScriptCallable {
    final String name;
    final IvoryScriptClass superclass;
    private final Map<String, IvoryScriptFunction> methods;

    IvoryScriptClass(String name, IvoryScriptClass superclass, Map<String, IvoryScriptFunction> methods) {
        this.name = name;
        this.superclass = superclass;
        this.methods = methods;
    }

    public IvoryScriptFunction findMethod(String name) {
        if (methods.containsKey(name)) {
            return methods.get(name);
        }

        if (superclass != null) {
            return superclass.findMethod(name);
        }

        return null;
    }

    @Override
    public Object call(Interpreter interpreter, List<Object> arguments) {
        IvoryScriptInstance instance = new IvoryScriptInstance(this);

        IvoryScriptFunction initializer = findMethod("init");
        if (initializer != null) {
            initializer.bind(instance).call(interpreter, arguments);
        }

        return instance;
    }

    @Override
    public int arity() {
        IvoryScriptFunction initializer = findMethod("init");
        return initializer == null ? 0 : initializer.arity();
    }

    @Override
    public String toString() {
        return "<class " + name + ">";
    }
}