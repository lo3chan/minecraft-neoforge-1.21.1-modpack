package de.odysseus.ithaka.digraph;

public interface DigraphFactory<G extends Digraph<?>> {
   G create();
}
