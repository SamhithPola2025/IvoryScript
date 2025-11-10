package com.mainsrc.ivoryscript;

import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.InputStreamReader;

class Interpreter implements Expr.Visitor<Object>, Stmt.Visitor<Void> {

    static class RuntimeError extends RuntimeException {
        final Token token;

        RuntimeError(Token token, String message) {
            super(message);
            this.token = token;
        }
    }

    @Override
    public Void visitBreakStmt(Stmt.Break stmt) {
        throw new BreakException();
    }
    
    @Override
    public Void visitForStmt(Stmt.For stmt) {
        if (stmt.initializer != null) {
            execute(stmt.initializer);
        }

        try {
            while (stmt.condition == null || isTruthy(evaluate(stmt.condition))) {
                execute(stmt.body);
                if (stmt.increment != null) evaluate(stmt.increment);
            }
        } catch (BreakException e) {
        }

        return null;
    }
    
    @Override
    public Void visitCaseStmt(Stmt.Case stmt) {
        return null;
    }
    
    @Override
    public Void visitDefaultStmt(Stmt.Default stmt) {
        return null;
    }
    
    @Override
    public Void visitSwitchStmt(Stmt.Switch stmt) {
        Object conditionValue = evaluate(stmt.condition);
        boolean matched = false;

        try {
            for (Stmt.Case caseStmt : stmt.cases) {
                if (matched || isEqual(conditionValue, evaluate(caseStmt.value))) {
                    matched = true;
                    executeStmtBody(caseStmt.body);
                }
            }

            if (!matched && stmt.defaultCase != null) {
                executeStmtBody(stmt.defaultCase.body);
            }
        } catch (BreakException e) {
        }

        return null;
    }

    public Environment environment = new Environment();

    public Interpreter() {
        defineGlobals();
    }

    private void defineGlobals() {
        environment.define("input", new IvoryScriptNativeFunction(0, args -> {
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
                return reader.readLine();
            } catch (Exception e) {
                throw new RuntimeError(null, "Error reading input: " + e.getMessage());
            }
        }));

        environment.define("length", new IvoryScriptNativeFunction(1, args -> {
            Object value = args.get(0);
            if (value instanceof String) {
                return (double) ((String) value).length();
            } else if (value instanceof IvoryScriptArray) {
                return (double) ((IvoryScriptArray) value).length();
            } else if (value instanceof IvoryScriptDictionary) {
                return (double) ((IvoryScriptDictionary) value).getEntries().size();
            }
            throw new RuntimeError(null, "length() can only be called on strings, arrays, or dictionaries.");
        }));

        environment.define("type", new IvoryScriptNativeFunction(1, args -> {
            Object value = args.get(0);
            if (value == null) return "nil";
            if (value instanceof Double) return "number";
            if (value instanceof String) return "string";
            if (value instanceof Boolean) return "boolean";
            if (value instanceof IvoryScriptArray) return "array";
            if (value instanceof IvoryScriptDictionary) return "dictionary";
            if (value instanceof IvoryScriptFunction) return "function";
            if (value instanceof IvoryScriptClass) return "class";
            if (value instanceof IvoryScriptInstance) return "instance";
            return "unknown";
        }));

        environment.define("toString", new IvoryScriptNativeFunction(1, args -> {
            return stringify(args.get(0));
        }));
    }

    @Override
    public Object visitLiteralExpr(Expr.Literal expr) {
        return expr.value;
    }

    @Override
    public Object visitGroupingExpr(Expr.Grouping expr) {
        return evaluate(expr.expression);
    }

    private Object evaluate(Expr expr) {
        return expr.accept(this);
    }

    @Override
    public Object visitUnaryExpr(Expr.Unary expr) {
        Object right = evaluate(expr.right);

        switch (expr.operator.type) {
            case BANG:
                return !isTruthy(right);
            case MINUS:
                if (!(right instanceof Double)) {
                    throw new RuntimeError(expr.operator, "Operand must be a number.");
                }
                return -(double) right;
            default:
                throw new RuntimeError(expr.operator, "Unknown unary operator.");
        }
    }

    private boolean isTruthy(Object object) {
        if (object == null) return false;
        if (object instanceof Boolean) return (boolean) object;
        return true;
    }

    @Override
    public Object visitBinaryExpr(Expr.Binary expr) {
        Object left = evaluate(expr.left);
        Object right = evaluate(expr.right);

        switch (expr.operator.type) {
            case MINUS:
                checkNumberOperands(expr.operator, left, right);
                return (double) left - (double) right;
            case PLUS:
                if (left instanceof Double && right instanceof Double) {
                    return (double) left + (double) right;
                }
                if (left instanceof String || right instanceof String) {
                    return left.toString() + right.toString();
                }
                throw new RuntimeError(expr.operator, "Operands must be two numbers or at least one string.");
            case SLASH:
                checkNumberOperands(expr.operator, left, right);
                if ((double) right == 0) {
                    throw new RuntimeError(expr.operator, "Division by zero.");
                }
                return (double) left / (double) right;
            case STAR:
                checkNumberOperands(expr.operator, left, right);
                return (double) left * (double) right;
            case BANG_EQUAL:
                return !isEqual(left, right);
            case EQUAL_EQUAL:
                return isEqual(left, right);
            default:
                throw new RuntimeError(expr.operator, "Unknown binary operator.");
        }
    }

    private boolean isEqual(Object a, Object b) {
        if (a == null && b == null) return true;
        if (a == null) return false;
        return a.equals(b);
    }

    private void checkNumberOperands(Token operator, Object left, Object right) {
        if (left instanceof Double && right instanceof Double) return;
        throw new RuntimeError(operator, "Operands must be numbers.");
    }

    private void execute(Stmt stmt) {
        stmt.accept(this);
    }

    private void executeStmtBody(Object body) {
        if (body instanceof Stmt) {
            execute((Stmt) body);
        } else if (body instanceof List) {
            executeBlock((List<Stmt>) body, new Environment(environment));
        } else {
            throw new RuntimeError(null, "Invalid statement body in switch/case.");
        }
    }

    @Override
    public Void visitPrintStmt(Stmt.Print stmt) {
        Object value = evaluate(stmt.expression);
        System.out.println(stringify(value));
        return null;
    }

    static String stringify(Object object) {
        if (object == null) return "nil";

        if (object instanceof Double) {
            String text = object.toString();
            if (text.endsWith(".0")) text = text.substring(0, text.length() - 2);
            return text;
        }

        return object.toString();
    }

    @Override
    public Void visitVarStmt(Stmt.Var stmt) {
        Object value = null;
        if (stmt.initializer != null) value = evaluate(stmt.initializer);
        environment.define(stmt.name.lexeme, value);
        return null;
    }

    @Override
    public Void visitBlockStmt(Stmt.Block stmt) {
        executeBlock(stmt.statements, new Environment(environment));
        return null;
    }

    public void executeBlock(List<Stmt> statements, Environment environment) {
        Environment previous = this.environment;
        try {
            this.environment = environment;
            for (Stmt statement : statements) {
                execute(statement);
            }
        } finally {
            this.environment = previous;
        }
    }

    @Override
    public Void visitIfStmt(Stmt.If stmt) {
        if (isTruthy(evaluate(stmt.condition))) {
            execute(stmt.thenBranch);
        } else if (stmt.elseBranch != null) {
            execute(stmt.elseBranch);
        }
        return null;
    }

    @Override
    public Void visitWhileStmt(Stmt.While stmt) {
        try {
            while (isTruthy(evaluate(stmt.condition))) {
                execute(stmt.body);
            }
        } catch (BreakException e) {
        }
        return null;
    }

    @Override
    public Object visitAssignExpr(Expr.Assign expr) {
        Object value = evaluate(expr.value);
        environment.assign(expr.name, value);
        return value;
    }

    @Override
    public Object visitVariableExpr(Expr.Variable expr) {
        return environment.get(expr.name);
    }

    @Override
    public Void visitExpressionStmt(Stmt.Expression stmt) {
        evaluate(stmt.expression);
        return null;
    }

    private void reportRuntimeError(RuntimeError error) {
        System.err.println("[line " + error.token.line + "] RuntimeError: " + error.getMessage());
    }

    void interpret(List<Stmt> statements) {
        try {
            for (Stmt statement : statements) {
                execute(statement);
            }
        } catch (RuntimeError error) {
            reportRuntimeError(error);
        }
    }

    @Override
    public Object visitCallExpr(Expr.Call expr) {
        Object callee = evaluate(expr.callee);
        List<Object> arguments = new ArrayList<>();
        for (Expr argument : expr.arguments) {
            arguments.add(evaluate(argument));
        }
        if (!(callee instanceof IvoryScriptCallable)) {
            throw new RuntimeError(expr.paren, "Can only call functions and classes.");
        }
        IvoryScriptCallable function = (IvoryScriptCallable) callee;
        if (arguments.size() != function.arity()) {
            throw new RuntimeError(expr.paren, "Expected " + function.arity() + " arguments but got " + arguments.size() + ".");
        }
        return function.call(this, arguments);
    }

    @Override
    public Void visitFunctionStmt(Stmt.Function stmt) {
        IvoryScriptFunction function = new IvoryScriptFunction(stmt);
        environment.define(stmt.name.lexeme, function);
        return null;
    }
    
    @Override
    public Void visitReturnStmt(Stmt.Return stmt) {
        Object value = null;
        if (stmt.value != null) {
            value = evaluate(stmt.value);
        }
        throw new Return(value);
    }

    @Override
    public Void visitClassStmt(Stmt.Class stmt) {
        Object superclass = null;
        if (stmt.superclass != null) {
            superclass = evaluate(stmt.superclass);
            if (!(superclass instanceof IvoryScriptClass)) {
                throw new RuntimeError(stmt.superclass.name, "Superclass must be a class.");
            }
        }

        environment.define(stmt.name.lexeme, null);
    
        Environment previous = this.environment;
        Environment methodEnvironment = new Environment(environment);
        if (stmt.superclass != null) {
            methodEnvironment.define("super", superclass);
        }
        this.environment = methodEnvironment;
    
        Map<String, IvoryScriptFunction> methods = new HashMap<>();
        for (Stmt.Function method : stmt.methods) {
            IvoryScriptFunction function = new IvoryScriptFunction(method, methodEnvironment);
            methods.put(method.name.lexeme, function);
        }
    
        this.environment = previous;
    
        IvoryScriptClass klass = new IvoryScriptClass(stmt.name.lexeme, (IvoryScriptClass) superclass, methods);
        environment.assign(stmt.name, klass);
    
        return null;
    }

    @Override
    public Object visitGetExpr(Expr.Get expr) {
        Object object = evaluate(expr.object);
        if (object instanceof IvoryScriptInstance) {
            return ((IvoryScriptInstance) object).get(expr.name);
        }
        if (object instanceof String) {
            IvoryScriptString str = new IvoryScriptString((String) object);
            Object property = str.getProperty(expr.name.lexeme);
            if (property != null) {
                return property;
            }
            throw new RuntimeError(expr.name, "String has no property '" + expr.name.lexeme + "'.");
        }
        if (object instanceof IvoryScriptArray && expr.name.lexeme.equals("length")) {
            return (double) ((IvoryScriptArray) object).length();
        }
        if (object instanceof IvoryScriptDictionary && expr.name.lexeme.equals("length")) {
            return (double) ((IvoryScriptDictionary) object).getEntries().size();
        }
        throw new RuntimeError(expr.name, "Only instances, strings, arrays, and dictionaries have properties.");
    }

    @Override
    public Object visitSetExpr(Expr.Set expr) {
        Object object = evaluate(expr.object);
        if (!(object instanceof IvoryScriptInstance)) {
            throw new RuntimeError(expr.name, "Only instances have fields.");
        }
        Object value = evaluate(expr.value);
        ((IvoryScriptInstance) object).set(expr.name, value);
        return value;
    }

    @Override
    public Object visitSuperExpr(Expr.Super expr) {
        Token superToken = new Token(TokenType.SUPER, "super", null, expr.keyword.line);
        Object superclassObj = environment.get(superToken);
        if (!(superclassObj instanceof IvoryScriptClass)) {
            throw new RuntimeError(expr.keyword, "Can't use 'super' outside of a class.");
        }
        IvoryScriptClass superclass = (IvoryScriptClass) superclassObj;
        
        Token thisToken = new Token(TokenType.THIS, "this", null, expr.keyword.line);
        Object thisObj = environment.get(thisToken);
        if (!(thisObj instanceof IvoryScriptInstance)) {
            throw new RuntimeError(expr.keyword, "Can't use 'super' outside of an instance method.");
        }
        IvoryScriptInstance object = (IvoryScriptInstance) thisObj;
        
        IvoryScriptFunction method = superclass.findMethod(expr.method.lexeme);
        if (method == null) {
            throw new RuntimeError(expr.method, "Undefined property '" + expr.method.lexeme + "'.");
        }
        return method.bind(object);
    }

    @Override
    public Object visitThisExpr(Expr.This expr) {
        return environment.get(expr.keyword);
    }

    @Override
    public Object visitArrayExpr(Expr.Array expr) {
        List<Object> elements = new ArrayList<>();
        for (Expr element : expr.elements) {
            elements.add(evaluate(element));
        }
        return new IvoryScriptArray(elements);
    }

    @Override
    public Object visitDictionaryExpr(Expr.Dictionary expr) {
        IvoryScriptDictionary dict = new IvoryScriptDictionary();
        for (int i = 0; i < expr.keys.size(); i++) {
            Object key = evaluate(expr.keys.get(i));
            if (!(key instanceof String)) {
                throw new RuntimeError(null, "Dictionary keys must be strings.");
            }
            Object value = evaluate(expr.values.get(i));
            dict.set((String) key, value);
        }
        return dict;
    }

    @Override
    public Object visitIndexExpr(Expr.Index expr) {
        Object object = evaluate(expr.object);
        Object index = evaluate(expr.index);

        if (object instanceof IvoryScriptArray) {
            if (!(index instanceof Double)) {
                throw new RuntimeError(expr.bracket, "Array index must be a number.");
            }
            int idx = ((Double) index).intValue();
            return ((IvoryScriptArray) object).get(idx);
        } else if (object instanceof IvoryScriptDictionary) {
            if (!(index instanceof String)) {
                throw new RuntimeError(expr.bracket, "Dictionary key must be a string.");
            }
            Object value = ((IvoryScriptDictionary) object).get((String) index);
            if (value == null) {
                throw new RuntimeError(expr.bracket, "Key '" + index + "' not found in dictionary.");
            }
            return value;
        } else if (object instanceof String) {
            if (!(index instanceof Double)) {
                throw new RuntimeError(expr.bracket, "String index must be a number.");
            }
            int idx = ((Double) index).intValue();
            String str = (String) object;
            if (idx < 0 || idx >= str.length()) {
                throw new RuntimeError(expr.bracket, "String index out of bounds.");
            }
            return String.valueOf(str.charAt(idx));
        }

        throw new RuntimeError(expr.bracket, "Can only index arrays, dictionaries, and strings.");
    }

    @Override
    public Object visitIndexAssignExpr(Expr.IndexAssign expr) {
        Object object = evaluate(expr.object);
        Object index = evaluate(expr.index);
        Object value = evaluate(expr.value);

        if (object instanceof IvoryScriptArray) {
            if (!(index instanceof Double)) {
                throw new RuntimeError(expr.bracket, "Array index must be a number.");
            }
            int idx = ((Double) index).intValue();
            ((IvoryScriptArray) object).set(idx, value);
            return value;
        } else if (object instanceof IvoryScriptDictionary) {
            if (!(index instanceof String)) {
                throw new RuntimeError(expr.bracket, "Dictionary key must be a string.");
            }
            ((IvoryScriptDictionary) object).set((String) index, value);
            return value;
        }

        throw new RuntimeError(expr.bracket, "Can only assign to array or dictionary indices.");
    }
}
