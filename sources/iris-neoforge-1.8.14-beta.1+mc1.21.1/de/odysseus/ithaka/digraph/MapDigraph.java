package de.odysseus.ithaka.digraph;

import it.unimi.dsi.fastutil.objects.Object2IntAVLTreeMap;
import it.unimi.dsi.fastutil.objects.Object2IntLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.OptionalInt;
import java.util.Set;
import java.util.TreeMap;

public class MapDigraph<V> implements Digraph<V> {
   private static final int INVALID_WEIGHT = -2147483648;
   private final MapDigraph.VertexMapFactory<V> vertexMapFactory;
   private final MapDigraph.EdgeMapFactory<V> edgeMapFactory;
   private final Map<V, Object2IntMap<V>> vertexMap;
   private int edgeCount;

   public MapDigraph() {
      this(null);
   }

   public MapDigraph(Comparator<? super V> comparator) {
      this(comparator, comparator);
   }

   public MapDigraph(Comparator<? super V> vertexComparator, Comparator<? super V> edgeComparator) {
      this(getDefaultVertexMapFactory(vertexComparator), getDefaultEdgeMapFactory(edgeComparator));
   }

   public MapDigraph(MapDigraph.VertexMapFactory<V> vertexMapFactory, MapDigraph.EdgeMapFactory<V> edgeMapFactory) {
      this.vertexMapFactory = vertexMapFactory;
      this.edgeMapFactory = edgeMapFactory;
      this.vertexMap = vertexMapFactory.create();
   }

   public static <V> DigraphFactory<MapDigraph<V>> getDefaultDigraphFactory() {
      return getMapDigraphFactory(getDefaultVertexMapFactory(null), getDefaultEdgeMapFactory(null));
   }

   public static <V> DigraphFactory<MapDigraph<V>> getMapDigraphFactory(
      MapDigraph.VertexMapFactory<V> vertexMapFactory, MapDigraph.EdgeMapFactory<V> edgeMapFactory
   ) {
      return () -> new MapDigraph<>(vertexMapFactory, edgeMapFactory);
   }

   private static <V> MapDigraph.VertexMapFactory<V> getDefaultVertexMapFactory(Comparator<? super V> comparator) {
      return () -> (Map<V, Object2IntMap<V>>)(comparator == null ? new LinkedHashMap<>(16) : new TreeMap<>(comparator));
   }

   private static <V> MapDigraph.EdgeMapFactory<V> getDefaultEdgeMapFactory(Comparator<? super V> comparator) {
      return ignore -> {
         Object2IntMap<V> map;
         if (comparator == null) {
            map = new Object2IntLinkedOpenHashMap(16);
         } else {
            map = new Object2IntAVLTreeMap(comparator);
         }

         map.defaultReturnValue(-2147483648);
         return map;
      };
   }

   private static <V> Object2IntMap<V> createEmptyMap() {
      return Object2IntMaps.emptyMap();
   }

   @Override
   public boolean add(V vertex) {
      if (!this.vertexMap.containsKey(vertex)) {
         this.vertexMap.put(vertex, createEmptyMap());
         return true;
      } else {
         return false;
      }
   }

   @Override
   public OptionalInt put(V source, V target, int weight) {
      if (weight == -2147483648) {
         throw new IllegalArgumentException("Invalid weight " + weight);
      } else {
         Object2IntMap<V> edgeMap = this.vertexMap.get(source);
         if (edgeMap == null || edgeMap.isEmpty()) {
            this.vertexMap.put(source, edgeMap = this.edgeMapFactory.create(source));
         }

         int previousInt = edgeMap.put(target, weight);
         OptionalInt previous;
         if (previousInt != -2147483648) {
            previous = OptionalInt.of(previousInt);
         } else {
            previous = OptionalInt.empty();
            this.add(target);
            this.edgeCount++;
         }

         return previous;
      }
   }

   @Override
   public OptionalInt get(V source, V target) {
      Object2IntMap<V> edgeMap = this.vertexMap.get(source);
      if (edgeMap != null && !edgeMap.isEmpty()) {
         int result = edgeMap.getInt(target);
         return result == -2147483648 ? OptionalInt.empty() : OptionalInt.of(result);
      } else {
         return OptionalInt.empty();
      }
   }

   @Override
   public OptionalInt remove(V source, V target) {
      Object2IntMap<V> edgeMap = this.vertexMap.get(source);
      if (edgeMap != null && edgeMap.containsKey(target)) {
         int result = edgeMap.removeInt(target);
         this.edgeCount--;
         if (edgeMap.isEmpty()) {
            this.vertexMap.put(source, createEmptyMap());
         }

         return result == -2147483648 ? OptionalInt.empty() : OptionalInt.of(result);
      } else {
         return OptionalInt.empty();
      }
   }

   @Override
   public boolean remove(V vertex) {
      Object2IntMap<V> edgeMap = this.vertexMap.get(vertex);
      if (edgeMap == null) {
         return false;
      } else {
         this.edgeCount = this.edgeCount - edgeMap.size();
         this.vertexMap.remove(vertex);

         for (V source : this.vertexMap.keySet()) {
            this.remove(source, vertex);
         }

         return true;
      }
   }

   @Override
   public void removeAll(Collection<V> vertices) {
      for (V vertex : vertices) {
         Object2IntMap<V> edgeMap = this.vertexMap.get(vertex);
         if (edgeMap != null) {
            this.edgeCount = this.edgeCount - edgeMap.size();
            this.vertexMap.remove(vertex);
         }
      }

      for (V source : this.vertexMap.keySet()) {
         Object2IntMap<V> edgeMap = this.vertexMap.get(source);
         Iterator<V> iterator = edgeMap.keySet().iterator();

         while (iterator.hasNext()) {
            if (vertices.contains(iterator.next())) {
               iterator.remove();
               this.edgeCount--;
            }
         }

         if (edgeMap.isEmpty()) {
            this.vertexMap.put(source, createEmptyMap());
         }
      }
   }

   @Override
   public boolean contains(V source, V target) {
      Object2IntMap<V> edgeMap = this.vertexMap.get(source);
      return edgeMap != null && !edgeMap.isEmpty() ? edgeMap.containsKey(target) : false;
   }

   @Override
   public boolean contains(V vertex) {
      return this.vertexMap.containsKey(vertex);
   }

   @Override
   public Iterable<V> vertices() {
      return (Iterable<V>)(this.vertexMap.isEmpty() ? Collections.emptySet() : new Iterable<V>() {
         @Override
         public Iterator<V> iterator() {
            return new Iterator<V>() {
               private final Iterator<V> delegate = MapDigraph.this.vertexMap.keySet().iterator();
               V vertex = (V)null;

               @Override
               public boolean hasNext() {
                  return this.delegate.hasNext();
               }

               @Override
               public V next() {
                  return this.vertex = this.delegate.next();
               }

               @Override
               public void remove() {
                  Object2IntMap<V> edgeMap = MapDigraph.this.vertexMap.get(this.vertex);
                  this.delegate.remove();
                  MapDigraph.this.edgeCount = MapDigraph.this.edgeCount - edgeMap.size();

                  for (V source : MapDigraph.this.vertexMap.keySet()) {
                     MapDigraph.this.remove(source, this.vertex);
                  }
               }
            };
         }

         @Override
         public String toString() {
            return MapDigraph.this.vertexMap.keySet().toString();
         }
      });
   }

   @Override
   public Iterable<V> targets(final V source) {
      final Object2IntMap<V> edgeMap = this.vertexMap.get(source);
      return (Iterable<V>)(edgeMap != null && !edgeMap.isEmpty() ? new Iterable<V>() {
         @Override
         public Iterator<V> iterator() {
            return new Iterator<V>() {
               private final Iterator<V> delegate = edgeMap.keySet().iterator();

               @Override
               public boolean hasNext() {
                  return this.delegate.hasNext();
               }

               @Override
               public V next() {
                  return this.delegate.next();
               }

               @Override
               public void remove() {
                  this.delegate.remove();
                  MapDigraph.this.edgeCount--;
                  if (edgeMap.isEmpty()) {
                     MapDigraph.this.vertexMap.put(source, MapDigraph.createEmptyMap());
                  }
               }
            };
         }

         @Override
         public String toString() {
            return edgeMap.keySet().toString();
         }
      } : Collections.emptySet());
   }

   @Override
   public int getVertexCount() {
      return this.vertexMap.size();
   }

   @Override
   public int totalWeight() {
      int weight = 0;

      for (V source : this.vertices()) {
         for (V target : this.targets(source)) {
            weight += this.get(source, target).getAsInt();
         }
      }

      return weight;
   }

   @Override
   public int getOutDegree(V vertex) {
      Object2IntMap<V> edgeMap = this.vertexMap.get(vertex);
      return edgeMap == null ? 0 : edgeMap.size();
   }

   @Override
   public int getEdgeCount() {
      return this.edgeCount;
   }

   public DigraphFactory<? extends MapDigraph<V>> getDigraphFactory() {
      return () -> new MapDigraph<>(this.vertexMapFactory, this.edgeMapFactory);
   }

   public MapDigraph<V> reverse() {
      return Digraphs.reverse(this, this.getDigraphFactory());
   }

   public MapDigraph<V> subgraph(Set<V> vertices) {
      return Digraphs.subgraph(this, vertices, this.getDigraphFactory());
   }

   @Override
   public boolean isAcyclic() {
      return Digraphs.isAcyclic(this);
   }

   @Override
   public String toString() {
      StringBuilder b = new StringBuilder();
      b.append(this.getClass().getName().substring(this.getClass().getName().lastIndexOf(46) + 1));
      b.append("(");
      Iterator<V> vertices = this.vertices().iterator();

      while (vertices.hasNext()) {
         V v = vertices.next();
         b.append(v);
         b.append(this.targets(v));
         if (vertices.hasNext()) {
            b.append(", ");
            if (b.length() > 1000) {
               b.append("...");
               break;
            }
         }
      }

      b.append(")");
      return b.toString();
   }

   public interface EdgeMapFactory<V> {
      Object2IntMap<V> create(V var1);
   }

   public interface VertexMapFactory<V> {
      Map<V, Object2IntMap<V>> create();
   }
}
