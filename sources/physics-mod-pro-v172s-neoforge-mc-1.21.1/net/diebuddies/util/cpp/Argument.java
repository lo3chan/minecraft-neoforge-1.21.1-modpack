package net.diebuddies.util.cpp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nonnull;

class Argument extends ArrayList<Token> {
   private List<Token> expansion = null;

   public Argument() {
   }

   public void addToken(@Nonnull Token tok) {
      this.add(tok);
   }

   void expand(@Nonnull Preprocessor p) throws IOException, LexerException {
      if (this.expansion == null) {
         this.expansion = p.expand(this);
      }
   }

   @Nonnull
   public Iterator<Token> expansion() {
      return this.expansion.iterator();
   }

   @Override
   public String toString() {
      StringBuilder buf = new StringBuilder();
      buf.append("Argument(");
      buf.append("raw=[ ");

      for (int i = 0; i < this.size(); i++) {
         buf.append(this.get(i).getText());
      }

      buf.append(" ];expansion=[ ");
      if (this.expansion == null) {
         buf.append("null");
      } else {
         for (Token token : this.expansion) {
            buf.append(token.getText());
         }
      }

      buf.append(" ])");
      return buf.toString();
   }
}
