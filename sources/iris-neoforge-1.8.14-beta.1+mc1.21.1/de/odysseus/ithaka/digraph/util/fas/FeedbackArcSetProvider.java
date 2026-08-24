package de.odysseus.ithaka.digraph.util.fas;

import de.odysseus.ithaka.digraph.Digraph;
import de.odysseus.ithaka.digraph.EdgeWeights;

public interface FeedbackArcSetProvider {
   <V> FeedbackArcSet<V> getFeedbackArcSet(Digraph<V> var1, EdgeWeights<? super V> var2, FeedbackArcSetPolicy var3);
}
