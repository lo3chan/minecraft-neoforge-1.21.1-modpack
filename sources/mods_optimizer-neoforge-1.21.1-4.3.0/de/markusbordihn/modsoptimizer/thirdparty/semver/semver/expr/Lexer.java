package de.markusbordihn.modsoptimizer.thirdparty.semver.semver.expr;

import de.markusbordihn.modsoptimizer.thirdparty.semver.semver.util.Stream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Lexer {
   Stream<Lexer.Token> tokenize(String input) {
      List<Lexer.Token> tokens = new ArrayList<>();
      int tokenPos = 0;

      while (!input.isEmpty()) {
         boolean matched = false;

         for (Lexer.Token.Type tokenType : Lexer.Token.Type.values()) {
            Matcher matcher = tokenType.pattern.matcher(input);
            if (matcher.find()) {
               matched = true;
               input = matcher.replaceFirst("");
               if (tokenType != Lexer.Token.Type.WHITESPACE) {
                  tokens.add(new Lexer.Token(tokenType, matcher.group(), tokenPos));
               }

               tokenPos += matcher.end();
               break;
            }
         }

         if (!matched) {
            throw new LexerException(input);
         }
      }

      tokens.add(new Lexer.Token(Lexer.Token.Type.EOI, null, tokenPos));
      return new Stream<>(tokens.toArray(new Lexer.Token[tokens.size()]));
   }

   static class Token {
      final Lexer.Token.Type type;
      final String lexeme;
      final int position;

      Token(Lexer.Token.Type type, String lexeme, int position) {
         this.type = type;
         this.lexeme = lexeme == null ? "" : lexeme;
         this.position = position;
      }

      @Override
      public boolean equals(Object other) {
         if (this == other) {
            return true;
         } else if (!(other instanceof Lexer.Token)) {
            return false;
         } else {
            Lexer.Token token = (Lexer.Token)other;
            return this.type.equals(token.type) && this.lexeme.equals(token.lexeme) && this.position == token.position;
         }
      }

      @Override
      public int hashCode() {
         int hash = 5;
         hash = 71 * hash + this.type.hashCode();
         hash = 71 * hash + this.lexeme.hashCode();
         return 71 * hash + this.position;
      }

      @Override
      public String toString() {
         return String.format("%s(%s) at position %d", this.type.name(), this.lexeme, this.position);
      }

      static enum Type implements Stream.ElementType<Lexer.Token> {
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

         @Override
         public String toString() {
            return this.name() + "(" + this.pattern + ")";
         }

         public boolean isMatchedBy(Lexer.Token token) {
            return token == null ? false : this == token.type;
         }
      }
   }
}
