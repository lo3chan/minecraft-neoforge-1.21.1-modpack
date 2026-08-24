package fuzs.puzzleslib.api.core.v1.util;

import java.util.AbstractList;
import java.util.List;
import java.util.Objects;
import org.jetbrains.annotations.Nullable;

public abstract class TransformingForwardingList<T, E> extends AbstractList<T> {
   private final List<E> delegate;

   public TransformingForwardingList(List<E> delegate) {
      for (E element : delegate) {
         Objects.requireNonNull(element, "element is null");
      }

      this.delegate = delegate;
   }

   @Override
   public T get(int index) {
      int delegateIndex = this.delegateIndex(index);
      E delegateElement = this.delegate.get(delegateIndex);
      return this.getAsElement(delegateElement);
   }

   @Override
   public int size() {
      return this.index(this.delegate.size() - 1) + 1;
   }

   @Override
   public T set(int index, T element) {
      Objects.requireNonNull(element, "element is null");
      int delegateIndex = this.delegateIndex(index);
      E delegateElement = this.delegate.set(delegateIndex, this.getAsListElement(element));
      return this.getAsElement(delegateElement);
   }

   @Override
   public void add(int index, T element) {
      Objects.requireNonNull(element, "element is null");
      int delegateIndex = this.delegateIndex(index);
      this.delegate.add(delegateIndex != -1 ? delegateIndex : this.delegate.size(), this.getAsListElement(element));
   }

   @Override
   public T remove(int index) {
      int delegateIndex = this.delegateIndex(index);
      E delegateElement = this.delegate.remove(delegateIndex);
      return this.getAsElement(delegateElement);
   }

   @Nullable
   protected abstract T getAsElement(@Nullable E var1);

   @Nullable
   protected abstract E getAsListElement(@Nullable T var1);

   private int delegateIndex(int index) {
      for (int i = 0; i < this.delegate.size(); i++) {
         E delegateElement = this.delegate.get(i);
         T t = this.getAsElement(delegateElement);
         if (t != null) {
            if (index == 0) {
               return i;
            }

            index--;
         }
      }

      return -1;
   }

   private int index(int delegateIndex) {
      for (int i = 0; i < this.delegate.size(); i++) {
         E delegateElement = this.delegate.get(i);
         T t = this.getAsElement(delegateElement);
         if (t != null) {
            if (i == delegateIndex) {
               return i;
            }
         } else {
            delegateIndex--;
         }
      }

      return -1;
   }
}
