package dev.latvian.mods.kubejs.core;

import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.kubejs.typings.ThisIs;
import dev.latvian.mods.rhino.util.HideFromJS;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

@RemapPrefixForJS("kjs$")
public interface ItemFrameEntityKJS extends EntityKJS {
   @HideFromJS
   default ItemFrame kjs$self() {
      return (ItemFrame)this;
   }

   @ThisIs({ItemFrame.class})
   @Info("Checks if the entity is an item frame entity.")
   @Override
   default boolean kjs$isFrame() {
      return true;
   }

   @Info("Gets the item stack corresponding to the item in the item frame.\nWill be `null` if the contained stack is empty.\n")
   @Nullable
   @Override
   default ItemStack kjs$getItem() {
      ItemStack stack = this.kjs$self().getItem();
      return stack.isEmpty() ? null : stack;
   }
}
