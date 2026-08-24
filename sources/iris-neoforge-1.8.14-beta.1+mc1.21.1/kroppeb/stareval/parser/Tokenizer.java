package kroppeb.stareval.parser;

import kroppeb.stareval.element.ExpressionElement;
import kroppeb.stareval.exception.ParseException;
import kroppeb.stareval.exception.UnexpectedCharacterException;
import kroppeb.stareval.exception.UnexpectedEndingException;

class Tokenizer {
   private Tokenizer() {
   }

   static ExpressionElement parse(String input, ParserOptions options) throws ParseException {
      return parseInternal(new StringReader(input), options);
   }

   static ExpressionElement parseInternal(StringReader input, ParserOptions options) throws ParseException {
      Parser stack = new Parser();
      ParserOptions.TokenRules tokenRules = options.getTokenRules();

      while (input.canRead()) {
         input.skipWhitespace();
         if (input.canRead()) {
            char c = input.read();
            if (tokenRules.isIdStart(c)) {
               String id = readWhile(input, tokenRules::isIdPart);
               stack.visitId(id);
               continue;
            } else if (c == '.' && stack.canReadAccess()) {
               input.skipWhitespace();
               if (!input.canRead()) {
                  throw new UnexpectedEndingException("An expression can't end with '.'");
               }

               char start = input.read();
               if (!tokenRules.isAccessStart(start)) {
                  throw new UnexpectedCharacterException("a valid accessor", start, input.getCurrentIndex());
               }

               String access = readWhile(input, tokenRules::isAccessPart);
               stack.visitAccess(access);
               continue;
            } else if (tokenRules.isNumberStart(c)) {
               String numberString = readWhile(input, tokenRules::isNumberPart);
               stack.visitNumber(numberString);
               continue;
            } else if (c == '(') {
               stack.visitOpeningParenthesis();
               continue;
            } else if (c == ',') {
               stack.visitComma(input.getCurrentIndex());
               continue;
            } else if (c == ')') {
               stack.visitClosingParenthesis(input.getCurrentIndex());
               continue;
            } else {
               if (stack.canReadBinaryOp()) {
                  OpResolver<? extends BinaryOp> resolver = options.getBinaryOpResolver(c);
                  if (resolver != null) {
                     stack.visitBinaryOperator(resolver.resolve(input));
                     continue;
                  }
               } else {
                  OpResolver<? extends UnaryOp> resolver = options.getUnaryOpResolver(c);
                  if (resolver != null) {
                     stack.visitUnaryOperator(resolver.resolve(input));
                     continue;
                  }
               }

               throw new UnexpectedCharacterException(c, input.getCurrentIndex());
            }
         }
         break;
      }

      return stack.getFinal(input.getCurrentIndex());
   }

   private static String readWhile(StringReader input, Tokenizer.CharPredicate predicate) {
      input.mark();

      while (input.canRead() && predicate.test(input.peek())) {
         input.skipOneCharacter();
      }

      return input.substring();
   }

   private interface CharPredicate {
      boolean test(char var1);
   }
}
