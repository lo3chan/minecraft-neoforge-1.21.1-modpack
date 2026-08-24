package top.theillusivec4.curios.api.event;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.util.TriState;
import net.neoforged.neoforge.event.entity.living.LivingEvent;
import top.theillusivec4.curios.api.SlotContext;

public class CurioCanUnequipEvent extends LivingEvent {
   private final SlotContext slotContext;
   private final ItemStack stack;
   private TriState result;

   public CurioCanUnequipEvent(ItemStack stack, SlotContext slotContext) {
      super(slotContext.entity());
      this.slotContext = slotContext;
      this.stack = stack;
   }

   public TriState getUnequipResult() {
      return this.result;
   }

   public void setUnequipResult(TriState result) {
      this.result = result;
   }

   public SlotContext getSlotContext() {
      return this.slotContext;
   }

   public ItemStack getStack() {
      return this.stack;
   }
}
