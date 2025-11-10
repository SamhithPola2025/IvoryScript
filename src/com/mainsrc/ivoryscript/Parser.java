package com.mainsrc.ivoryscript;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.mainsrc.ivoryscript.TokenType.*;
import com.mainsrc.ivoryscript.Stmt.*;
import com.mainsrc.ivoryscript.Expr.*;
import com.mainsrc.ivoryscript.IvoryScript.*;

class Parser {

    private static class ParseError extends RuntimeException {}
    private final List<Token> tokens;
    private int current = 0;

    Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    private Expr equality() {
        Expr expr = comparison();

        while (match(BANG_EQUAL, EQUAL_EQUAL)) {
            Token operator = previous();
            Expr right = comparison();
            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

    private boolean match(TokenType... types) {
        for (TokenType type : types) {
            if (check(type)) {
                advance();
                return true;
            }
        }

        return false;
    }

    private boolean check(TokenType type) {
        if (isAtEnd()) return false;
        return peek().type == type;
    }

    private Token advance() {
        if (!isAtEnd()) current++;
        return previous();
    }

    private boolean isAtEnd() {
        return peek().type == EOF;
    }

    private Token peek() {
        return tokens.get(current);
    }

    private Token previous() {
        return tokens.get(current - 1);
    }

    private Expr comparison() {
        Expr expr = term();

        while (match(GREATER, GREATER_EQUAL, LESS, LESS_EQUAL)) {
            Token operator = previous();
            Expr right = term();
            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

    private Expr term() {
        Expr expr = factor();

        while (match(MINUS, PLUS)) {
            Token operator = previous();
            Expr right = factor();
            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

    private Expr factor() {
        Expr expr = unary();

        while (match(SLASH, STAR)) {
            Token operator = previous();
            Expr right = unary();
            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

    private Expr unary() {
        if (match(BANG, MINUS)) {
            Token operator = previous();
            Expr right = unary();
            return new Expr.Unary(operator, right);
        }

        return call();
    }

    private Expr call() {
        Expr expr = primary();

        while (true) {
            if (match(LEFT_PAREN)) {
                expr = finishCall(expr);
            } else if (match(DOT)) {
                Token name = consume(IDENTIFIER, "Expect property name after '.'.");
                expr = new Expr.Get(expr, name);
            } else if (match(LEFT_BRACKET)) {
                Expr index = expression();
                Token bracket = consume(RIGHT_BRACKET, "Expect ']' after index.");
                expr = new Expr.Index(expr, index, bracket);
            } else {
                break;
            }
        }

        return expr;
    }

    private Expr primary() {
        if (match(FALSE)) return new Expr.Literal(false);
        if (match(TRUE)) return new Expr.Literal(true);
        if (match(NIL)) return new Expr.Literal(null);
        if (match(NUMBER, STRING)) {
            return new Expr.Literal(previous().literal);
        }
    
        if (match(SUPER)) {
            Token keyword = previous();
            consume(DOT, "Expect '.' after 'super'.");
            Token method = consume(IDENTIFIER, "Expect superclass method name.");
            return new Expr.Super(keyword, method);
        }

        if (match(THIS)) {
            return new Expr.This(previous());
        }
    
        if (match(IDENTIFIER)) {
            return new Expr.Variable(previous());
        }
    
        if (match(LEFT_PAREN)) {
            Expr expr = expression();
            consume(RIGHT_PAREN, "Expected ')' after expression!");
            return new Expr.Grouping(expr);
        }

        if (match(LEFT_BRACKET)) {
            return array();
        }

        if (match(LEFT_BRACE)) {
            return dictionary();
        }
    
        throw error(peek(), "Expected expression.");
    }

    private Expr array() {
        List<Expr> elements = new ArrayList<>();
        if (!check(RIGHT_BRACKET)) {
            do {
                elements.add(expression());
            } while (match(COMMA));
        }
        consume(RIGHT_BRACKET, "Expect ']' after array elements.");
        return new Expr.Array(elements);
    }

    private Expr dictionary() {
        List<Expr> keys = new ArrayList<>();
        List<Expr> values = new ArrayList<>();
        if (!check(RIGHT_BRACE)) {
            do {
                Expr key = expression();
                consume(COLON, "Expect ':' after dictionary key.");
                Expr value = expression();
                keys.add(key);
                values.add(value);
            } while (match(COMMA));
        }
        consume(RIGHT_BRACE, "Expect '}' after dictionary entries.");
        return new Expr.Dictionary(keys, values);
    }

    private Expr finishCall(Expr callee) {
        List<Expr> arguments = new ArrayList<>();
        if (!check(TokenType.RIGHT_PAREN)) {
            do {
                if (arguments.size() >= 255) {
                    error(peek(), "Cannot have more than 255 arguments.");
                }
                arguments.add(expression());
            } while (match(TokenType.COMMA));
        }
        Token paren = consume(TokenType.RIGHT_PAREN, "Expect ')' after arguments.");
        return new Expr.Call(callee, paren, arguments);
    }

    private Token consume(TokenType type, String message) {
        if (check(type)) {
            return advance();
        }
    
        throw error(peek(), message);
    }
    private ParseError error(Token token, String message) {
        IvoryScript.error(token.line, message);
        return new ParseError();
    }
    
    private Stmt statement() {
        if (match(PRINT)) return printStatement();
        if (match(LEFT_BRACE)) return new Stmt.Block(block());
        if (match(CHOOSE)) return chooseStatement();
        if (match(IF)) return ifStatement();
        if (match(WHILE)) return whileStatement();
        if (match(FOR)) return forStatement();
        if (match(DISRUPT)) {
            consume(SEMICOLON, "Expected ';' after 'disrupt'.");
            return new Stmt.Break();
        }
        if (match(RETURN)) return returnStatement(); 
        return expressionStatement();
    }

    private Stmt printStatement() {
        Expr value = expression();
        consume(SEMICOLON, "Expected ';' after value!");
        return new Stmt.Print(value);
    }

    private Stmt expressionStatement() {
        Expr expr = expression();
        consume(TokenType.SEMICOLON, "Expected ';' after value!");
        return new Stmt.Expression(expr);
    }

    private List<Stmt> block() {
        List<Stmt> statements = new ArrayList<>();

        while (!check(RIGHT_BRACE) && !isAtEnd()) {
            statements.add(declaration());
        }

        consume(RIGHT_BRACE, "Expected '}' after block!");
        return statements;
    }

    private Stmt declaration() {
        try {
            if (match(FUN)) return function("function");
            if (match(VAR)) return varDeclaration();
            if (match(CLASS)) return classDeclaration();
            return statement();
        } catch (ParseError error) {
            synchronize();
            return null;
        }
    }

    private Stmt varDeclaration() {
        Token name = consume(IDENTIFIER, "Expected variable name.");

        Expr initializer = null;
        if (match(EQUAL)) {
            initializer = expression();
        }

        consume(SEMICOLON, "Expected ';' after variable declaration!");
        return new Stmt.Var(name, initializer);
    }

    private Stmt ifStatement() {
        consume(LEFT_PAREN, "Expected '(' after 'if'.");
        Expr condition = expression();
        consume(RIGHT_PAREN, "Expected ')' after if condition.");

        Stmt thenBranch = statement();
        Stmt elseBranch = null;
        if (match(ELSE)) {
            elseBranch = statement();
        }

        return new Stmt.If(condition, thenBranch, elseBranch);
    }

    private Stmt whileStatement() {
        consume(LEFT_PAREN, "Expected '(' after 'while'.");
        Expr condition = expression();
        consume(RIGHT_PAREN, "Expected ')' after condition.");
        Stmt body = statement();

        return new Stmt.While(condition, body);
    }

    private Stmt forStatement() {
        consume(LEFT_PAREN, "Expected '(' after 'for'.");

        Stmt initializer;
        if (match(SEMICOLON)) {
            initializer = null;
        } else if (match(VAR)) {
            initializer = varDeclaration();
        } else {
            initializer = expressionStatement();
        }

        Expr condition = null;
        if (!check(SEMICOLON)) {
            condition = expression();
        }
        consume(SEMICOLON, "Expected ';' after loop condition.");

        Expr increment = null;
        if (!check(RIGHT_PAREN)) {
            increment = expression();
        }
        consume(RIGHT_PAREN, "Expected ')' after for clauses.");

        Stmt body = statement();

        if (increment != null) {
            body = new Stmt.Block(
                Arrays.asList(body, new Stmt.Expression(increment)));
        }

        if (condition == null) condition = new Expr.Literal(true);

        body = new Stmt.While(condition, body);

        if (initializer != null) {
            body = new Stmt.Block(Arrays.asList(initializer, body));
        }

        return body;
    }

    private Expr expression() {
        return assignment();
    }

    private Expr assignment() {
        Expr expr = equality();

        if (match(EQUAL)) {
            Token equals = previous();
            Expr value = assignment();

            if (expr instanceof Expr.Variable) {
                Token name = ((Expr.Variable)expr).name;
                return new Expr.Assign(name, value);
            } else if (expr instanceof Expr.Get) {
                Expr.Get get = (Expr.Get)expr;
                return new Expr.Set(get.object, get.name, value);
            } else if (expr instanceof Expr.Index) {
                Expr.Index index = (Expr.Index)expr;
                return new Expr.IndexAssign(index.object, index.index, value, index.bracket);
            }

            error(equals, "Invalid assignment target.");
        }

        return expr;
    }

    private void synchronize() {
        advance();

        while (!isAtEnd()) {
            if (previous().type == SEMICOLON) return;

            switch (peek().type) {
                case CLASS:
                case FUN:
                case VAR:
                case FOR:
                case IF:
                case WHILE:
                case PRINT:
                case RETURN:
                case CHOOSE:
                case DISRUPT:
                    return;
            }

            advance();
        }
    }

    private Stmt chooseStatement() {
        consume(LEFT_PAREN, "Expected '(' after 'choose'.");
        Expr condition = expression();
        consume(RIGHT_PAREN, "Expected ')' after choose condition.");
        consume(LEFT_BRACE, "Expected '{' before choose options.");

        List<Stmt.Case> cases = new ArrayList<>();
        Stmt.Default defaultCase = null;

        while (!check(RIGHT_BRACE) && !isAtEnd()) {
            if (match(OPTION)) {
                if (!check(NUMBER) && !check(STRING)) {
                    throw error(peek(), "Expected a value (e.g., number or string) after 'option'. Found: " + peek().type);
                }
                Expr optionValue = expression();
                consume(COLON, "Expected ':' following option value.");
                List<Stmt> body = new ArrayList<>();
                while (!check(OPTION) && !check(OTHERWISE) && !check(RIGHT_BRACE) && !isAtEnd()) {
                    if (match(DISRUPT)) {
                        consume(SEMICOLON, "Expected ';' after 'disrupt'.");
                        body.add(new Stmt.Break());
                    } else {
                        body.add(statement());
                    }
                }
                cases.add(new Stmt.Case(optionValue, body));
            } else if (match(OTHERWISE)) {
                if (defaultCase != null) {
                    throw error(peek(), "Multiple 'otherwise' blocks are not allowed.");
                }
                consume(COLON, "Expected ':' after 'otherwise'.");
                List<Stmt> body = new ArrayList<>();
                while (!check(OPTION) && !check(RIGHT_BRACE) && !isAtEnd()) {
                    if (match(DISRUPT)) {
                        consume(SEMICOLON, "Expected ';' after 'disrupt'.");
                        body.add(new Stmt.Break());
                    } else {
                        body.add(statement());
                    }
                }
                defaultCase = new Stmt.Default(body);
            } else {
                throw error(peek(), "Expected 'option' or 'otherwise'.");
            }
        }

        consume(RIGHT_BRACE, "Expected '}' after choose options.");
        return new Stmt.Switch(condition, cases, defaultCase);
    }

    public List<Stmt> parse() {
        List<Stmt> statements = new ArrayList<>();
        while (!isAtEnd()) {
            statements.add(declaration());
        }
        return statements;
    }

    private Stmt.Function function(String kind) {
        Token name = consume(TokenType.IDENTIFIER, "Expect " + kind + " name.");
        consume(TokenType.LEFT_PAREN, "Expect '(' after " + kind + " name.");
        List<Token> parameters = new ArrayList<>();
        if (!check(TokenType.RIGHT_PAREN)) {
            do {
                if (parameters.size() >= 255) {
                    error(peek(), "Cannot have more than 255 parameters.");
                }
                parameters.add(consume(TokenType.IDENTIFIER, "Expect parameter name."));
            } while (match(TokenType.COMMA));
        }
        consume(TokenType.RIGHT_PAREN, "Expect ')' after parameters.");
        consume(TokenType.LEFT_BRACE, "Expect '{' before " + kind + " body.");
        List<Stmt> body = block();
        return new Stmt.Function(name, parameters, body);
    }

    private Stmt returnStatement() {
        Token keyword = previous();
        Expr value = null;

        if (!check(TokenType.SEMICOLON)) {
            value = expression();
        }

        consume(TokenType.SEMICOLON, "Expected ';' after return value.");
        return new Stmt.Return(keyword, value);
    }

    private Stmt classDeclaration() {
        Token name = consume(TokenType.IDENTIFIER, "Expect class name.");
    
        Expr.Variable superclass = null;
        if (match(TokenType.LESS)) {
            consume(TokenType.IDENTIFIER, "Expect superclass name.");
            superclass = new Expr.Variable(previous());
        }
    
        consume(TokenType.LEFT_BRACE, "Expect '{' before class body.");
        List<Stmt.Function> methods = new ArrayList<>();
        while (!check(TokenType.RIGHT_BRACE) && !isAtEnd()) {
            consume(TokenType.FUN, "Expect 'fun' keyword before method.");
            methods.add(function("method"));
        }
        consume(TokenType.RIGHT_BRACE, "Expect '}' after class body.");
    
        return new Stmt.Class(name, superclass, methods);
    }
}