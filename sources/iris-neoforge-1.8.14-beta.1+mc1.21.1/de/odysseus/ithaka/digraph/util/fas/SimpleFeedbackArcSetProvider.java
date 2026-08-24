package de.odysseus.ithaka.digraph.util.fas;

import de.odysseus.ithaka.digraph.Digraph;
import de.odysseus.ithaka.digraph.Digraphs;
import de.odysseus.ithaka.digraph.EdgeWeights;
import de.odysseus.ithaka.digraph.MapDigraph;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Set;

public class SimpleFeedbackArcSetProvider extends AbstractFeedbackArcSetProvider {
   public SimpleFeedbackArcSetProvider() {
   }

   public SimpleFeedbackArcSetProvider(int numberOfThreads) {
      super(numberOfThreads);
   }

   private <V> List<Digraph<V>> copies(Digraph<V> digraph, int count) {
      List<Digraph<V>> copies = new ArrayList<>();
      copies.add(digraph);
      List<Integer> shuffle = new ArrayList<>();
      Map<V, Integer> order = new HashMap<>();
      int index = 0;

      for (V source : digraph.vertices()) {
         order.put(source, index);
         shuffle.add(index++);
      }

      Random random = new Random(7L);

      for (int i = 0; i < count; i++) {
         Collections.shuffle(shuffle, random);
         List<Integer> mapping = new ArrayList<>(shuffle);
         copies.add(Digraphs.copy(digraph, () -> new MapDigraph<>((v1, v2) -> {
            int value1 = mapping.get(order.get(v1));
            int value2 = mapping.get(order.get(v2));
            return Integer.compare(value1, value2);
         })));
      }

      return copies;
   }

   @Override
   protected <V> Digraph<V> lfas(Digraph<V> tangle, EdgeWeights<? super V> weights) {
      int minWeight = 2147483647;
      int minSize = 2147483647;
      List<V> minFinished = null;
      int maxIterationsLeft = Math.max(1, 1000000 / (tangle.getVertexCount() + tangle.getEdgeCount()));
      List<Digraph<V>> copies = this.copies(tangle, Math.min(10, tangle.getVertexCount()));
      List<V> finished = new ArrayList<>(tangle.getVertexCount());
      Set<V> discovered = new HashSet<>(tangle.getVertexCount());

      for (V start : tangle.vertices()) {
         for (Digraph<V> copy : copies) {
            finished.clear();
            discovered.clear();
            Digraphs.dfs(copy, start, discovered, finished);

            assert finished.size() == tangle.getVertexCount();

            int weight = 0;
            int size = 0;
            discovered.clear();

            for (V source : finished) {
               discovered.add(source);

               for (V target : tangle.targets(source)) {
                  if (!discovered.contains(target)) {
                     weight += weights.get(source, target).getAsInt();
                     size++;
                  }
               }

               if (weight > minWeight) {
                  break;
               }
            }

            if (weight < minWeight || weight == minWeight && size < minSize) {
               minFinished = new ArrayList<>(finished);
               minWeight = weight;
               minSize = size;
            }
         }

         if (--maxIterationsLeft == 0) {
            break;
         }
      }

      Objects.requireNonNull(minFinished);
      Digraph<V> feedback = MapDigraph.<V>getDefaultDigraphFactory().create();
      discovered.clear();

      for (V source : minFinished) {
         discovered.add(source);

         for (V targetx : tangle.targets(source)) {
            if (!discovered.contains(targetx)) {
               feedback.put(source, targetx, tangle.get(source, targetx).getAsInt());
            }
         }
      }

      return feedback;
   }
}
