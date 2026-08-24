@file:JvmName(name = "ClientAccessorWrappers")

package at.petrak.hexcasting.client.ktxt

import at.petrak.hexcasting.mixin.accessor.client.AccessorMouseHandler

public final var accumulatedScroll: Double
   public final get() {
      return (`$this$accumulatedScroll` as AccessorMouseHandler).hex$getAccumulatedScroll();
   }

   public final set(value) {
      (`$this$accumulatedScroll` as AccessorMouseHandler).hex$setAccumulatedScroll(value);
   }

