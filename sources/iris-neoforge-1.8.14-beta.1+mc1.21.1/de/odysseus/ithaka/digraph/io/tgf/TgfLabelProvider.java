package de.odysseus.ithaka.digraph.io.tgf;

public interface TgfLabelProvider<V> {
   String getVertexLabel(V var1);

   String getEdgeLabel(int var1);
}
