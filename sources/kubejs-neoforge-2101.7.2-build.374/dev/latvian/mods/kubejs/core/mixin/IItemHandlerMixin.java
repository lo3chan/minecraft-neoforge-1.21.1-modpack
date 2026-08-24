package dev.latvian.mods.kubejs.core.mixin;

import dev.latvian.mods.kubejs.core.InventoryKJS;
import dev.latvian.mods.kubejs.level.LevelBlock;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin({IItemHandler.class})
public interface IItemHandlerMixin extends InventoryKJS {
   @Unique
   default IItemHandler kjs$self() {
      return (IItemHandler)this;
   }

   @Override
   default boolean kjs$isMutable() {
      return this.kjs$self() instanceof IItemHandlerModifiable;
   }

   @Invoker("getSlots")
   @Override
   int kjs$getSlots();

   @Invoker("getStackInSlot")
   @Override
   ItemStack kjs$getStackInSlot(int i);

   @Override
   default void kjs$setStackInSlot(int slot, ItemStack stack) {
      if (this.kjs$self() instanceof IItemHandlerModifiable mod) {
         mod.setStackInSlot(slot, stack);
      } else {
         InventoryKJS.super.kjs$setStackInSlot(slot, stack);
      }
   }

   @Invoker("insertItem")
   @Override
   ItemStack kjs$insertItem(int i, ItemStack itemStack, boolean b);

   @Invoker("extractItem")
   @Override
   ItemStack kjs$extractItem(int i, int i1, boolean b);

   @Invoker("getSlotLimit")
   @Override
   int kjs$getSlotLimit(int i);

   @Invoker("isItemValid")
   @Override
   boolean kjs$isItemValid(int i, ItemStack itemStack);

   @Nullable
   @Override
   default LevelBlock kjs$getBlock(Level level) {
      return this.kjs$self() instanceof BlockEntity entity ? level.kjs$getBlock(entity) : null;
   }
}
