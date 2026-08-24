package amp_libs.org.antlr.v4.runtime.atn;

import amp_libs.org.antlr.v4.runtime.Lexer;

public interface LexerAction {
   LexerActionType getActionType();

   boolean isPositionDependent();

   void execute(Lexer var1);
}
