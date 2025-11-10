package com.mainsrc.ivoryscript;

import java.util.List;

abstract class Expr {
  interface Visitor<R> {
    R visitBinaryExpr(Binary expr);
    R visitGroupingExpr(Grouping expr);
    R visitLiteralExpr(Literal expr);
    R visitUnaryExpr(Unary expr);
    R visitVariableExpr(Variable expr);
    R visitAssignExpr(Assign expr);
    R visitCallExpr(Call expr);
    R visitGetExpr(Get expr);
    R visitSetExpr(Set expr);
    R visitSuperExpr(Super expr);
    R visitThisExpr(This expr);
    R visitArrayExpr(Array expr);
    R visitDictionaryExpr(Dictionary expr);
    R visitIndexExpr(Index expr);
    R visitIndexAssignExpr(IndexAssign expr);
  }
  static class Binary extends Expr {
    Binary(Expr left, Token operator, Expr right) {
      this.left = left;
      this.operator = operator;
      this.right = right;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitBinaryExpr(this);
    }

    final Expr left;
    final Token operator;
    final Expr right;
  }
  static class Grouping extends Expr {
    Grouping(Expr expression) {
      this.expression = expression;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitGroupingExpr(this);
    }

    final Expr expression;
  }
  static class Literal extends Expr {
    Literal(Object value) {
      this.value = value;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitLiteralExpr(this);
    }

    final Object value;
  }
  static class Unary extends Expr {
    Unary(Token operator, Expr right) {
      this.operator = operator;
      this.right = right;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitUnaryExpr(this);
    }

    final Token operator;
    final Expr right;
  }
  static class Variable extends Expr {
    Variable(Token name) {
      this.name = name;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitVariableExpr(this);
    }

    final Token name;
  }
  static class Assign extends Expr {
    Assign(Token name, Expr value) {
      this.name = name;
      this.value = value;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitAssignExpr(this);
    }

    final Token name;
    final Expr value;
  }

  abstract <R> R accept(Visitor<R> visitor);

  static class Call extends Expr {
    final Expr callee;
    final Token paren;
    final List<Expr> arguments;

    Call (Expr callee, Token paren, List<Expr> arguments) {
      this.callee = callee;
      this.paren=paren;
      this.arguments=arguments;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitCallExpr(this);
    }  
  }

  static class Get extends Expr {
    final Expr object;
    final Token name;

    Get(Expr object, Token name) {
      this.object = object;
      this.name = name;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitGetExpr(this);
    }
  }

  static class Set extends Expr {
    final Expr object;
    final Token name;
    final Expr value;

    Set(Expr object, Token name, Expr value) {
      this.object = object;
      this.name = name;
      this.value = value;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitSetExpr(this);
    }
  }

  static class Super extends Expr {
    final Token keyword;
    final Token method;

    Super(Token keyword, Token method) {
      this.keyword = keyword;
      this.method = method;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitSuperExpr(this);
    }
  }

  static class This extends Expr {
    final Token keyword;

    This(Token keyword) {
      this.keyword = keyword;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitThisExpr(this);
    }
  }

  static class Array extends Expr {
    final List<Expr> elements;

    Array(List<Expr> elements) {
      this.elements = elements;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitArrayExpr(this);
    }
  }

  static class Dictionary extends Expr {
    final List<Expr> keys;
    final List<Expr> values;

    Dictionary(List<Expr> keys, List<Expr> values) {
      this.keys = keys;
      this.values = values;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitDictionaryExpr(this);
    }
  }

  static class Index extends Expr {
    final Expr object;
    final Expr index;
    final Token bracket;

    Index(Expr object, Expr index, Token bracket) {
      this.object = object;
      this.index = index;
      this.bracket = bracket;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitIndexExpr(this);
    }
  }

  static class IndexAssign extends Expr {
    final Expr object;
    final Expr index;
    final Expr value;
    final Token bracket;

    IndexAssign(Expr object, Expr index, Expr value, Token bracket) {
      this.object = object;
      this.index = index;
      this.value = value;
      this.bracket = bracket;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitIndexAssignExpr(this);
    }
  }
}
