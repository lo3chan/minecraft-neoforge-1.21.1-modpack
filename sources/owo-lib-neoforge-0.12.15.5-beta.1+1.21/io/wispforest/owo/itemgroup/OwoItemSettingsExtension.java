package io.wispforest.owo.itemgroup;

import java.util.function.BiConsumer;
import java.util.function.Supplier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.CreativeModeTab.Output;
import net.minecraft.world.item.Item.Properties;

public interface OwoItemSettingsExtension {
   default Properties group(ItemGroupReference ref) {
      throw new IllegalStateException("Implemented in mixin.");
   }

   @Deprecated
   default Properties group(OwoItemGroup group) {
      throw new IllegalStateException("Implemented in mixin.");
   }

   default Properties group(Supplier<OwoItemGroup> groupSupplier) {
      throw new IllegalStateException("Implemented in mixin.");
   }

   @Deprecated
   default OwoItemGroup group() {
      throw new IllegalStateException("Implemented in mixin.");
   }

   default Supplier<OwoItemGroup> groupSupplier() {
      throw new IllegalStateException("Implemented in mixin.");
   }

   default Properties tab(int tab) {
      throw new IllegalStateException("Implemented in mixin.");
   }

   default int tab() {
      throw new IllegalStateException("Implemented in mixin.");
   }

   default Properties stackGenerator(BiConsumer<Item, Output> generator) {
      throw new IllegalStateException("Implemented in mixin.");
   }

   default BiConsumer<Item, Output> stackGenerator() {
      throw new IllegalStateException("Implemented in mixin.");
   }

   default Properties trackUsageStat() {
      throw new IllegalStateException("Implemented in mixin.");
   }

   default boolean shouldTrackUsageStat() {
      throw new IllegalStateException("Implemented in mixin.");
   }
}
