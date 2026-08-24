package amp_libs.org.antlr.v4.runtime.dfa;

import amp_libs.org.antlr.v4.runtime.VocabularyImpl;

public class LexerDFASerializer extends DFASerializer {
   public LexerDFASerializer(DFA dfa) {
      super(dfa, VocabularyImpl.EMPTY_VOCABULARY);
   }

   @Override
   protected String getEdgeLabel(int i) {
      return new StringBuilder("'").appendCodePoint(i).append("'").toString();
   }
}
