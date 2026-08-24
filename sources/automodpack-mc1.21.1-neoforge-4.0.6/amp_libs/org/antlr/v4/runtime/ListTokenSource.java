package amp_libs.org.antlr.v4.runtime;

import amp_libs.org.antlr.v4.runtime.misc.Pair;
import java.util.List;

public class ListTokenSource implements TokenSource {
   protected final List<? extends Token> tokens;
   private final String sourceName;
   protected int i;
   protected Token eofToken;
   private TokenFactory<?> _factory = CommonTokenFactory.DEFAULT;

   public ListTokenSource(List<? extends Token> tokens) {
      this(tokens, null);
   }

   public ListTokenSource(List<? extends Token> tokens, String sourceName) {
      if (tokens == null) {
         throw new NullPointerException("tokens cannot be null");
      } else {
         this.tokens = tokens;
         this.sourceName = sourceName;
      }
   }

   @Override
   public int getCharPositionInLine() {
      if (this.i < this.tokens.size()) {
         return this.tokens.get(this.i).getCharPositionInLine();
      } else if (this.eofToken != null) {
         return this.eofToken.getCharPositionInLine();
      } else if (this.tokens.size() > 0) {
         Token lastToken = this.tokens.get(this.tokens.size() - 1);
         String tokenText = lastToken.getText();
         if (tokenText != null) {
            int lastNewLine = tokenText.lastIndexOf(10);
            if (lastNewLine >= 0) {
               return tokenText.length() - lastNewLine - 1;
            }
         }

         return lastToken.getCharPositionInLine() + lastToken.getStopIndex() - lastToken.getStartIndex() + 1;
      } else {
         return 0;
      }
   }

   @Override
   public Token nextToken() {
      if (this.i >= this.tokens.size()) {
         if (this.eofToken == null) {
            int start = -1;
            if (this.tokens.size() > 0) {
               int previousStop = this.tokens.get(this.tokens.size() - 1).getStopIndex();
               if (previousStop != -1) {
                  start = previousStop + 1;
               }
            }

            int stop = Math.max(-1, start - 1);
            this.eofToken = this._factory
               .create(new Pair<>(this, this.getInputStream()), -1, "EOF", 0, start, stop, this.getLine(), this.getCharPositionInLine());
         }

         return this.eofToken;
      } else {
         Token t = this.tokens.get(this.i);
         if (this.i == this.tokens.size() - 1 && t.getType() == -1) {
            this.eofToken = t;
         }

         this.i++;
         return t;
      }
   }

   @Override
   public int getLine() {
      if (this.i < this.tokens.size()) {
         return this.tokens.get(this.i).getLine();
      } else if (this.eofToken != null) {
         return this.eofToken.getLine();
      } else if (this.tokens.size() <= 0) {
         return 1;
      } else {
         Token lastToken = this.tokens.get(this.tokens.size() - 1);
         int line = lastToken.getLine();
         String tokenText = lastToken.getText();
         if (tokenText != null) {
            for (int i = 0; i < tokenText.length(); i++) {
               if (tokenText.charAt(i) == '\n') {
                  line++;
               }
            }
         }

         return line;
      }
   }

   @Override
   public CharStream getInputStream() {
      if (this.i < this.tokens.size()) {
         return this.tokens.get(this.i).getInputStream();
      } else if (this.eofToken != null) {
         return this.eofToken.getInputStream();
      } else {
         return this.tokens.size() > 0 ? this.tokens.get(this.tokens.size() - 1).getInputStream() : null;
      }
   }

   @Override
   public String getSourceName() {
      if (this.sourceName != null) {
         return this.sourceName;
      } else {
         CharStream inputStream = this.getInputStream();
         return inputStream != null ? inputStream.getSourceName() : "List";
      }
   }

   @Override
   public void setTokenFactory(TokenFactory<?> factory) {
      this._factory = factory;
   }

   @Override
   public TokenFactory<?> getTokenFactory() {
      return this._factory;
   }
}
