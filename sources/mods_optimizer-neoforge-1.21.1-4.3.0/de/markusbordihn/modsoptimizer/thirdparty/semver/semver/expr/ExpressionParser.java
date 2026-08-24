package de.markusbordihn.modsoptimizer.thirdparty.semver.semver.expr;

import de.markusbordihn.modsoptimizer.thirdparty.semver.semver.Parser;
import de.markusbordihn.modsoptimizer.thirdparty.semver.semver.Version;
import de.markusbordihn.modsoptimizer.thirdparty.semver.semver.util.Stream;
import de.markusbordihn.modsoptimizer.thirdparty.semver.semver.util.UnexpectedElementException;
import java.util.EnumSet;
import java.util.Iterator;

public class ExpressionParser implements Parser<Expression> {
   private final Lexer lexer;
   private Stream<Lexer.Token> tokens;

   ExpressionParser(Lexer lexer) {
      this.lexer = lexer;
   }

   public static Parser<Expression> newInstance() {
      return new ExpressionParser(new Lexer());
   }

   public Expression parse(String input) {
      this.tokens = this.lexer.tokenize(input);
      Expression expr = this.parseSemVerExpression();
      this.consumeNextToken(Lexer.Token.Type.EOI);
      return expr;
   }

   private CompositeExpression parseSemVerExpression() {
      CompositeExpression expr;
      if (this.tokens.positiveLookahead(Lexer.Token.Type.NOT)) {
         this.tokens.consume();
         this.consumeNextToken(Lexer.Token.Type.LEFT_PAREN);
         expr = CompositeExpression.Helper.not(this.parseSemVerExpression());
         this.consumeNextToken(Lexer.Token.Type.RIGHT_PAREN);
      } else if (this.tokens.positiveLookahead(Lexer.Token.Type.LEFT_PAREN)) {
         this.consumeNextToken(Lexer.Token.Type.LEFT_PAREN);
         expr = this.parseSemVerExpression();
         this.consumeNextToken(Lexer.Token.Type.RIGHT_PAREN);
      } else {
         expr = this.parseRange();
      }

      return this.parseMoreExpressions(expr);
   }

   private CompositeExpression parseMoreExpressions(CompositeExpression expr) {
      if (this.tokens.positiveLookahead(Lexer.Token.Type.AND)) {
         this.tokens.consume();
         expr = expr.and(this.parseSemVerExpression());
      } else if (this.tokens.positiveLookahead(Lexer.Token.Type.OR)) {
         this.tokens.consume();
         expr = expr.or(this.parseSemVerExpression());
      }

      return expr;
   }

   private CompositeExpression parseRange() {
      if (this.tokens.positiveLookahead(Lexer.Token.Type.TILDE)) {
         return this.parseTildeRange();
      } else if (this.tokens.positiveLookahead(Lexer.Token.Type.CARET)) {
         return this.parseCaretRange();
      } else if (this.isWildcardRange()) {
         return this.parseWildcardRange();
      } else if (this.isHyphenRange()) {
         return this.parseHyphenRange();
      } else {
         return this.isPartialVersionRange() ? this.parsePartialVersionRange() : this.parseComparisonRange();
      }
   }

   private CompositeExpression parseComparisonRange() {
      Lexer.Token token = this.tokens.lookahead();
      CompositeExpression expr;
      switch (token.type) {
         case EQUAL:
            this.tokens.consume();
            expr = CompositeExpression.Helper.eq(this.parseVersion());
            break;
         case NOT_EQUAL:
            this.tokens.consume();
            expr = CompositeExpression.Helper.neq(this.parseVersion());
            break;
         case GREATER:
            this.tokens.consume();
            expr = CompositeExpression.Helper.gt(this.parseVersion());
            break;
         case GREATER_EQUAL:
            this.tokens.consume();
            expr = CompositeExpression.Helper.gte(this.parseVersion());
            break;
         case LESS:
            this.tokens.consume();
            expr = CompositeExpression.Helper.lt(this.parseVersion());
            break;
         case LESS_EQUAL:
            this.tokens.consume();
            expr = CompositeExpression.Helper.lte(this.parseVersion());
            break;
         default:
            expr = CompositeExpression.Helper.eq(this.parseVersion());
      }

      return expr;
   }

   private CompositeExpression parseTildeRange() {
      this.consumeNextToken(Lexer.Token.Type.TILDE);
      int major = this.intOf(this.consumeNextToken(Lexer.Token.Type.NUMERIC).lexeme);
      if (!this.tokens.positiveLookahead(Lexer.Token.Type.DOT)) {
         return CompositeExpression.Helper.gte(this.versionFor(major)).and(CompositeExpression.Helper.lt(this.versionFor(major + 1)));
      } else {
         this.consumeNextToken(Lexer.Token.Type.DOT);
         int minor = this.intOf(this.consumeNextToken(Lexer.Token.Type.NUMERIC).lexeme);
         if (!this.tokens.positiveLookahead(Lexer.Token.Type.DOT)) {
            return CompositeExpression.Helper.gte(this.versionFor(major, minor)).and(CompositeExpression.Helper.lt(this.versionFor(major, minor + 1)));
         } else {
            this.consumeNextToken(Lexer.Token.Type.DOT);
            int patch = this.intOf(this.consumeNextToken(Lexer.Token.Type.NUMERIC).lexeme);
            return CompositeExpression.Helper.gte(this.versionFor(major, minor, patch)).and(CompositeExpression.Helper.lt(this.versionFor(major, minor + 1)));
         }
      }
   }

   private CompositeExpression parseCaretRange() {
      this.consumeNextToken(Lexer.Token.Type.CARET);
      int major = this.intOf(this.consumeNextToken(Lexer.Token.Type.NUMERIC).lexeme);
      if (!this.tokens.positiveLookahead(Lexer.Token.Type.DOT)) {
         return CompositeExpression.Helper.gte(this.versionFor(major)).and(CompositeExpression.Helper.lt(this.versionFor(major + 1)));
      } else {
         this.consumeNextToken(Lexer.Token.Type.DOT);
         int minor = this.intOf(this.consumeNextToken(Lexer.Token.Type.NUMERIC).lexeme);
         if (!this.tokens.positiveLookahead(Lexer.Token.Type.DOT)) {
            Version lower = this.versionFor(major, minor);
            Version upper = major > 0 ? lower.incrementMajorVersion() : lower.incrementMinorVersion();
            return CompositeExpression.Helper.gte(lower).and(CompositeExpression.Helper.lt(upper));
         } else {
            this.consumeNextToken(Lexer.Token.Type.DOT);
            int patch = this.intOf(this.consumeNextToken(Lexer.Token.Type.NUMERIC).lexeme);
            Version version = this.versionFor(major, minor, patch);
            CompositeExpression gte = CompositeExpression.Helper.gte(version);
            if (major > 0) {
               return gte.and(CompositeExpression.Helper.lt(version.incrementMajorVersion()));
            } else if (minor > 0) {
               return gte.and(CompositeExpression.Helper.lt(version.incrementMinorVersion()));
            } else {
               return patch > 0 ? gte.and(CompositeExpression.Helper.lt(version.incrementPatchVersion())) : CompositeExpression.Helper.eq(version);
            }
         }
      }
   }

   private boolean isWildcardRange() {
      return this.isVersionFollowedBy(Lexer.Token.Type.WILDCARD);
   }

   private CompositeExpression parseWildcardRange() {
      if (this.tokens.positiveLookahead(Lexer.Token.Type.WILDCARD)) {
         this.tokens.consume();
         return CompositeExpression.Helper.gte(this.versionFor(0, 0, 0));
      } else {
         int major = this.intOf(this.consumeNextToken(Lexer.Token.Type.NUMERIC).lexeme);
         this.consumeNextToken(Lexer.Token.Type.DOT);
         if (this.tokens.positiveLookahead(Lexer.Token.Type.WILDCARD)) {
            this.tokens.consume();
            return CompositeExpression.Helper.gte(this.versionFor(major)).and(CompositeExpression.Helper.lt(this.versionFor(major + 1)));
         } else {
            int minor = this.intOf(this.consumeNextToken(Lexer.Token.Type.NUMERIC).lexeme);
            this.consumeNextToken(Lexer.Token.Type.DOT);
            this.consumeNextToken(Lexer.Token.Type.WILDCARD);
            return CompositeExpression.Helper.gte(this.versionFor(major, minor)).and(CompositeExpression.Helper.lt(this.versionFor(major, minor + 1)));
         }
      }
   }

   private boolean isHyphenRange() {
      return this.isVersionFollowedBy(Lexer.Token.Type.HYPHEN);
   }

   private CompositeExpression parseHyphenRange() {
      CompositeExpression gte = CompositeExpression.Helper.gte(this.parseVersion());
      this.consumeNextToken(Lexer.Token.Type.HYPHEN);
      return gte.and(CompositeExpression.Helper.lte(this.parseVersion()));
   }

   private boolean isPartialVersionRange() {
      if (!this.tokens.positiveLookahead(Lexer.Token.Type.NUMERIC)) {
         return false;
      } else {
         EnumSet<Lexer.Token.Type> expected = EnumSet.complementOf(EnumSet.of(Lexer.Token.Type.NUMERIC, Lexer.Token.Type.DOT));
         return this.tokens.positiveLookaheadUntil(5, expected.toArray(new Lexer.Token.Type[expected.size()]));
      }
   }

   private CompositeExpression parsePartialVersionRange() {
      int major = this.intOf(this.consumeNextToken(Lexer.Token.Type.NUMERIC).lexeme);
      if (!this.tokens.positiveLookahead(Lexer.Token.Type.DOT)) {
         return CompositeExpression.Helper.gte(this.versionFor(major)).and(CompositeExpression.Helper.lt(this.versionFor(major + 1)));
      } else {
         this.consumeNextToken(Lexer.Token.Type.DOT);
         int minor = this.intOf(this.consumeNextToken(Lexer.Token.Type.NUMERIC).lexeme);
         return CompositeExpression.Helper.gte(this.versionFor(major, minor)).and(CompositeExpression.Helper.lt(this.versionFor(major, minor + 1)));
      }
   }

   private Version parseVersion() {
      int major = this.intOf(this.consumeNextToken(Lexer.Token.Type.NUMERIC).lexeme);
      int minor = 0;
      if (this.tokens.positiveLookahead(Lexer.Token.Type.DOT)) {
         this.tokens.consume();
         minor = this.intOf(this.consumeNextToken(Lexer.Token.Type.NUMERIC).lexeme);
      }

      int patch = 0;
      if (this.tokens.positiveLookahead(Lexer.Token.Type.DOT)) {
         this.tokens.consume();
         patch = this.intOf(this.consumeNextToken(Lexer.Token.Type.NUMERIC).lexeme);
      }

      return this.versionFor(major, minor, patch);
   }

   private boolean isVersionFollowedBy(Stream.ElementType<Lexer.Token> type) {
      EnumSet<Lexer.Token.Type> expected = EnumSet.of(Lexer.Token.Type.NUMERIC, Lexer.Token.Type.DOT);
      Iterator<Lexer.Token> it = this.tokens.iterator();
      Lexer.Token lookahead = null;

      while (it.hasNext()) {
         lookahead = it.next();
         if (!expected.contains(lookahead.type)) {
            break;
         }
      }

      return type.isMatchedBy(lookahead);
   }

   private Version versionFor(int major) {
      return this.versionFor(major, 0, 0);
   }

   private Version versionFor(int major, int minor) {
      return this.versionFor(major, minor, 0);
   }

   private Version versionFor(int major, int minor, int patch) {
      return Version.forIntegers(major, minor, patch);
   }

   private int intOf(String value) {
      return Integer.parseInt(value);
   }

   private Lexer.Token consumeNextToken(Lexer.Token.Type... expected) {
      try {
         return this.tokens.consume(expected);
      } catch (UnexpectedElementException var3) {
         throw new UnexpectedTokenException(var3);
      }
   }
}
