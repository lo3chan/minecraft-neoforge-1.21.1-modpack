package amp_libs.org.antlr.v4.runtime.atn;

import amp_libs.org.antlr.v4.runtime.dfa.DFAState;
import java.util.IdentityHashMap;

public abstract class ATNSimulator {
   public static final DFAState ERROR = new DFAState(new ATNConfigSet());
   public final ATN atn;
   protected final PredictionContextCache sharedContextCache;

   public ATNSimulator(ATN atn, PredictionContextCache sharedContextCache) {
      this.atn = atn;
      this.sharedContextCache = sharedContextCache;
   }

   public abstract void reset();

   public void clearDFA() {
      throw new UnsupportedOperationException("This ATN simulator does not support clearing the DFA.");
   }

   public PredictionContextCache getSharedContextCache() {
      return this.sharedContextCache;
   }

   public PredictionContext getCachedContext(PredictionContext context) {
      if (this.sharedContextCache == null) {
         return context;
      } else {
         synchronized (this.sharedContextCache) {
            IdentityHashMap<PredictionContext, PredictionContext> visited = new IdentityHashMap<>();
            return PredictionContext.getCachedContext(context, this.sharedContextCache, visited);
         }
      }
   }

   static {
      ERROR.stateNumber = 2147483647;
   }
}
