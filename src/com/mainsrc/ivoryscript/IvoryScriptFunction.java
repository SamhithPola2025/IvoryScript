package com.mainsrc.ivoryscript;

import java.util.List;

public class IvoryScriptFunction implements IvoryScriptCallable {
    private final Stmt.Function declaration;
    private final Environment closure;

    public IvoryScriptFunction(Stmt.Function declaration, Environment closure) {
        this.declaration = declaration;
        this.closure = closure;
    }

    public IvoryScriptFunction(Stmt.Function declaration) {
        this.declaration = declaration;
        this.closure = null;
    }

    public IvoryScriptFunction bind(IvoryScriptInstance instance) {
        Environment environment = new Environment(closure);
        environment.define("this", instance);
        return new IvoryScriptFunction(declaration, environment);
    }

    @Override
    public int arity() {
        return declaration.params.size();
    }

    @Override
    public Object call(Interpreter interpreter, List<Object> arguments) {
        Environment environment = new Environment(closure);
        for (int i = 0; i < declaration.params.size(); i++) {
            environment.define(declaration.params.get(i).lexeme, arguments.get(i));
        }

        try {
            interpreter.executeBlock(declaration.body, environment);
        } catch (Return returnValue) {
            return returnValue.value;
        }
        return null;
    }

    @Override
    public String toString() {
        return "<fn " + declaration.name.lexeme + ">";
    }
}