package de.odysseus.ithaka.digraph;

public interface DigraphProvider<T, G extends Digraph<?>> {
   G get(T var1);
}
