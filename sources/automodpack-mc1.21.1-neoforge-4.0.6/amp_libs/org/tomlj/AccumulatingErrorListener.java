package amp_libs.org.tomlj;

import amp_libs.org.antlr.v4.runtime.BaseErrorListener;
import amp_libs.org.antlr.v4.runtime.InputMismatchException;
import amp_libs.org.antlr.v4.runtime.NoViableAltException;
import amp_libs.org.antlr.v4.runtime.RecognitionException;
import amp_libs.org.antlr.v4.runtime.Recognizer;
import amp_libs.org.antlr.v4.runtime.Token;
import amp_libs.org.antlr.v4.runtime.misc.IntervalSet;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

final class AccumulatingErrorListener extends BaseErrorListener implements ErrorReporter {
   private final List<TomlParseError> errors = new ArrayList<>();

   @Override
   public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPosition, String msg, RecognitionException e) {
      TomlPosition position = TomlPosition.positionAt(line, charPosition + 1);
      if (e instanceof InputMismatchException || e instanceof NoViableAltException) {
         String message = this.getMessage(e.getOffendingToken(), getExpected(e));
         this.reportError(message, position);
      } else if (offendingSymbol instanceof Token && recognizer instanceof amp_libs.org.antlr.v4.runtime.Parser) {
         String message = this.getMessage((Token)offendingSymbol, getExpected(((amp_libs.org.antlr.v4.runtime.Parser)recognizer).getExpectedTokens()));
         this.reportError(message, position);
      } else {
         this.reportError(msg, position);
      }
   }

   @Override
   public void reportError(TomlParseError error) {
      this.errors.add(error);
   }

   private void reportError(String message, TomlPosition position) {
      this.reportError(new TomlParseError(message, position));
   }

   List<TomlParseError> errors() {
      return this.errors;
   }

   private String getMessage(Token token, String expected) {
      return "Unexpected " + getTokenName(token) + ", expected " + expected;
   }

   private static String getTokenName(Token token) {
      int tokenType = token.getType();
      switch (tokenType) {
         case -1:
            return "end of input";
         case 16:
            return "end of line";
         default:
            String text = token.getText();
            return isOnlyQuotes(text) ? text : "'" + Toml.tomlEscape(token.getText()) + '\'';
      }
   }

   private static String getExpected(RecognitionException e) {
      IntervalSet expectedTokens = e.getExpectedTokens();
      return getExpected(expectedTokens);
   }

   private static String getExpected(IntervalSet expectedTokens) {
      List<String> sortedNames = expectedTokens.getIntervals()
         .stream()
         .flatMap(ix -> IntStream.rangeClosed(ix.a, ix.b).boxed())
         .flatMap(TokenName::namesForToken)
         .sorted()
         .distinct()
         .map(TokenName::displayName)
         .collect(Collectors.toList());
      StringBuilder builder = new StringBuilder();
      int count = sortedNames.size();

      for (int i = 0; i < count; i++) {
         builder.append(sortedNames.get(i));
         if (i < count - 2) {
            builder.append(", ");
         } else if (i == count - 2) {
            if (count >= 3) {
               builder.append(',');
            }

            builder.append(" or ");
         }
      }

      return builder.toString();
   }

   private static boolean isOnlyQuotes(String text) {
      int length = text.length();
      if (length == 0) {
         return false;
      } else {
         char first = text.charAt(0);
         if (first != '\'' && first != '"') {
            return false;
         } else {
            for (int i = 1; i < length; i++) {
               if (text.charAt(i) != first) {
                  return false;
               }
            }

            return true;
         }
      }
   }
}
