package amp_libs.org.antlr.v4.runtime.dfa;

import amp_libs.org.antlr.v4.runtime.atn.ATNConfig;
import amp_libs.org.antlr.v4.runtime.atn.ATNConfigSet;
import amp_libs.org.antlr.v4.runtime.atn.LexerActionExecutor;
import amp_libs.org.antlr.v4.runtime.atn.SemanticContext;
import amp_libs.org.antlr.v4.runtime.misc.MurmurHash;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class DFAState {
   public int stateNumber = -1;
   public ATNConfigSet configs = new ATNConfigSet();
   public DFAState[] edges;
   public boolean isAcceptState = false;
   public int prediction;
   public LexerActionExecutor lexerActionExecutor;
   public boolean requiresFullContext;
   public DFAState.PredPrediction[] predicates;

   public DFAState() {
   }

   public DFAState(int stateNumber) {
      this.stateNumber = stateNumber;
   }

   public DFAState(ATNConfigSet configs) {
      this.configs = configs;
   }

   public Set<Integer> getAltSet() {
      Set<Integer> alts = new HashSet<>();
      if (this.configs != null) {
         for (ATNConfig c : this.configs) {
            alts.add(c.alt);
         }
      }

      return alts.isEmpty() ? null : alts;
   }

   @Override
   public int hashCode() {
      int hash = MurmurHash.initialize(7);
      hash = MurmurHash.update(hash, this.configs.hashCode());
      return MurmurHash.finish(hash, 1);
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (!(o instanceof DFAState)) {
         return false;
      } else {
         DFAState other = (DFAState)o;
         return this.configs.equals(other.configs);
      }
   }

   @Override
   public String toString() {
      StringBuilder buf = new StringBuilder();
      buf.append(this.stateNumber).append(":").append(this.configs);
      if (this.isAcceptState) {
         buf.append("=>");
         if (this.predicates != null) {
            buf.append(Arrays.toString((Object[])this.predicates));
         } else {
            buf.append(this.prediction);
         }
      }

      return buf.toString();
   }

   public static class PredPrediction {
      public SemanticContext pred;
      public int alt;

      public PredPrediction(SemanticContext pred, int alt) {
         this.alt = alt;
         this.pred = pred;
      }

      @Override
      public String toString() {
         return "(" + this.pred + ", " + this.alt + ")";
      }
   }
}
