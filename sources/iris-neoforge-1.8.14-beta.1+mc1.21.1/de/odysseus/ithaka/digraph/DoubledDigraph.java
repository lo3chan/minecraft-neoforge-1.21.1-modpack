package de.odysseus.ithaka.digraph;

public interface DoubledDigraph<V> extends Digraph<V> {
   int getInDegree(V var1);

   Iterable<V> sources(V var1);

   DoubledDigraph<V> reverse();
}
