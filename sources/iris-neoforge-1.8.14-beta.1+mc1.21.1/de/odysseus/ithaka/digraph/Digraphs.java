package de.odysseus.ithaka.digraph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.OptionalInt;
import java.util.Set;
import java.util.Stack;

public class Digraphs {
   public static <V> DoubledDigraph<V> emptyDigraph() {
      return new EmptyDigraph<>();
   }

   public static <V> Digraph<V> unmodifiableDigraph(Digraph<V> digraph) {
      return new UnmodifiableDigraph<>(digraph);
   }

   public static <V> List<V> toposort(Digraph<V> digraph, boolean descending) {
      List<V> finished = new ArrayList<>();
      Set<V> discovered = new HashSet<>(digraph.getVertexCount());

      for (V vertex : digraph.vertices()) {
         if (!discovered.contains(vertex)) {
            dfs(digraph, vertex, discovered, finished);
         }
      }

      if (!descending) {
         Collections.reverse(finished);
      }

      return finished;
   }

   public static <V> Set<V> closure(Digraph<V> digraph, V source) {
      Set<V> closure = new HashSet<>();
      dfs(digraph, source, closure, closure);
      return closure;
   }

   public static <V> boolean isTriviallyAcyclic(Digraph<V> digraph) {
      return digraph.getVertexCount() < 2;
   }

   public static <V> boolean isAcyclic(Digraph<V> digraph) {
      if (isTriviallyAcyclic(digraph)) {
         return true;
      } else {
         int n = digraph.getVertexCount();
         return digraph.getEdgeCount() > n * (n - 1) / 2 ? false : scc(digraph).size() == n;
      }
   }

   public static <V> boolean isEquivalent(Digraph<V> first, Digraph<V> second, boolean compareEdges) {
      if (first == second) {
         return true;
      } else if (first.getEdgeCount() == second.getEdgeCount() && first.getVertexCount() == second.getVertexCount()) {
         for (V source : first.vertices()) {
            if (!second.contains(source)) {
               return false;
            }

            for (V target : first.targets(source)) {
               OptionalInt secondEdge = second.get(source, target);
               if (secondEdge.isEmpty()) {
                  return false;
               }

               if (compareEdges) {
                  int edge1 = first.get(source, target).getAsInt();
                  int edge2 = secondEdge.getAsInt();
                  if (edge1 != edge2) {
                     return false;
                  }
               }
            }
         }

         return true;
      } else {
         return false;
      }
   }

   public static <V> boolean isStronglyConnected(Digraph<V> digraph) {
      int n = digraph.getVertexCount();
      return n < 2 ? true : scc(digraph).size() == 1;
   }

   public static <V> boolean isReachable(Digraph<V> digraph, V source, V target) {
      return digraph.contains(source, target) || closure(digraph, source).contains(target);
   }

   public static <V> void dfs(Digraph<V> digraph, V source, Set<? super V> discovered, Collection<? super V> finished) {
      if (discovered.add(source)) {
         for (V target : digraph.targets(source)) {
            dfs(digraph, target, discovered, finished);
         }

         finished.add(source);
      }
   }

   public static <V> void dfs2(Digraph<V> digraph, V source, Set<? super V> discovered, Collection<? super V> finished) {
      dfs2(digraph, digraph.reverse(), source, discovered, finished);
   }

   private static <V> void dfs2(Digraph<V> forward, Digraph<V> backward, V source, Set<? super V> discovered, Collection<? super V> finished) {
      if (discovered.add(source)) {
         for (V target : forward.targets(source)) {
            dfs2(forward, backward, target, discovered, finished);
         }

         for (V target : backward.targets(source)) {
            dfs2(forward, backward, target, discovered, finished);
         }

         finished.add(source);
      }
   }

   public static <V> List<Set<V>> scc(Digraph<V> digraph) {
      List<Set<V>> components = new ArrayList<>();
      Digraph<V> reverse = digraph.reverse();
      Stack<V> stack = new Stack<>();
      Set<V> discovered = new HashSet<>();

      for (V vertex : digraph.vertices()) {
         dfs(digraph, vertex, discovered, stack);
      }

      discovered = new HashSet<>();

      while (!stack.isEmpty()) {
         V vertex = stack.pop();
         if (!discovered.contains(vertex)) {
            Set<V> component = new HashSet<>();
            dfs(reverse, vertex, discovered, component);
            components.add(component);
         }
      }

      return components;
   }

   public static <V> List<Set<V>> wcc(Digraph<V> digraph) {
      List<Set<V>> components = new ArrayList<>();
      Digraph<V> reverse = digraph.reverse();
      Set<V> discovered = new HashSet<>();

      for (V vertex : digraph.vertices()) {
         if (!discovered.contains(vertex)) {
            Set<V> component = new HashSet<>();
            dfs2(digraph, reverse, vertex, discovered, component);
            components.add(component);
         }
      }

      return components;
   }

   public static <V, G extends Digraph<V>> G reverse(Digraph<V> digraph, DigraphFactory<? extends G> factory) {
      G reverse = (G)factory.create();

      for (V source : digraph.vertices()) {
         reverse.add(source);

         for (V target : digraph.targets(source)) {
            reverse.put(target, source, digraph.get(source, target).getAsInt());
         }
      }

      return reverse;
   }

   public static <V, G extends Digraph<V>> G copy(Digraph<V> digraph, DigraphFactory<? extends G> factory) {
      G result = (G)factory.create();

      for (V source : digraph.vertices()) {
         result.add(source);

         for (V target : digraph.targets(source)) {
            result.put(source, target, digraph.get(source, target).getAsInt());
         }
      }

      return result;
   }

   public static <V, G extends Digraph<V>> G subgraph(Digraph<V> digraph, Set<V> vertices, DigraphFactory<? extends G> factory) {
      G subgraph = (G)factory.create();

      for (V v : vertices) {
         if (digraph.contains(v)) {
            subgraph.add(v);

            for (V w : digraph.targets(v)) {
               if (vertices.contains(w)) {
                  subgraph.put(v, w, digraph.get(v, w).getAsInt());
               }
            }
         }
      }

      return subgraph;
   }
}
