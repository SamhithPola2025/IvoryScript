package com.mainsrc.ivoryscript;

import java.util.List;

public class IvoryScriptString implements IvoryScriptCallable {
    private final String value;

    public IvoryScriptString(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public Object getProperty(String name) {
        switch (name) {
            case "length":
                return new IvoryScriptCallable() {
                    @Override
                    public int arity() {
                        return 0;
                    }

                    @Override
                    public Object call(Interpreter interpreter, List<Object> arguments) {
                        return (double) value.length();
                    }

                    @Override
                    public String toString() {
                        return "<native fn>";
                    }
                };
            case "substring":
                return new IvoryScriptCallable() {
                    @Override
                    public int arity() {
                        return 2;
                    }

                    @Override
                    public Object call(Interpreter interpreter, List<Object> arguments) {
                        if (!(arguments.get(0) instanceof Double) || !(arguments.get(1) instanceof Double)) {
                            throw new Interpreter.RuntimeError(null, "substring() requires two number arguments.");
                        }
                        int start = ((Double) arguments.get(0)).intValue();
                        int end = ((Double) arguments.get(1)).intValue();
                        if (start < 0 || end > value.length() || start > end) {
                            throw new Interpreter.RuntimeError(null, "Invalid substring indices.");
                        }
                        return value.substring(start, end);
                    }

                    @Override
                    public String toString() {
                        return "<native fn>";
                    }
                };
            case "toUpper":
                return new IvoryScriptCallable() {
                    @Override
                    public int arity() {
                        return 0;
                    }

                    @Override
                    public Object call(Interpreter interpreter, List<Object> arguments) {
                        return value.toUpperCase();
                    }

                    @Override
                    public String toString() {
                        return "<native fn>";
                    }
                };
            case "toLower":
                return new IvoryScriptCallable() {
                    @Override
                    public int arity() {
                        return 0;
                    }

                    @Override
                    public Object call(Interpreter interpreter, List<Object> arguments) {
                        return value.toLowerCase();
                    }

                    @Override
                    public String toString() {
                        return "<native fn>";
                    }
                };
            default:
                return null;
        }
    }

    @Override
    public int arity() {
        return 0;
    }

    @Override
    public Object call(Interpreter interpreter, List<Object> arguments) {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }
}

