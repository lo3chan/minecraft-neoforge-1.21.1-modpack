package amp_libs.org.antlr.v4.runtime.dfa;

import amp_libs.org.antlr.v4.runtime.Vocabulary;
import amp_libs.org.antlr.v4.runtime.VocabularyImpl;
import java.util.Arrays;

public class DFASerializer {
   private final DFA dfa;
   private final Vocabulary vocabulary;

   @Deprecated
   public DFASerializer(DFA dfa, String[] tokenNames) {
      this(dfa, VocabularyImpl.fromTokenNames(tokenNames));
   }

   public DFASerializer(DFA dfa, Vocabulary vocabulary) {
      this.dfa = dfa;
      this.vocabulary = vocabulary;
   }

   @Override
   public String toString() {
      if (this.dfa.s0 == null) {
         return null;
      } else {
         StringBuilder buf = new StringBuilder();

         for (DFAState s : this.dfa.getStates()) {
            int n = 0;
            if (s.edges != null) {
               n = s.edges.length;
            }

            for (int i = 0; i < n; i++) {
               DFAState t = s.edges[i];
               if (t != null && t.stateNumber != 2147483647) {
                  buf.append(this.getStateString(s));
                  String label = this.getEdgeLabel(i);
                  buf.append("-").append(label).append("->").append(this.getStateString(t)).append('\n');
               }
            }
         }

         String output = buf.toString();
         return output.length() == 0 ? null : output;
      }
   }

   protected String getEdgeLabel(int i) {
      return this.vocabulary.getDisplayName(i - 1);
   }

   protected String getStateString(DFAState s) {
      int n = s.stateNumber;
      String baseStateStr = (s.isAcceptState ? ":" : "") + "s" + n + (s.requiresFullContext ? "^" : "");
      if (s.isAcceptState) {
         return s.predicates != null ? baseStateStr + "=>" + Arrays.toString((Object[])s.predicates) : baseStateStr + "=>" + s.prediction;
      } else {
         return baseStateStr;
      }
   }
}
