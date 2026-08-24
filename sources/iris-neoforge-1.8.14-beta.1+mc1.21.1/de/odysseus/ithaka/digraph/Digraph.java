package de.odysseus.ithaka.digraph;

import java.util.Collection;
import java.util.OptionalInt;
import java.util.Set;

public interface Digraph<V> extends EdgeWeights<V> {
   @Override
   OptionalInt get(V var1, V var2);

   boolean contains(V var1, V var2);

   boolean contains(V var1);

   boolean add(V var1);

   OptionalInt put(V var1, V var2, int var3);

   OptionalInt remove(V var1, V var2);

   boolean remove(V var1);

   void removeAll(Collection<V> var1);

   Iterable<V> vertices();

   Iterable<V> targets(V var1);

   int getVertexCount();

   int totalWeight();

   int getOutDegree(V var1);

   int getEdgeCount();

   boolean isAcyclic();

   Digraph<V> reverse();

   Digraph<V> subgraph(Set<V> var1);
}
