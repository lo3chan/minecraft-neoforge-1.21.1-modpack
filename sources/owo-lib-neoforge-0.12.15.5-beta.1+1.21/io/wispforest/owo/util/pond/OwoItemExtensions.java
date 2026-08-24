package io.wispforest.owo.util.pond;

import java.util.function.BiConsumer;
import java.util.function.Supplier;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.CreativeModeTab.Output;
import org.jetbrains.annotations.Nullable;

public interface OwoItemExtensions {
   int owo$tab();

   BiConsumer<Item, Output> owo$stackGenerator();

   void owo$setGroup(Supplier<CreativeModeTab> var1);

   default void owo$setGroup(CreativeModeTab group) {
      this.owo$setGroup((Supplier<CreativeModeTab>)(() -> group));
   }

   @Nullable
   CreativeModeTab owo$group();

   boolean owo$shouldTrackUsageStat();
}
