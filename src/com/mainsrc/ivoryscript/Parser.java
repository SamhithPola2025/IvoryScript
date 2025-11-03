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

        return primary();
    }

    private Expr primary() {
        if (match(FALSE)) return new Expr.Literal(false);
        if (match(TRUE)) return new Expr.Literal(true);
        if (match(NIL)) return new Expr.Literal(null);
        if (match(NUMBER, STRING)) {
            return new Expr.Literal(previous().literal);
        }

        if (match(IDENTIFIER)) {
            return new Expr.Variable(previous());
        }

        if (match(LEFT_PAREN)) {
            Expr expr = expression();
            consume(RIGHT_PAREN, "Expected ')' after expression!");
            return new Expr.Grouping(expr);
        }

        throw error(peek(), "Expected expression.");
    }

    private Token consume(TokenType type, String message) {
        if (check(type)) return advance();

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
        return expressionStatement();
    }

    private Stmt printStatement() {
        Expr value = expression();
        consume(SEMICOLON, "Expected ';' after value!");
        return new Stmt.Print(value);
    }

    private Stmt expressionStatement() {
        Expr expr = expression();
        consume(SEMICOLON, "Expected ';' after value!");
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
            if (match(VAR)) return varDeclaration();
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
        System.out.println("Parsing choose statement...");
        consume(LEFT_PAREN, "Expected '(' after 'choose'.");
        Expr condition = expression();
        System.out.println("Condition: " + condition);
        consume(RIGHT_PAREN, "Expected ')' after choose condition.");
        consume(LEFT_BRACE, "Expected '{' before choose options.");

        List<Stmt.Case> cases = new ArrayList<>();
        Stmt.Default defaultCase = null;

        while (!check(RIGHT_BRACE) && !isAtEnd()) {
            System.out.println("Current token: " + peek().type + " (" + peek().lexeme + ")");
            if (match(OPTION)) {
                System.out.println("Parsing option...");
                if (!check(NUMBER) && !check(STRING)) {
                    throw error(peek(), "Expected a value (e.g., number or string) after 'option'. Found: " + peek().type);
                }
                Expr optionValue = expression();
                System.out.println("Option value: " + optionValue);
                consume(COLON, "Expected ':' following option value.");
                List<Stmt> body = new ArrayList<>();
                while (!check(OPTION) && !check(OTHERWISE) && !check(RIGHT_BRACE) && !isAtEnd()) {
                    System.out.println("Parsing statement inside option... Current token: " + peek().type);
                    if (match(DISRUPT)) {
                        consume(SEMICOLON, "Expected ';' after 'disrupt'.");
                        body.add(new Stmt.Break());
                    } else {
                        body.add(statement());
                    }
                }
                cases.add(new Stmt.Case(optionValue, body));
                System.out.println("Finished parsing option.");
            } else if (match(OTHERWISE)) {
                System.out.println("Parsing otherwise...");
                if (defaultCase != null) {
                    throw error(peek(), "Multiple 'otherwise' blocks are not allowed.");
                }
                consume(COLON, "Expected ':' after 'otherwise'.");
                List<Stmt> body = new ArrayList<>();
                while (!check(OPTION) && !check(RIGHT_BRACE) && !isAtEnd()) {
                    System.out.println("Parsing statement inside otherwise... Current token: " + peek().type);
                    if (match(DISRUPT)) {
                        consume(SEMICOLON, "Expected ';' after 'disrupt'.");
                        body.add(new Stmt.Break());
                    } else {
                        body.add(statement());
                    }
                }
                defaultCase = new Stmt.Default(body);
                System.out.println("Finished parsing otherwise.");
            } else {
                System.out.println("Unexpected token: " + peek().type + " (" + peek().lexeme + ")");
                throw error(peek(), "Expected 'option' or 'otherwise'.");
            }
        }

        consume(RIGHT_BRACE, "Expected '}' after choose options.");
        System.out.println("Finished parsing choose statement.");
        return new Stmt.Switch(condition, cases, defaultCase);
    }

    public List<Stmt> parse() {
        List<Stmt> statements = new ArrayList<>();
        while (!isAtEnd()) {
            statements.add(declaration());
        }
        return statements;
    }
}