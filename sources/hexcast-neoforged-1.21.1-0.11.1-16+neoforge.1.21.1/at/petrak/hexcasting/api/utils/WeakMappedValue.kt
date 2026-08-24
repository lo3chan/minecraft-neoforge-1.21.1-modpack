package at.petrak.hexcasting.api.utils

import java.util.WeakHashMap

private class WeakMappedValue<K, T>(keyGen: (Any) -> Any) : WeakValue<T> {
   public final val keyGen: (Any) -> Any
   public final val reference: WeakHashMap<Any, Any>

   public open var value: Any?
      public open get() {
         val var10000: java.util.Collection = this.reference.values();
         return (T)CollectionsKt.firstOrNull(var10000);
      }

      public open set(value) {
         this.reference.clear();
         if (value != null) {
            this.reference.put((K)this.keyGen.invoke(value), (T)value);
         }
      }


   init {
      this.keyGen = keyGen;
      this.reference = new WeakHashMap<>();
   }
}
