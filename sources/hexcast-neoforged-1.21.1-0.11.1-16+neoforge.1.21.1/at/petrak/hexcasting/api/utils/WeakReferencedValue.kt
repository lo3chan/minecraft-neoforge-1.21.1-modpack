package at.petrak.hexcasting.api.utils

import java.lang.ref.WeakReference
import kotlin.jvm.internal.SourceDebugExtension

@SourceDebugExtension(["SMAP\nHexUtils.kt\nKotlin\n*S Kotlin\n*F\n+ 1 HexUtils.kt\nat/petrak/hexcasting/api/utils/WeakReferencedValue\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,321:1\n1#2:322\n*E\n"])
private class WeakReferencedValue<T>(reference: WeakReference<Any>?) : WeakValue<T> {
   public final var reference: WeakReference<Any>?
      internal set

   public open var value: Any?
      public open get() {
         return if (this.reference != null) this.reference.get() else null;
      }

      public open set(value) {
         var var10000: WeakReferencedValue = this;
         val var10001: WeakReference;
         if (value != null) {
            var10001 = new WeakReference<>(value);
            var10000 = this;
         } else {
            var10001 = null;
         }

         var10000.reference = var10001;
      }


   init {
      this.reference = reference;
   }
}
