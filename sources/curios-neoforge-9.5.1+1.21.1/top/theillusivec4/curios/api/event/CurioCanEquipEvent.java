package top.theillusivec4.curios.api.event;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.util.TriState;
import net.neoforged.neoforge.event.entity.living.LivingEvent;
import top.theillusivec4.curios.api.SlotContext;

public class CurioCanEquipEvent extends LivingEvent {
   private final SlotContext slotContext;
   private final ItemStack stack;
   private TriState result;

   public CurioCanEquipEvent(ItemStack stack, SlotContext slotContext, TriState result) {
      super(slotContext.entity());
      this.slotContext = slotContext;
      this.stack = stack;
      this.result = result;
   }

   @Deprecated(
      forRemoval = true,
      since = "1.22"
   )
   public CurioCanEquipEvent(ItemStack stack, SlotContext slotContext) {
      this(stack, slotContext, TriState.DEFAULT);
   }

   public TriState getEquipResult() {
      return this.result;
   }

   public void setEquipResult(TriState result) {
      this.result = result;
   }

   public SlotContext getSlotContext() {
      return this.slotContext;
   }

   public ItemStack getStack() {
      return this.stack;
   }
}
