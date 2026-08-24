package top.theillusivec4.curios.common.capability;

import java.util.Map;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.wrapper.CombinedInvWrapper;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;
import top.theillusivec4.curios.common.CuriosRegistry;

public class CurioItemHandler implements IItemHandler {
   final IItemHandler curios;
   final LivingEntity livingEntity;

   public CurioItemHandler(LivingEntity livingEntity) {
      this.livingEntity = livingEntity;
      CurioInventory inv = (CurioInventory)livingEntity.getData(CuriosRegistry.INVENTORY.get());
      Map<String, ICurioStacksHandler> curios = inv.curios;
      IItemHandlerModifiable[] itemHandlers = new IItemHandlerModifiable[curios.size()];
      int index = 0;

      for (ICurioStacksHandler stacksHandler : curios.values()) {
         if (index < itemHandlers.length) {
            itemHandlers[index] = stacksHandler.getStacks();
            index++;
         }
      }

      this.curios = new CombinedInvWrapper(itemHandlers);
   }

   public int getSlots() {
      return this.curios.getSlots();
   }

   @NotNull
   public ItemStack getStackInSlot(int slot) {
      return this.curios.getStackInSlot(slot);
   }

   @NotNull
   public ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
      return this.curios.insertItem(slot, stack, simulate);
   }

   @NotNull
   public ItemStack extractItem(int slot, int amount, boolean simulate) {
      return this.curios.extractItem(slot, amount, simulate);
   }

   public int getSlotLimit(int slot) {
      return this.curios.getSlotLimit(slot);
   }

   public boolean isItemValid(int slot, @NotNull ItemStack stack) {
      return this.curios.isItemValid(slot, stack);
   }
}
