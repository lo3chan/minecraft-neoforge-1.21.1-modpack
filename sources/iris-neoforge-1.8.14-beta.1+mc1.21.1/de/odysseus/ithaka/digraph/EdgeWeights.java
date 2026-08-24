package de.odysseus.ithaka.digraph;

import java.util.OptionalInt;

public interface EdgeWeights<V> {
   OptionalInt UNIT_WEIGHT = OptionalInt.of(1);
   EdgeWeights<Object> UNIT_WEIGHTS = (source, target) -> UNIT_WEIGHT;

   OptionalInt get(V var1, V var2);
}
