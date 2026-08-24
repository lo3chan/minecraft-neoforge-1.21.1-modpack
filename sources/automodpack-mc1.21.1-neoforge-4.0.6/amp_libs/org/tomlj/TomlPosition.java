package amp_libs.org.tomlj;

import amp_libs.org.antlr.v4.runtime.ParserRuleContext;
import amp_libs.org.antlr.v4.runtime.Token;

public final class TomlPosition {
   private final int line;
   private final int column;

   public static TomlPosition positionAt(int line, int column) {
      if (line < 1) {
         throw new IllegalArgumentException("line must be >= 1");
      } else if (column < 1) {
         throw new IllegalArgumentException("column must be >= 1");
      } else {
         return new TomlPosition(line, column);
      }
   }

   private TomlPosition(int line, int column) {
      this.line = line;
      this.column = column;
   }

   TomlPosition(ParserRuleContext ctx) {
      this(ctx, 0);
   }

   TomlPosition(ParserRuleContext ctx, int offset) {
      Token token = ctx.getStart();
      this.line = token.getLine();
      this.column = token.getCharPositionInLine() + 1 + offset;
   }

   public int line() {
      return this.line;
   }

   public int column() {
      return this.column;
   }

   @Override
   public boolean equals(Object obj) {
      if (obj == this) {
         return true;
      } else if (!(obj instanceof TomlPosition)) {
         return false;
      } else {
         TomlPosition other = (TomlPosition)obj;
         return this.line == other.line && this.column == other.column;
      }
   }

   @Override
   public int hashCode() {
      return 31 * this.line + this.column;
   }

   @Override
   public String toString() {
      return "line " + this.line + ", column " + this.column;
   }
}
