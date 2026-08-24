package dev.latvian.mods.kubejs.core;

import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.kubejs.typings.ThisIs;
import dev.latvian.mods.rhino.util.HideFromJS;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

@RemapPrefixForJS("kjs$")
public interface ItemEntityKJS extends EntityKJS {
   @HideFromJS
   default ItemEntity kjs$self() {
      return (ItemEntity)this;
   }

   @Info("Gets the item stack corresponding to the item contained in the item entity.\nWill be `null` if the contained stack is empty.\n")
   @Nullable
   @Override
   default ItemStack kjs$getItem() {
      ItemStack stack = this.kjs$self().getItem();
      return stack.isEmpty() ? null : stack;
   }

   @ThisIs({ItemEntity.class})
   @Override
   default boolean kjs$isItem() {
      return true;
   }

   default int kjs$getLifespan() {
      return this.kjs$self().lifespan;
   }

   default void kjs$setLifespan(int lifespan) {
      this.kjs$self().lifespan = lifespan;
   }

   default void kjs$setDefaultPickUpDelay() {
      this.kjs$self().setPickUpDelay(10);
   }

   default void kjs$setNoPickUpDelay() {
      this.kjs$self().setPickUpDelay(0);
   }

   default void kjs$setInfinitePickUpDelay() {
      this.kjs$self().setPickUpDelay(32767);
   }

   default void kjs$setNoDespawn() {
      this.kjs$self().setUnlimitedLifetime();
   }

   default int kjs$getTicksUntilDespawn() {
      return this.kjs$getLifespan() - this.kjs$self().age;
   }

   default void kjs$setTicksUntilDespawn(int ticks) {
      this.kjs$self().age = this.kjs$getLifespan() - ticks;
   }
}
