package net.cibernet.alchemancy.properties;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.cibernet.alchemancy.properties.data.IDataHolder;
import net.cibernet.alchemancy.registries.AlchemancyProperties;
import net.cibernet.alchemancy.registries.AlchemancyTags;
import net.cibernet.alchemancy.util.ColorUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class FragmentedProperty extends Property {
   @Override
   public void onStackedOverMe(
      ItemStack carriedItem, ItemStack stack, Player player, ClickAction clickAction, SlotAccess carriedSlot, Slot stackedOnSlot, AtomicBoolean isCancelled
   ) {
      if (!isCancelled.get() && carriedItem.isEmpty() && clickAction == ClickAction.SECONDARY) {
         if (stack.getMaxDamage() <= 1) {
            this.consumeItem(player, stack, EquipmentSlot.MAINHAND);
            isCancelled.set(true);
         } else {
            InfusedPropertiesHelper.removeProperty(stack, this.asHolder());
            stack.set(DataComponents.MAX_DAMAGE, Math.floorDiv(stack.getMaxDamage(), 2));
            stack.set(DataComponents.DAMAGE, Math.floorDiv(stack.getDamageValue(), 2));
            if (((ResizedProperty)AlchemancyProperties.RESIZED.get()).hasData(stack)) {
               ((ResizedProperty)AlchemancyProperties.RESIZED.get())
                  .setData(stack, Math.max(0.25F, ((ResizedProperty)AlchemancyProperties.RESIZED.get()).getData(stack) * 0.5F));
            }

            ItemStack newStack = stack.copy();
            newStack.setCount(1);
            InfusedPropertiesHelper.forEachProperty(newStack, propertyHolder -> {
               if (propertyHolder.is(AlchemancyTags.Properties.CANNOT_CLONE_DATA) && propertyHolder.value() instanceof IDataHolder<?> dataHolder) {
                  dataHolder.removeData(newStack);
               }
            }, false);

            for (DataComponentType<?> dataComponentType : List.copyOf(newStack.getComponents().keySet())) {
               if (BuiltInRegistries.DATA_COMPONENT_TYPE.wrapAsHolder(dataComponentType).is(AlchemancyTags.DataComponents.FRAGMENTED_DOES_NOT_CLONE)) {
                  newStack.remove(dataComponentType);
               }
            }

            carriedSlot.set(newStack);
            isCancelled.set(true);
         }
      }
   }

   @Override
   public int getColor(ItemStack stack) {
      return ColorUtils.flashColorsOverTime(1000.0, 13085656, 6110066);
   }

   @Override
   public Component getDisplayText(ItemStack stack) {
      return this.getName(stack);
   }

   @Override
   public Component getName(ItemStack stack) {
      return Component.translatable(
         this.getLanguageKey() + ".format",
         new Object[]{
            Component.translatable(this.getLanguageKey() + ".a").withColor(13085656),
            Component.translatable(this.getLanguageKey() + ".b").withStyle(ChatFormatting.ITALIC).withColor(6110066)
         }
      );
   }
}
