package DistantHorizons.libraries.electronwill.nightconfig.core.utils;

import java.util.ListIterator;
import java.util.function.Function;

public final class TransformingListIterator<InternalV, ExternalV> extends TransformingIterator<InternalV, ExternalV> implements ListIterator<ExternalV> {
   private final Function<? super ExternalV, ? extends InternalV> writeTransformation;

   public TransformingListIterator(
      ListIterator<InternalV> internalIterator,
      Function<? super InternalV, ? extends ExternalV> readTransformation,
      Function<? super ExternalV, ? extends InternalV> writeTransformation
   ) {
      super(internalIterator, readTransformation);
      this.writeTransformation = writeTransformation;
   }

   @Override
   public boolean hasPrevious() {
      return ((ListIterator)this.internalIterator).hasPrevious();
   }

   @Override
   public ExternalV previous() {
      return (ExternalV)this.readTransformation.apply(((ListIterator)this.internalIterator).previous());
   }

   @Override
   public int nextIndex() {
      return ((ListIterator)this.internalIterator).nextIndex();
   }

   @Override
   public int previousIndex() {
      return ((ListIterator)this.internalIterator).previousIndex();
   }

   @Override
   public void set(ExternalV externalV) {
      ((ListIterator)this.internalIterator).set((InternalV)this.writeTransformation.apply(externalV));
   }

   @Override
   public void add(ExternalV externalV) {
      ((ListIterator)this.internalIterator).add((InternalV)this.writeTransformation.apply(externalV));
   }
}
