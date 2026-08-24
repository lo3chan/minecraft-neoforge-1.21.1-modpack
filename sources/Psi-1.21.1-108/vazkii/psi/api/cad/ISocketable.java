package vazkii.psi.api.cad;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import vazkii.psi.api.PsiAPI;
import vazkii.psi.api.spell.ISpellAcceptor;

public interface ISocketable {
   List<ResourceLocation> signs = Arrays.asList(
      ResourceLocation.parse(String.format("psi:textures/gui/signs/sign%d.png", 0)),
      ResourceLocation.parse(String.format("psi:textures/gui/signs/sign%d.png", 1)),
      ResourceLocation.parse(String.format("psi:textures/gui/signs/sign%d.png", 2)),
      ResourceLocation.parse(String.format("psi:textures/gui/signs/sign%d.png", 3)),
      ResourceLocation.parse(String.format("psi:textures/gui/signs/sign%d.png", 4)),
      ResourceLocation.parse(String.format("psi:textures/gui/signs/sign%d.png", 5)),
      ResourceLocation.parse(String.format("psi:textures/gui/signs/sign%d.png", 6)),
      ResourceLocation.parse(String.format("psi:textures/gui/signs/sign%d.png", 7)),
      ResourceLocation.parse(String.format("psi:textures/gui/signs/sign%d.png", 8)),
      ResourceLocation.parse(String.format("psi:textures/gui/signs/sign%d.png", 9)),
      ResourceLocation.parse(String.format("psi:textures/gui/signs/sign%d.png", 10)),
      ResourceLocation.parse(String.format("psi:textures/gui/signs/sign%d.png", 11)),
      ResourceLocation.parse(String.format("psi:textures/gui/signs/sign%d.png", 12))
   );
   int MAX_ASSEMBLER_SLOTS = 12;

   static Component getSocketedItemName(ItemStack stack, String fallbackKey) {
      if (!stack.isEmpty() && isSocketable(stack)) {
         ISocketable socketable = socketable(stack);
         ItemStack item = socketable.getSelectedBullet();
         return (Component)(item.isEmpty() ? Component.translatable(fallbackKey) : item.getHoverName());
      } else {
         return Component.translatable(fallbackKey);
      }
   }

   static boolean isSocketable(ItemStack stack) {
      return !stack.isEmpty() && stack.getCapability(PsiAPI.SOCKETABLE_CAPABILITY) != null;
   }

   static ISocketable socketable(ItemStack stack) {
      ISocketable capability = (ISocketable)stack.getCapability(PsiAPI.SOCKETABLE_CAPABILITY);
      if (capability == null) {
         throw new NullPointerException();
      } else {
         return capability;
      }
   }

   boolean isSocketSlotAvailable(int var1);

   default List<Integer> getRadialMenuSlots() {
      List<Integer> list = new ArrayList<>();

      for (int i = 0; i < 12; i++) {
         if (this.isSocketSlotAvailable(i)) {
            list.add(i);
         }
      }

      return list;
   }

   default List<ResourceLocation> getRadialMenuIcons() {
      return signs;
   }

   ItemStack getBulletInSocket(int var1);

   void setBulletInSocket(int var1, ItemStack var2);

   int getSelectedSlot();

   void setSelectedSlot(int var1);

   default int getLastSlot() {
      int slot = 0;

      while (this.isSocketSlotAvailable(slot + 1)) {
         slot++;
      }

      return slot;
   }

   default ItemStack getSelectedBullet() {
      return this.getBulletInSocket(this.getSelectedSlot());
   }

   default boolean isItemValid(int slot, ItemStack bullet) {
      if (!this.isSocketSlotAvailable(slot)) {
         return false;
      } else if (!ISpellAcceptor.isContainer(bullet)) {
         return false;
      } else {
         ISpellAcceptor container = ISpellAcceptor.acceptor(bullet);
         return this instanceof ICADData || !container.isCADOnlyContainer();
      }
   }

   default boolean canLoopcast() {
      return this instanceof ICADData;
   }
}
