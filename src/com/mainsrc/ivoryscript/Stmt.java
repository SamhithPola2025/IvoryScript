package com.mainsrc.ivoryscript;

import java.util.List;

abstract class Stmt {
  interface Visitor<R> {
    R visitExpressionStmt(Expression stmt);
    R visitPrintStmt(Print stmt);
    R visitVarStmt(Var stmt);
    R visitBlockStmt(Block stmt);
    R visitIfStmt(If stmt);
    R visitWhileStmt(While stmt);
    R visitForStmt(For stmt);
    R visitBreakStmt(Break stmt);
    R visitSwitchStmt(Switch stmt);
    R visitCaseStmt(Case stmt);
    R visitDefaultStmt(Default stmt);
  }
  static class Expression extends Stmt {
    Expression(Expr expression) {
      this.expression = expression;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitExpressionStmt(this);
    }

    final Expr expression;
  }
  static class Print extends Stmt {
    Print(Expr expression) {
      this.expression = expression;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitPrintStmt(this);
    }

    final Expr expression;
  }
  static class Var extends Stmt {
    Var(Token name, Expr initializer) {
      this.name = name;
      this.initializer = initializer;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitVarStmt(this);
    }

    final Token name;
    final Expr initializer;
  }
  static class Block extends Stmt {
    Block(List<Stmt> statements) {
      this.statements = statements;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitBlockStmt(this);
    }

    final List<Stmt> statements;
  }
  static class If extends Stmt {
    If(Expr condition, Stmt thenBranch, Stmt elseBranch) {
      this.condition = condition;
      this.thenBranch = thenBranch;
      this.elseBranch = elseBranch;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitIfStmt(this);
    }

    final Expr condition;
    final Stmt thenBranch;
    final Stmt elseBranch;
  }
  static class While extends Stmt {
    While(Expr condition, Stmt body) {
      this.condition = condition;
      this.body = body;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitWhileStmt(this);
    }

    final Expr condition;
    final Stmt body;
  }
  static class For extends Stmt {
    For(Stmt initializer, Expr condition, Expr increment, Stmt body) {
      this.initializer = initializer;
      this.condition = condition;
      this.increment = increment;
      this.body = body;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitForStmt(this);
    }

    final Stmt initializer;
    final Expr condition;
    final Expr increment;
    final Stmt body;
  }
  static class Break extends Stmt {
    Break() {
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitBreakStmt(this);
    }

  }
  static class Switch extends Stmt {
    Switch(Expr condition, List<Case> cases, Default defaultCase) {
      this.condition = condition;
      this.cases = cases;
      this.defaultCase = defaultCase;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitSwitchStmt(this);
    }

    final Expr condition;
    final List<Case> cases;
    final Default defaultCase;
  }
  static class Case extends Stmt {
    Case(Expr value, List<Stmt> body) {
      this.value = value;
      this.body = body;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitCaseStmt(this);
    }

    final Expr value;
    final List<Stmt> body;
  }
  static class Default extends Stmt {
    Default(List<Stmt> body) {
      this.body = body;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitDefaultStmt(this);
    }

    final List<Stmt> body;
  }

  abstract <R> R accept(Visitor<R> visitor);
}
