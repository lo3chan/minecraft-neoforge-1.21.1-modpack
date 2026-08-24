package de.odysseus.ithaka.digraph.util.fas;

import de.odysseus.ithaka.digraph.Digraph;
import de.odysseus.ithaka.digraph.Digraphs;
import de.odysseus.ithaka.digraph.EdgeWeights;
import de.odysseus.ithaka.digraph.MapDigraph;
import java.util.ArrayList;
import java.util.List;
import java.util.OptionalInt;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public abstract class AbstractFeedbackArcSetProvider implements FeedbackArcSetProvider {
   private final ExecutorService executor;

   protected AbstractFeedbackArcSetProvider() {
      this.executor = null;
   }

   protected AbstractFeedbackArcSetProvider(int numberOfThreads) {
      if (numberOfThreads > 0) {
         this.executor = Executors.newFixedThreadPool(numberOfThreads);
      } else {
         this.executor = null;
      }
   }

   protected <V> Digraph<V> mfas(Digraph<V> digraph, EdgeWeights<? super V> weights) {
      return null;
   }

   protected abstract <V> Digraph<V> lfas(Digraph<V> var1, EdgeWeights<? super V> var2);

   private <V> FeedbackArcSet<V> fas(Digraph<V> digraph, EdgeWeights<? super V> weights, FeedbackArcSetPolicy policy) {
      EdgeWeights<? super V> filteredWeights = weights;
      if (policy == FeedbackArcSetPolicy.MIN_SIZE) {
         int delta = this.totalWeight(digraph, weights);
         filteredWeights = (source, target) -> {
            OptionalInt original = weights.get(source, target);
            return original.isPresent() ? OptionalInt.of(original.getAsInt() + delta) : OptionalInt.empty();
         };
      }

      Digraph<V> result = this.mfas(digraph, filteredWeights);
      boolean exact = true;
      if (result == null) {
         result = this.lfas(digraph, filteredWeights);
         exact = false;
      }

      return new FeedbackArcSet<>(result, this.totalWeight(result, weights), policy, exact);
   }

   protected <V> int totalWeight(Digraph<V> digraph, EdgeWeights<? super V> weights) {
      int weight = 0;

      for (V source : digraph.vertices()) {
         for (V target : digraph.targets(source)) {
            weight += weights.get(source, target).getAsInt();
         }
      }

      return weight;
   }

   private <V> List<FeedbackArcSet<V>> executeAll(List<AbstractFeedbackArcSetProvider.FeedbackTask<V>> tasks) {
      List<FeedbackArcSet<V>> result = new ArrayList<>();
      if (this.executor == null) {
         for (AbstractFeedbackArcSetProvider.FeedbackTask<V> task : tasks) {
            result.add(task.call());
         }
      } else {
         try {
            for (Future<FeedbackArcSet<V>> future : this.executor.invokeAll(tasks)) {
               result.add(future.get());
            }
         } catch (InterruptedException | ExecutionException var5) {
            var5.printStackTrace();
            return null;
         }
      }

      return result;
   }

   @Override
   public <V> FeedbackArcSet<V> getFeedbackArcSet(Digraph<V> digraph, EdgeWeights<? super V> weights, FeedbackArcSetPolicy policy) {
      if (Digraphs.isTriviallyAcyclic(digraph)) {
         return FeedbackArcSet.empty(policy);
      } else {
         List<Set<V>> components = Digraphs.scc(digraph);
         if (components.size() == digraph.getVertexCount()) {
            return FeedbackArcSet.empty(policy);
         } else if (components.size() == 1) {
            return this.fas(digraph, weights, policy);
         } else {
            List<AbstractFeedbackArcSetProvider.FeedbackTask<V>> tasks = new ArrayList<>();

            for (Set<V> component : components) {
               if (component.size() > 1) {
                  tasks.add(new AbstractFeedbackArcSetProvider.FeedbackTask<>(digraph, weights, policy, component));
               }
            }

            List<FeedbackArcSet<V>> feedbacks = this.executeAll(tasks);
            if (feedbacks == null) {
               return null;
            } else {
               int weight = 0;
               boolean exact = true;
               Digraph<V> result = new MapDigraph<>();

               for (FeedbackArcSet<V> feedback : feedbacks) {
                  for (V source : feedback.vertices()) {
                     for (V target : feedback.targets(source)) {
                        result.put(source, target, digraph.get(source, target).getAsInt());
                     }
                  }

                  exact &= feedback.isExact();
                  weight += feedback.getWeight();
               }

               return new FeedbackArcSet<>(result, weight, policy, exact);
            }
         }
      }
   }

   class FeedbackTask<V> implements Callable<FeedbackArcSet<V>> {
      final Digraph<V> digraph;
      final EdgeWeights<? super V> weights;
      final FeedbackArcSetPolicy policy;
      final Set<V> scc;

      FeedbackTask(Digraph<V> digraph, EdgeWeights<? super V> weights, FeedbackArcSetPolicy policy, Set<V> scc) {
         this.digraph = digraph;
         this.weights = weights;
         this.policy = policy;
         this.scc = scc;
      }

      public FeedbackArcSet<V> call() {
         return AbstractFeedbackArcSetProvider.this.fas(this.digraph.subgraph(this.scc), this.weights, this.policy);
      }
   }
}
