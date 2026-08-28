/*
 * Decompiled with CFR 0.152.
 */
package de.markusbordihn.modsoptimizer.thirdparty.semver.semver.expr;

import de.markusbordihn.modsoptimizer.thirdparty.semver.semver.expr.LexerException;
import de.markusbordihn.modsoptimizer.thirdparty.semver.semver.util.Stream;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Lexer {
    Lexer() {
    }

    Stream<Token> tokenize(String input) {
        ArrayList<Token> tokens = new ArrayList<Token>();
        int tokenPos = 0;
        while (!input.isEmpty()) {
            boolean matched = false;
            for (Token.Type tokenType : Token.Type.values()) {
                Matcher matcher = tokenType.pattern.matcher(input);
                if (!matcher.find()) continue;
                matched = true;
                input = matcher.replaceFirst("");
                if (tokenType != Token.Type.WHITESPACE) {
                    tokens.add(new Token(tokenType, matcher.group(), tokenPos));
                }
                tokenPos += matcher.end();
                break;
            }
            if (matched) continue;
            throw new LexerException(input);
        }
        tokens.add(new Token(Token.Type.EOI, null, tokenPos));
        return new Stream<Token>(tokens.toArray(new Token[tokens.size()]));
    }

    static class Token {
        final Type type;
        final String lexeme;
        final int position;

        Token(Type type, String lexeme, int position) {
            this.type = type;
            this.lexeme = lexeme == null ? "" : lexeme;
            this.position = position;
        }

        public boolean equals(Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof Token)) {
                return false;
            }
            Token token = (Token)other;
            return this.type.equals(token.type) && this.lexeme.equals(token.lexeme) && this.position == token.position;
        }

        public int hashCode() {
            int hash = 5;
            hash = 71 * hash + this.type.hashCode();
            hash = 71 * hash + this.lexeme.hashCode();
            hash = 71 * hash + this.position;
            return hash;
        }

        public String toString() {
            return String.format("%s(%s) at position %d", this.type.name(), this.lexeme, this.position);
        }

        static enum Type implements Stream.ElementType<Token>
        {
            NUMERIC("0|[1-9][0-9]*"),
            DOT("\\."),
            HYPHEN("-"),
            EQUAL("="),
            NOT_EQUAL("!="),
            GREATER(">(?!=)"),
            GREATER_EQUAL(">="),
            LESS("<(?!=)"),
            LESS_EQUAL("<="),
            TILDE("~"),
            WILDCARD("[\\*xX]"),
            CARET("\\^"),
            AND("&"),
            OR("\\|"),
            NOT("!(?!=)"),
            LEFT_PAREN("\\("),
            RIGHT_PAREN("\\)"),
            WHITESPACE("\\s+"),
            EOI("?!");

            final Pattern pattern;

            private Type(String regexp) {
                this.pattern = Pattern.compile("^(" + regexp + ")");
            }

            public String toString() {
                return this.name() + "(" + this.pattern + ")";
            }

            @Override
            public boolean isMatchedBy(Token token) {
                if (token == null) {
                    return false;
                }
                return this == token.type;
            }
        }
    }
}

