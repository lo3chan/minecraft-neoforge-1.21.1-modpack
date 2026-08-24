package net.cibernet.alchemancy.properties;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import net.cibernet.alchemancy.crafting.ForgeRecipeGrid;
import net.cibernet.alchemancy.item.components.InfusedPropertiesComponent;
import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.cibernet.alchemancy.properties.data.IDataHolder;
import net.cibernet.alchemancy.registries.AlchemancyItems;
import net.cibernet.alchemancy.registries.AlchemancyTags;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.ItemAttributeModifierEvent;
import net.neoforged.neoforge.registries.DeferredItem;
import org.jetbrains.annotations.Nullable;

public class ResizedProperty extends Property implements IDataHolder<Float> {
   private static final ResourceLocation MODIFIER_KEY = ResourceLocation.fromNamespaceAndPath("alchemancy", "resized_modifier");
   public static final float MIN = 0.5F;
   public static final float MAX = 2.0F;

   @Override
   public void applyAttributes(ItemAttributeModifierEvent event) {
      float size = this.getData(event.getItemStack());
      event.addModifier(Attributes.ATTACK_DAMAGE, new AttributeModifier(MODIFIER_KEY, size - 1.0F, Operation.ADD_MULTIPLIED_TOTAL), EquipmentSlotGroup.MAINHAND);
      event.addModifier(
         Attributes.ATTACK_SPEED, new AttributeModifier(MODIFIER_KEY, 1.0F / size - 1.0F, Operation.ADD_MULTIPLIED_TOTAL), EquipmentSlotGroup.MAINHAND
      );
   }

   @Override
   public boolean cluelessCanReset() {
      return false;
   }

   @Override
   public boolean onInfusedByDormantProperty(
      ItemStack stack, ItemStack propertySource, ForgeRecipeGrid grid, List<Holder<Property>> propertiesToAdd, boolean consumeItem
   ) {
      float currentSize = this.getData(stack);
      float newSize;
      if (!this.getData(propertySource).equals(this.getDefaultData())) {
         newSize = this.getData(propertySource);
      } else if (propertySource.is(AlchemancyTags.Items.INCREASES_RESIZED) && currentSize < 2.0F) {
         newSize = Math.min(2.0F, currentSize + 0.1F);
      } else {
         if (!propertySource.is(AlchemancyTags.Items.DECREASES_RESIZED) || !(currentSize > 0.5F)) {
            return false;
         }

         newSize = Math.max(0.5F, currentSize - 0.1F);
      }

      if (newSize == this.getDefaultData()) {
         propertiesToAdd.remove(this.asHolder());
         if (consumeItem) {
            InfusedPropertiesHelper.removeProperty(stack, this.asHolder());
         }
      } else if (consumeItem) {
         this.setData(stack, newSize);
      }

      return true;
   }

   public Float readData(CompoundTag tag) {
      return tag.getFloat("size");
   }

   public CompoundTag writeData(final Float data) {
      return new CompoundTag() {
         {
            this.putFloat("size", data);
         }
      };
   }

   public Float combineData(@Nullable Float currentData, Float newData) {
      return currentData == null ? newData : Math.clamp(currentData + newData, 0.5F, 2.0F);
   }

   public Float getDefaultData() {
      return 1.0F;
   }

   @Override
   public int getColor(ItemStack stack) {
      return 14551807;
   }

   @Override
   public Component getDisplayText(ItemStack stack) {
      return Component.translatable(
            "property.detail",
            new Object[]{
               super.getDisplayText(stack), Component.translatable("property.detail.percentage", new Object[]{Math.round(this.getData(stack) * 100.0F)})
            }
         )
         .withColor(this.getColor(stack));
   }

   @Override
   public Component getName(ItemStack stack) {
      if (stack.is(AlchemancyTags.Items.INCREASES_RESIZED)) {
         return Component.translatable("property.alchemancy.resized.increase").withColor(this.getColor(stack));
      } else if (stack.is(AlchemancyTags.Items.DECREASES_RESIZED)) {
         return Component.translatable("property.alchemancy.resized.decrease").withColor(this.getColor(stack));
      } else {
         return !this.getData(stack).equals(this.getDefaultData()) ? this.getDisplayText(stack) : super.getName(stack);
      }
   }

   @Override
   public Collection<ItemStack> populateCreativeTab(DeferredItem<Item> capsuleItem, Holder<Property> holder) {
      ArrayList<ItemStack> result = new ArrayList<>();

      for (float size = 0.5F; size <= 2.0F; size++) {
         ItemStack stack = capsuleItem.toStack();
         stack.set(AlchemancyItems.Components.STORED_PROPERTIES, new InfusedPropertiesComponent(List.of(holder)));
         this.setData(stack, size);
         result.add(stack);
      }

      return result;
   }
}
