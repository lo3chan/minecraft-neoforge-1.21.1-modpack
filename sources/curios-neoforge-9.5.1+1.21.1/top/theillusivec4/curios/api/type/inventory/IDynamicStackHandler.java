package top.theillusivec4.curios.api.type.inventory;

import javax.annotation.Nonnull;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandlerModifiable;

public interface IDynamicStackHandler extends IItemHandlerModifiable {
   void setStackInSlot(int var1, @Nonnull ItemStack var2);

   @Nonnull
   ItemStack getStackInSlot(int var1);

   void setPreviousStackInSlot(int var1, @Nonnull ItemStack var2);

   ItemStack getPreviousStackInSlot(int var1);

   int getSlots();

   void grow(int var1);

   void shrink(int var1);

   CompoundTag serializeNBT(Provider var1);

   void deserializeNBT(Provider var1, CompoundTag var2);
}
