package com.mainsrc.ivoryscript;

import java.util.List;

public class IvoryScriptNativeFunction implements IvoryScriptCallable {
    private final int arity;
    private final java.util.function.Function<List<Object>, Object> function;

    public IvoryScriptNativeFunction(int arity, java.util.function.Function<List<Object>, Object> function) {
        this.arity = arity;
        this.function = function;
    }

    @Override
    public int arity() {
        return arity;
    }

    @Override
    public Object call(Interpreter interpreter, List<Object> arguments) {
        return function.apply(arguments);
    }

    @Override
    public String toString() {
        return "<native fn>";
    }
}

