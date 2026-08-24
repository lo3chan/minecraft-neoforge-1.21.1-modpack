package de.odysseus.ithaka.digraph;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.OptionalInt;

public class DoubledDigraphAdapter<V> extends DigraphAdapter<V> implements DoubledDigraph<V> {
   private final DoubledDigraphAdapter<V> reverse;
   private final DigraphFactory<? extends Digraph<V>> factory;

   public DoubledDigraphAdapter() {
      this(MapDigraph.getDefaultDigraphFactory());
   }

   public DoubledDigraphAdapter(DigraphFactory<? extends Digraph<V>> factory) {
      super((Digraph<V>)factory.create());
      this.factory = factory;
      this.reverse = this.createReverse();
   }

   protected DoubledDigraphAdapter(DigraphFactory<? extends Digraph<V>> factory, DoubledDigraphAdapter<V> reverse) {
      super((Digraph<V>)factory.create());
      this.factory = factory;
      this.reverse = reverse;
   }

   public static <V> DigraphFactory<DoubledDigraphAdapter<V>> getAdapterFactory(DigraphFactory<? extends Digraph<V>> factory) {
      return () -> new DoubledDigraphAdapter<>(factory);
   }

   protected DoubledDigraphAdapter<V> createReverse() {
      return new DoubledDigraphAdapter<>(this.factory, this);
   }

   protected DigraphFactory<? extends DoubledDigraph<V>> getDigraphFactory() {
      return getAdapterFactory(this.factory);
   }

   protected DigraphFactory<? extends Digraph<V>> getDelegateFactory() {
      return this.factory;
   }

   @Override
   public int getInDegree(V vertex) {
      return this.reverse.getOutDegree(vertex);
   }

   @Override
   public Iterable<V> sources(V target) {
      return this.reverse.targets(target);
   }

   @Override
   public final boolean add(V vertex) {
      this.reverse.add0(vertex);
      return this.add0(vertex);
   }

   protected boolean add0(V vertex) {
      return super.add(vertex);
   }

   @Override
   public final boolean remove(V vertex) {
      this.reverse.remove0(vertex);
      return this.remove0(vertex);
   }

   protected boolean remove0(V vertex) {
      return super.remove(vertex);
   }

   @Override
   public void removeAll(Collection<V> vertices) {
      this.reverse.removeAll0(vertices);
      this.removeAll0(vertices);
   }

   protected void removeAll0(Collection<V> vertices) {
      super.removeAll(vertices);
   }

   @Override
   public Iterable<V> vertices() {
      final Iterator<V> delegate = super.vertices().iterator();
      return (Iterable<V>)(!delegate.hasNext() ? Collections.emptySet() : new Iterable<V>() {
         @Override
         public Iterator<V> iterator() {
            return new Iterator<V>() {
               V vertex;

               @Override
               public boolean hasNext() {
                  return delegate.hasNext();
               }

               @Override
               public V next() {
                  return this.vertex = delegate.next();
               }

               @Override
               public void remove() {
                  delegate.remove();
                  DoubledDigraphAdapter.this.reverse.remove0(this.vertex);
               }
            };
         }

         @Override
         public String toString() {
            return DoubledDigraphAdapter.super.vertices().toString();
         }
      });
   }

   @Override
   public Iterable<V> targets(final V source) {
      final Iterator<V> delegate = super.targets(source).iterator();
      return (Iterable<V>)(!delegate.hasNext() ? Collections.emptySet() : new Iterable<V>() {
         @Override
         public Iterator<V> iterator() {
            return new Iterator<V>() {
               V target;

               @Override
               public boolean hasNext() {
                  return delegate.hasNext();
               }

               @Override
               public V next() {
                  return this.target = delegate.next();
               }

               @Override
               public void remove() {
                  delegate.remove();
                  DoubledDigraphAdapter.this.reverse.remove0(this.target, source);
               }
            };
         }

         @Override
         public String toString() {
            return DoubledDigraphAdapter.super.targets(source).toString();
         }
      });
   }

   @Override
   public final OptionalInt put(V source, V target, int edge) {
      this.reverse.put0(target, source, edge);
      return this.put0(source, target, edge);
   }

   protected OptionalInt put0(V source, V target, int edge) {
      return super.put(source, target, edge);
   }

   @Override
   public final OptionalInt remove(V source, V target) {
      this.reverse.remove0(target, source);
      return this.remove0(source, target);
   }

   protected OptionalInt remove0(V source, V target) {
      return super.remove(source, target);
   }

   public final DoubledDigraphAdapter<V> reverse() {
      return this.reverse;
   }
}
