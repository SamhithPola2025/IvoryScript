package com.mainsrc.ivoryscript;

import java.util.List;

public interface IvoryScriptCallable {
    int arity();
    Object call(Interpreter interpreter, List<Object> arguments);
}
