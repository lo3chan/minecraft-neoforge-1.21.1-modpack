package net.cibernet.alchemancy.properties;

import java.util.Collection;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import net.cibernet.alchemancy.crafting.ForgeRecipeGrid;
import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.cibernet.alchemancy.item.components.PropertyModifierComponent;
import net.cibernet.alchemancy.registries.AlchemancyItems;
import net.cibernet.alchemancy.registries.AlchemancyProperties;
import net.cibernet.alchemancy.util.ColorUtils;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredItem;

public abstract class IncreaseInfuseSlotsProperty extends Property {
   public final int slots;
   private static final int[] LIMIT_BREAK_BASE = new int[]{16751892, 16769359, 16777115, 16777215};
   private static final int[] LIMIT_BREAK_ENHANCED = new int[]{65535, 4849663, 9699327, 14221311};
   private static final int[] LIMIT_BREAK_CREATIVE = new int[]{13259263, 16273407, 16751602, 16767226};

   protected IncreaseInfuseSlotsProperty(int slots) {
      this.slots = slots;
   }

   public static Property simple(
      int slots,
      final UnaryOperator<Style> style,
      final Function<ItemStack, Integer> colorSupplier,
      final BiFunction<DeferredItem<Item>, Holder<Property>, Collection<ItemStack>> populateCreativeTab
   ) {
      return new IncreaseInfuseSlotsProperty(slots) {
         @Override
         public int getColor(ItemStack stack) {
            return colorSupplier.apply(stack);
         }

         @Override
         public Component getDisplayText(ItemStack stack) {
            return super.getDisplayText(stack).copy().withStyle(style);
         }

         @Override
         public Collection<ItemStack> populateCreativeTab(DeferredItem<Item> capsuleItem, Holder<Property> holder) {
            return populateCreativeTab.apply(capsuleItem, holder);
         }
      };
   }

   public static Property simple(int slots, int color) {
      return simple(
         slots, style -> style, stack -> color, (capsuleItem, holder) -> List.of(InfusedPropertiesHelper.storeProperties(capsuleItem.toStack(), holder))
      );
   }

   @Override
   public <T> Object modifyDataComponent(ItemStack stack, DataComponentType<? extends T> dataType, T data) {
      return dataType == AlchemancyItems.Components.INFUSION_SLOTS.get() && data instanceof Integer dataInt
         ? dataInt + this.slots + PropertyModifierComponent.<Integer>get(stack, this.asHolder(), AlchemancyProperties.Modifiers.BONUS_SLOTS)
         : super.modifyDataComponent(stack, dataType, data);
   }

   @Override
   public Component getDisplayText(ItemStack stack) {
      int bonusSlots = PropertyModifierComponent.<Integer>get(stack, this.asHolder(), AlchemancyProperties.Modifiers.BONUS_SLOTS);
      return (Component)(bonusSlots > 0
         ? Component.translatable("property.detail.item_count", new Object[]{super.getDisplayText(stack), bonusSlots + 1})
            .setStyle(Style.EMPTY.withColor(this.getColor(stack)))
         : super.getName(stack));
   }

   @Override
   public Component getName(ItemStack stack) {
      return this.getDisplayText(stack);
   }

   @Override
   public boolean onInfusedByDormantProperty(
      ItemStack stack, ItemStack propertySource, ForgeRecipeGrid grid, List<Holder<Property>> propertiesToAdd, boolean consumeItem
   ) {
      if (super.onInfusedByDormantProperty(stack, propertySource, grid, propertiesToAdd, consumeItem)) {
         if (consumeItem) {
            PropertyModifierComponent.set(
               stack,
               this.asHolder(),
               AlchemancyProperties.Modifiers.BONUS_SLOTS,
               (Integer)PropertyModifierComponent.get(propertySource, this.asHolder(), AlchemancyProperties.Modifiers.BONUS_SLOTS)
            );
         }

         return true;
      } else {
         return false;
      }
   }

   public static int limitBreakColors(ItemStack stack) {
      int bonusSlots = PropertyModifierComponent.<Integer>get(stack, AlchemancyProperties.LIMIT_BREAK, AlchemancyProperties.Modifiers.BONUS_SLOTS);
      return ColorUtils.interpolateColorsOverTime(0.15F, bonusSlots >= 99 ? LIMIT_BREAK_CREATIVE : (bonusSlots >= 9 ? LIMIT_BREAK_ENHANCED : LIMIT_BREAK_BASE));
   }

   public static Collection<ItemStack> limitBreakCreativeTab(DeferredItem<Item> capsuleItem, Holder<Property> propertyHolder) {
      ItemStack creativeLimitBreak = InfusedPropertiesHelper.storeProperties(capsuleItem.toStack(), propertyHolder);
      PropertyModifierComponent.set(creativeLimitBreak, propertyHolder, AlchemancyProperties.Modifiers.BONUS_SLOTS, 999);
      return List.of(InfusedPropertiesHelper.storeProperties(capsuleItem.toStack(), propertyHolder), creativeLimitBreak);
   }
}
