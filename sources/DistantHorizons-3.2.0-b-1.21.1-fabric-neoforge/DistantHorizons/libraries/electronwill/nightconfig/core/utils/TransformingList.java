package DistantHorizons.libraries.electronwill.nightconfig.core.utils;

import java.util.Collection;
import java.util.List;
import java.util.ListIterator;
import java.util.function.Function;

public final class TransformingList<InternalV, ExternalV> extends TransformingCollection<InternalV, ExternalV> implements List<ExternalV> {
   public TransformingList(
      List<InternalV> internalList,
      Function<? super InternalV, ? extends ExternalV> readTransformation,
      Function<? super ExternalV, ? extends InternalV> writeTransformation,
      Function<Object, Object> searchTransformation
   ) {
      super(internalList, readTransformation, writeTransformation, searchTransformation);
   }

   @Override
   public boolean addAll(int index, Collection<? extends ExternalV> c) {
      return ((List)this.internalCollection)
         .addAll(index, new TransformingCollection<>(c, this.writeTransformation, this.readTransformation, this.searchTransformation));
   }

   @Override
   public ExternalV get(int index) {
      return (ExternalV)this.readTransformation.apply(((List)this.internalCollection).get(index));
   }

   @Override
   public ExternalV set(int index, ExternalV element) {
      return (ExternalV)this.readTransformation.apply(((List)this.internalCollection).set(index, (InternalV)this.writeTransformation.apply(element)));
   }

   @Override
   public void add(int index, ExternalV element) {
      ((List)this.internalCollection).add(index, (InternalV)this.writeTransformation.apply(element));
   }

   @Override
   public ExternalV remove(int index) {
      return (ExternalV)this.readTransformation.apply(((List)this.internalCollection).remove(index));
   }

   @Override
   public int indexOf(Object o) {
      return ((List)this.internalCollection).indexOf(this.searchTransformation.apply(o));
   }

   @Override
   public int lastIndexOf(Object o) {
      return ((List)this.internalCollection).lastIndexOf(this.searchTransformation.apply(o));
   }

   @Override
   public ListIterator<ExternalV> listIterator() {
      return new TransformingListIterator<>(((List)this.internalCollection).listIterator(), this.readTransformation, this.writeTransformation);
   }

   @Override
   public ListIterator<ExternalV> listIterator(int index) {
      return new TransformingListIterator<>(((List)this.internalCollection).listIterator(index), this.readTransformation, this.writeTransformation);
   }

   @Override
   public List<ExternalV> subList(int fromIndex, int toIndex) {
      return new TransformingList<>(
         ((List)this.internalCollection).subList(fromIndex, toIndex), this.readTransformation, this.writeTransformation, this.searchTransformation
      );
   }
}
