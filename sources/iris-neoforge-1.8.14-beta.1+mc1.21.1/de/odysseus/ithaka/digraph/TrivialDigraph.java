package de.odysseus.ithaka.digraph;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.OptionalInt;
import java.util.Set;

public class TrivialDigraph<V> implements DoubledDigraph<V> {
   private V vertex = (V)null;
   private boolean hasLoop = false;
   private int loopWeight = 0;

   @Override
   public boolean add(V vertex) {
      if (vertex == null) {
         throw new IllegalArgumentException("Cannot add null vertex!");
      } else if (this.vertex == null) {
         this.vertex = vertex;
         return true;
      } else if (this.vertex.equals(vertex)) {
         return false;
      } else {
         throw new UnsupportedOperationException("TrivialDigraph must contain at most one vertex!");
      }
   }

   @Override
   public boolean contains(Object source, Object target) {
      return this.vertex != null && this.hasLoop && this.vertex.equals(source) && this.vertex.equals(target);
   }

   @Override
   public boolean contains(Object vertex) {
      return this.vertex != null && this.vertex.equals(vertex);
   }

   @Override
   public OptionalInt get(Object source, Object target) {
      return this.contains(source, target) ? OptionalInt.of(this.loopWeight) : OptionalInt.empty();
   }

   @Override
   public int getInDegree(Object vertex) {
      return this.hasLoop ? 1 : 0;
   }

   @Override
   public int getOutDegree(Object vertex) {
      return this.hasLoop ? 1 : 0;
   }

   @Override
   public int getEdgeCount() {
      return this.hasLoop ? 1 : 0;
   }

   @Override
   public int getVertexCount() {
      return this.vertex == null ? 0 : 1;
   }

   @Override
   public int totalWeight() {
      return this.hasLoop ? this.loopWeight : 0;
   }

   @Override
   public Iterable<V> vertices() {
      return (Iterable<V>)(this.vertex == null ? Collections.emptyList() : new Iterable<V>() {
         @Override
         public Iterator<V> iterator() {
            return new Iterator<V>() {
               boolean hasNext = true;

               @Override
               public boolean hasNext() {
                  return this.hasNext;
               }

               @Override
               public V next() {
                  if (this.hasNext) {
                     this.hasNext = false;
                     return TrivialDigraph.this.vertex;
                  } else {
                     throw new NoSuchElementException("No more vertices");
                  }
               }

               @Override
               public void remove() {
                  if (this.hasNext) {
                     throw new IllegalStateException();
                  } else {
                     TrivialDigraph.this.remove(TrivialDigraph.this.vertex);
                  }
               }
            };
         }

         @Override
         public String toString() {
            return "[" + TrivialDigraph.this.vertex + "]";
         }
      });
   }

   @Override
   public OptionalInt put(V source, V target, int loopWeight) {
      if (source != target) {
         throw new UnsupportedOperationException("TrivialDigraph must not contain no-loop edges!");
      } else {
         OptionalInt previousLoopWeight = this.hasLoop ? OptionalInt.of(this.loopWeight) : OptionalInt.empty();
         this.add(source);
         this.hasLoop = true;
         this.loopWeight = loopWeight;
         return previousLoopWeight;
      }
   }

   @Override
   public OptionalInt remove(V source, V target) {
      if (this.contains(source, target)) {
         int loopWeight = this.loopWeight;
         this.loopWeight = 0;
         this.hasLoop = false;
         return OptionalInt.of(loopWeight);
      } else {
         return OptionalInt.empty();
      }
   }

   @Override
   public boolean remove(V vertex) {
      if (this.vertex != null && this.vertex.equals(vertex)) {
         this.vertex = null;
         this.loopWeight = 0;
         this.hasLoop = false;
         return true;
      } else {
         return false;
      }
   }

   @Override
   public void removeAll(Collection<V> vertices) {
      if (vertices.contains(this.vertex)) {
         this.remove(this.vertex);
      }
   }

   @Override
   public DoubledDigraph<V> reverse() {
      return this;
   }

   @Override
   public Digraph<V> subgraph(Set<V> vertices) {
      return (Digraph<V>)(this.vertex != null && vertices.contains(this.vertex) ? this : Digraphs.emptyDigraph());
   }

   @Override
   public Iterable<V> sources(Object target) {
      return this.targets(target);
   }

   @Override
   public Iterable<V> targets(Object source) {
      return (Iterable<V>)(this.hasLoop && this.vertex != null && this.vertex.equals(source) ? new Iterable<V>() {
         @Override
         public Iterator<V> iterator() {
            return new Iterator<V>() {
               boolean hasNext = true;

               @Override
               public boolean hasNext() {
                  return this.hasNext;
               }

               @Override
               public V next() {
                  if (this.hasNext) {
                     this.hasNext = false;
                     return TrivialDigraph.this.vertex;
                  } else {
                     throw new NoSuchElementException("No more vertices");
                  }
               }

               @Override
               public void remove() {
                  if (this.hasNext) {
                     throw new IllegalStateException();
                  } else {
                     TrivialDigraph.this.remove(TrivialDigraph.this.vertex, TrivialDigraph.this.vertex);
                  }
               }
            };
         }

         @Override
         public String toString() {
            return "[" + TrivialDigraph.this.vertex + "]";
         }
      } : Collections.emptyList());
   }

   @Override
   public boolean isAcyclic() {
      return !this.hasLoop;
   }
}
