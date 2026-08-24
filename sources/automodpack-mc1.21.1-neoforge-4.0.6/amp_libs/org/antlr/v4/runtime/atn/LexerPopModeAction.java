package amp_libs.org.antlr.v4.runtime.atn;

import amp_libs.org.antlr.v4.runtime.Lexer;
import amp_libs.org.antlr.v4.runtime.misc.MurmurHash;

public final class LexerPopModeAction implements LexerAction {
   public static final LexerPopModeAction INSTANCE = new LexerPopModeAction();

   private LexerPopModeAction() {
   }

   @Override
   public LexerActionType getActionType() {
      return LexerActionType.POP_MODE;
   }

   @Override
   public boolean isPositionDependent() {
      return false;
   }

   @Override
   public void execute(Lexer lexer) {
      lexer.popMode();
   }

   @Override
   public int hashCode() {
      int hash = MurmurHash.initialize();
      hash = MurmurHash.update(hash, this.getActionType().ordinal());
      return MurmurHash.finish(hash, 1);
   }

   @Override
   public boolean equals(Object obj) {
      return obj == this;
   }

   @Override
   public String toString() {
      return "popMode";
   }
}
