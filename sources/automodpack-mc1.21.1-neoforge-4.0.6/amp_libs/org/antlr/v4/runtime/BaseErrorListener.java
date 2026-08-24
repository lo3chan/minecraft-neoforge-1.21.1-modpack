package amp_libs.org.antlr.v4.runtime;

import amp_libs.org.antlr.v4.runtime.atn.ATNConfigSet;
import amp_libs.org.antlr.v4.runtime.dfa.DFA;
import java.util.BitSet;

public class BaseErrorListener implements ANTLRErrorListener {
   @Override
   public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {
   }

   @Override
   public void reportAmbiguity(Parser recognizer, DFA dfa, int startIndex, int stopIndex, boolean exact, BitSet ambigAlts, ATNConfigSet configs) {
   }

   @Override
   public void reportAttemptingFullContext(Parser recognizer, DFA dfa, int startIndex, int stopIndex, BitSet conflictingAlts, ATNConfigSet configs) {
   }

   @Override
   public void reportContextSensitivity(Parser recognizer, DFA dfa, int startIndex, int stopIndex, int prediction, ATNConfigSet configs) {
   }
}
