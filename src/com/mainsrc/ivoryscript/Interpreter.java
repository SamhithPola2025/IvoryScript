package com.mainsrc.ivoryscript;

import java.util.List;
import java.util.ArrayList;

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
            // break stops the loop
        }

        return null;
    }
    
    @Override
    public Void visitCaseStmt(Stmt.Case stmt) {
        // handled in switch
        return null;
    }
    
    @Override
    public Void visitDefaultStmt(Stmt.Default stmt) {
        // handled in switch
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
            // break stops execution of switch
        }

        return null;
    }

    public Environment environment = new Environment();

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
        // handle single statement or block
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

    private String stringify(Object object) {
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
            // break exits while
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
}
