package vazkii.psi.api.cad;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import vazkii.psi.api.internal.Vector3;
import vazkii.psi.api.spell.SpellRuntimeException;
import vazkii.psi.api.spell.piece.PieceCraftingTrick;
import vazkii.psi.common.item.base.ModDataComponents;

public interface ICAD {
   static void setComponent(ItemStack stack, ItemStack componentStack) {
      List<Item> items = (List<Item>)stack.getOrDefault(
         ModDataComponents.COMPONENTS, new ArrayList<>(Collections.nCopies(EnumCADComponent.values().length, Items.AIR))
      );
      if (!componentStack.isEmpty() && componentStack.getItem() instanceof ICADComponent component) {
         if (!(items instanceof ArrayList)) {
            items = new ArrayList<>(items);
         }

         EnumCADComponent componentType = component.getComponentType(componentStack);
         items.set(componentType.ordinal(), componentStack.getItem());
         stack.set(ModDataComponents.COMPONENTS, items);
      }
   }

   static void copyComponents(ItemStack from, ItemStack to) {
      if (from.getItem() instanceof ICAD && to.getItem() instanceof ICAD) {
         List<Item> fromComponents = (List<Item>)from.get(ModDataComponents.COMPONENTS);
         to.set(
            ModDataComponents.COMPONENTS,
            new ArrayList(Objects.requireNonNullElseGet(fromComponents, () -> Collections.nCopies(EnumCADComponent.values().length, Items.AIR)))
         );
      }
   }

   default void setCADComponent(ItemStack stack, ItemStack component) {
      setComponent(stack, component);
   }

   ItemStack getComponentInSlot(ItemStack var1, EnumCADComponent var2);

   int getStatValue(ItemStack var1, EnumCADStat var2);

   int getStoredPsi(ItemStack var1);

   void regenPsi(ItemStack var1, int var2);

   int consumePsi(ItemStack var1, int var2);

   int getMemorySize(ItemStack var1);

   void setStoredVector(ItemStack var1, int var2, Vector3 var3) throws SpellRuntimeException;

   Vector3 getStoredVector(ItemStack var1, int var2) throws SpellRuntimeException;

   int getTime(ItemStack var1);

   void incrementTime(ItemStack var1);

   @OnlyIn(Dist.CLIENT)
   int getSpellColor(ItemStack var1);

   boolean craft(ItemStack var1, Player var2, PieceCraftingTrick var3);
}
