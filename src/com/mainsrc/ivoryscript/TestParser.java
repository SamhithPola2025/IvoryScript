package com.mainsrc.ivoryscript;

import java.util.List;

public class TestParser {
    public static void main(String[] args) {
        String source = """
            choose (x) {
                option 1:
                    print "one";
                    disrupt;
                option 2:
                    print "two";
                    disrupt;
                otherwise:
                    print "default";
            }
        """;

        IvoryScanner scanner = new IvoryScanner(source);
        List<Token> tokens = scanner.scanTokens();

        // Print all tokens for debugging
        for (Token token : tokens) {
            System.out.println(token);
        }

        Parser parser = new Parser(tokens);
        List<Stmt> statements = parser.parse();

        for (Stmt stmt : statements) {
            System.out.println(stmt);
        }
    }
}