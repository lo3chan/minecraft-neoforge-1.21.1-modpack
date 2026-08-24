package io.wispforest.owo.itemgroup;

import java.util.function.Supplier;

public record ItemGroupReference(Supplier<OwoItemGroup> groupSup, int tab) {
   @Deprecated
   public ItemGroupReference(OwoItemGroup group, int tab) {
      this(() -> group, tab);
   }

   public OwoItemGroup group() {
      return this.groupSup.get();
   }
}
