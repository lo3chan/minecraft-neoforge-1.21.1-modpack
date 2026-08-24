package vazkii.psi.common.item.tool;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;
import net.neoforged.neoforge.capabilities.ItemCapability;
import net.neoforged.neoforge.capabilities.Capabilities.ItemHandler;
import net.neoforged.neoforge.items.ComponentItemHandler;
import org.jetbrains.annotations.NotNull;
import vazkii.psi.api.PsiAPI;
import vazkii.psi.api.cad.IPsiBarDisplay;
import vazkii.psi.api.cad.ISocketable;
import vazkii.psi.api.internal.IPlayerData;
import vazkii.psi.api.spell.ISpellAcceptor;
import vazkii.psi.api.spell.Spell;
import vazkii.psi.common.item.base.ModDataComponents;

public class ToolSocketable implements ICapabilityProvider<ItemCapability<?, Void>, Void, ToolSocketable>, ISocketable, IPsiBarDisplay, ISpellAcceptor {
   protected final ItemStack tool;
   protected final int slots;
   private final ComponentItemHandler toolHandler;

   public ToolSocketable(ItemStack tool, int slots) {
      this.tool = tool;
      this.slots = Mth.clamp(slots, 1, 11);
      this.toolHandler = (ComponentItemHandler)tool.getCapability(ItemHandler.ITEM);
   }

   public ToolSocketable getCapability(@NotNull ItemCapability<?, Void> capability, Void facing) {
      return capability != PsiAPI.SOCKETABLE_CAPABILITY && capability != PsiAPI.PSI_BAR_DISPLAY_CAPABILITY && capability != PsiAPI.SPELL_ACCEPTOR_CAPABILITY
         ? null
         : this;
   }

   @Override
   public boolean isSocketSlotAvailable(int slot) {
      return slot < this.slots && slot >= 0;
   }

   @Override
   public List<Integer> getRadialMenuSlots() {
      List<Integer> list = new ArrayList<>();

      for (int i = 0; i <= this.slots; i++) {
         list.add(i);
      }

      return list;
   }

   @Override
   public ItemStack getBulletInSocket(int slot) {
      return !this.isSocketSlotAvailable(slot) ? ItemStack.EMPTY : this.toolHandler.getStackInSlot(slot);
   }

   @Override
   public void setBulletInSocket(int slot, ItemStack bullet) {
      if (this.isSocketSlotAvailable(slot)) {
         this.toolHandler.setStackInSlot(slot, bullet);
      }
   }

   @Override
   public int getSelectedSlot() {
      return (Integer)this.tool.getOrDefault(ModDataComponents.SELECTED_SLOT, 0);
   }

   @Override
   public void setSelectedSlot(int slot) {
      this.tool.set(ModDataComponents.SELECTED_SLOT, slot);
   }

   @Override
   public int getLastSlot() {
      return this.slots - 1;
   }

   @Override
   public boolean shouldShow(IPlayerData data) {
      return false;
   }

   @Override
   public void setSpell(Player player, Spell spell) {
      int slot = this.getSelectedSlot();
      ItemStack bullet = this.getBulletInSocket(slot);
      if (!bullet.isEmpty() && ISpellAcceptor.isAcceptor(bullet)) {
         ISpellAcceptor.acceptor(bullet).setSpell(player, spell);
         this.setBulletInSocket(slot, bullet);
      }
   }
}
