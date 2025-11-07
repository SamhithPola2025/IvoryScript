package com.mainsrc.ivoryscript;

import java.util.List;
import com.mainsrc.ivoryscript.Token;

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
    R visitFunctionStmt(Function stmt);
    R visitReturnStmt(Return stmt);
  }

  static class Expression extends Stmt {
    final Expr expression;

    Expression(Expr expression) {
      this.expression = expression;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitExpressionStmt(this);
    }
  }

  static class Print extends Stmt {
    final Expr expression;

    Print(Expr expression) {
      this.expression = expression;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitPrintStmt(this);
    }
  }

  static class Var extends Stmt {
    final Token name;
    final Expr initializer;

    Var(Token name, Expr initializer) {
      this.name = name;
      this.initializer = initializer;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitVarStmt(this);
    }
  }

  static class Block extends Stmt {
    final List<Stmt> statements;

    Block(List<Stmt> statements) {
      this.statements = statements;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitBlockStmt(this);
    }
  }

  static class If extends Stmt {
    final Expr condition;
    final Stmt thenBranch;
    final Stmt elseBranch;

    If(Expr condition, Stmt thenBranch, Stmt elseBranch) {
      this.condition = condition;
      this.thenBranch = thenBranch;
      this.elseBranch = elseBranch;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitIfStmt(this);
    }
  }

  static class While extends Stmt {
    final Expr condition;
    final Stmt body;

    While(Expr condition, Stmt body) {
      this.condition = condition;
      this.body = body;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitWhileStmt(this);
    }
  }

  static class For extends Stmt {
    final Stmt initializer;
    final Expr condition;
    final Expr increment;
    final Stmt body;

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
  }

  static class Break extends Stmt {
    Break() {}

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitBreakStmt(this);
    }
  }

  static class Switch extends Stmt {
    final Expr condition;
    final List<Case> cases;
    final Default defaultCase;

    Switch(Expr condition, List<Case> cases, Default defaultCase) {
      this.condition = condition;
      this.cases = cases;
      this.defaultCase = defaultCase;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitSwitchStmt(this);
    }
  }

  static class Case extends Stmt {
    final Expr value;
    final List<Stmt> body;

    Case(Expr value, List<Stmt> body) {
      this.value = value;
      this.body = body;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitCaseStmt(this);
    }
  }

  static class Default extends Stmt {
    final List<Stmt> body;

    Default(List<Stmt> body) {
      this.body = body;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitDefaultStmt(this);
    }
  }

  static class Function extends Stmt {
    final Token name;
    final List<Token> params;
    final List<Stmt> body;

    Function(Token name, List<Token> params, List<Stmt> body) {
      this.name = name;
      this.params = params;
      this.body = body;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitFunctionStmt(this);
    }
  }

  static class Return extends Stmt {
    final Token keyword;
    final Expr value;

    Return(Token keyword, Expr value) {
      this.keyword = keyword;
      this.value = value;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitReturnStmt(this);
    }
  }

  abstract <R> R accept(Visitor<R> visitor);
}