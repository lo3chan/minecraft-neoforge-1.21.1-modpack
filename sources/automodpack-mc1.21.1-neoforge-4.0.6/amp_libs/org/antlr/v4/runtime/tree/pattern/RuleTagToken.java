package amp_libs.org.antlr.v4.runtime.tree.pattern;

import amp_libs.org.antlr.v4.runtime.CharStream;
import amp_libs.org.antlr.v4.runtime.Token;
import amp_libs.org.antlr.v4.runtime.TokenSource;

public class RuleTagToken implements Token {
   private final String ruleName;
   private final int bypassTokenType;
   private final String label;

   public RuleTagToken(String ruleName, int bypassTokenType) {
      this(ruleName, bypassTokenType, null);
   }

   public RuleTagToken(String ruleName, int bypassTokenType, String label) {
      if (ruleName != null && !ruleName.isEmpty()) {
         this.ruleName = ruleName;
         this.bypassTokenType = bypassTokenType;
         this.label = label;
      } else {
         throw new IllegalArgumentException("ruleName cannot be null or empty.");
      }
   }

   public final String getRuleName() {
      return this.ruleName;
   }

   public final String getLabel() {
      return this.label;
   }

   @Override
   public int getChannel() {
      return 0;
   }

   @Override
   public String getText() {
      return this.label != null ? "<" + this.label + ":" + this.ruleName + ">" : "<" + this.ruleName + ">";
   }

   @Override
   public int getType() {
      return this.bypassTokenType;
   }

   @Override
   public int getLine() {
      return 0;
   }

   @Override
   public int getCharPositionInLine() {
      return -1;
   }

   @Override
   public int getTokenIndex() {
      return -1;
   }

   @Override
   public int getStartIndex() {
      return -1;
   }

   @Override
   public int getStopIndex() {
      return -1;
   }

   @Override
   public TokenSource getTokenSource() {
      return null;
   }

   @Override
   public CharStream getInputStream() {
      return null;
   }

   @Override
   public String toString() {
      return this.ruleName + ":" + this.bypassTokenType;
   }
}
