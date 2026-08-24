package de.odysseus.ithaka.digraph.io.dot;

import de.odysseus.ithaka.digraph.Digraph;

public interface DotProvider<V, G extends Digraph<? extends V>> {
   Iterable<DotAttribute> getDefaultGraphAttributes(G var1);

   Iterable<DotAttribute> getDefaultNodeAttributes(G var1);

   Iterable<DotAttribute> getDefaultEdgeAttributes(G var1);

   String getNodeId(V var1);

   Iterable<DotAttribute> getNodeAttributes(V var1);

   Iterable<DotAttribute> getEdgeAttributes(V var1, V var2, int var3);

   Iterable<DotAttribute> getSubgraphAttributes(G var1, V var2);
}
