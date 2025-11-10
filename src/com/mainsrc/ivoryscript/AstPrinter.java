package com.mainsrc.ivoryscript;

import com.mainsrc.ivoryscript.Expr;

class AstPrinter implements Expr.Visitor<String> {
  String print(Expr expr) {
    return expr.accept(this);
  }

  @Override
  public String visitBinaryExpr(Expr.Binary expr) {
    return parenthesize(expr.operator.lexeme, expr.left, expr.right);
  }

  @Override
  public String visitGroupingExpr(Expr.Grouping expr) {
    return parenthesize("group", expr.expression);
  }

  @Override
  public String visitLiteralExpr(Expr.Literal expr) {
    if (expr.value == null) return "nil";
    return expr.value.toString();
  }

  @Override
  public String visitUnaryExpr(Expr.Unary expr) {
    return parenthesize(expr.operator.lexeme, expr.right);
  }

  @Override
  public String visitVariableExpr(Expr.Variable expr) {
    return expr.name.lexeme;
  }

  @Override
  public String visitAssignExpr(Expr.Assign expr) {
    return parenthesize("assign " + expr.name.lexeme, expr.value);
  }

  private String parenthesize(String name, Expr... exprs) {
    StringBuilder builder = new StringBuilder();

    builder.append("(").append(name);
    for (Expr expr : exprs) {
      builder.append(" ");
      builder.append(expr.accept(this));
    }
    builder.append(")");
    return builder.toString();
  }
  public static void main(String[] args) {
    Expr expression = new Expr.Binary(
        new Expr.Unary(
            new Token(TokenType.MINUS, "-", null, 1),
            new Expr.Literal(123)),
        new Token(TokenType.STAR, "*", null, 1),
        new Expr.Grouping(
            new Expr.Literal(45.67)));

    System.out.println(new AstPrinter().print(expression));
  }

  @Override
  public String visitCallExpr(Expr.Call expr) {
    return parenthesize("call " + expr.callee.accept(this), expr.arguments.toArray(new Expr[0]));
  }

  @Override
  public String visitGetExpr(Expr.Get expr) {
    return parenthesize("get " + expr.name.lexeme, expr.object);
  }

  @Override
  public String visitSetExpr(Expr.Set expr) {
    return parenthesize("set " + expr.name.lexeme, expr.object, expr.value);
  }

  @Override
  public String visitSuperExpr(Expr.Super expr) {
    return parenthesize("super." + expr.method.lexeme);
  }

  @Override
  public String visitThisExpr(Expr.This expr) {
    return "this";
  }

  @Override
  public String visitArrayExpr(Expr.Array expr) {
    StringBuilder sb = new StringBuilder("[");
    for (int i = 0; i < expr.elements.size(); i++) {
      if (i > 0) sb.append(", ");
      sb.append(expr.elements.get(i).accept(this));
    }
    sb.append("]");
    return sb.toString();
  }

  @Override
  public String visitDictionaryExpr(Expr.Dictionary expr) {
    StringBuilder sb = new StringBuilder("{");
    for (int i = 0; i < expr.keys.size(); i++) {
      if (i > 0) sb.append(", ");
      sb.append(expr.keys.get(i).accept(this));
      sb.append(": ");
      sb.append(expr.values.get(i).accept(this));
    }
    sb.append("}");
    return sb.toString();
  }

  @Override
  public String visitIndexExpr(Expr.Index expr) {
    return parenthesize("index", expr.object, expr.index);
  }

  @Override
  public String visitIndexAssignExpr(Expr.IndexAssign expr) {
    return parenthesize("indexAssign", expr.object, expr.index, expr.value);
  }
}