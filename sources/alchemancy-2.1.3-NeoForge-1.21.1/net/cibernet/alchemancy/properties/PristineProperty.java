package net.cibernet.alchemancy.properties;

import java.util.List;
import net.cibernet.alchemancy.crafting.ForgeRecipeGrid;
import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.cibernet.alchemancy.properties.data.IDataHolder;
import net.cibernet.alchemancy.util.ColorUtils;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.util.FastColor.ARGB32;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class PristineProperty extends Property implements IDataHolder<Integer>, ITintModifier {
   @Override
   public int getColor(ItemStack stack) {
      return ColorUtils.interpolateColorsOverTime(5.0F, -8800341, -8800364, -10582620, -10576220);
   }

   @Override
   public boolean onInfusedByDormantProperty(
      ItemStack stack, ItemStack propertySource, ForgeRecipeGrid grid, List<Holder<Property>> propertiesToAdd, boolean consumeItem
   ) {
      if (!this.getData(stack).equals(this.getDefaultData())) {
         if (consumeItem) {
            this.removeData(stack);
         }

         return true;
      } else {
         return super.onInfusedByDormantProperty(stack, propertySource, grid, propertiesToAdd, consumeItem);
      }
   }

   @Override
   public int modifyDurabilityConsumed(
      ItemStack stack, ServerLevel level, @Nullable LivingEntity user, int originalAmount, int resultingAmount, RandomSource random
   ) {
      int durability = this.getData(stack) - 1;
      if (InfusedPropertiesHelper.hasInfusedProperty(stack, this.asHolder()) && durability <= 0) {
         InfusedPropertiesHelper.removeProperty(stack, this.asHolder());
      }

      this.setData(stack, durability);
      return 0;
   }

   @Override
   public Component getDisplayText(ItemStack stack) {
      return Component.translatable(
            "property.detail",
            new Object[]{
               super.getDisplayText(stack),
               Component.translatable("property.detail.percentage", new Object[]{(int)(this.getDurabilityPercentage(stack) * 100.0F)})
            }
         )
         .withColor(this.getColor(stack));
   }

   private float getDurabilityPercentage(ItemStack stack) {
      return (float)this.getData(stack).intValue() / this.getDefaultData().intValue();
   }

   public Integer readData(CompoundTag tag) {
      return tag.contains("durability", 3) ? tag.getInt("durability") : this.getDefaultData();
   }

   public CompoundTag writeData(final Integer data) {
      return new CompoundTag() {
         {
            this.putInt("durability", data);
         }
      };
   }

   public Integer combineData(@Nullable Integer currentData, Integer newData) {
      return currentData == null ? newData : Math.min(this.getDefaultData(), currentData + newData);
   }

   public Integer getDefaultData() {
      return 100;
   }

   @Override
   public int getTint(ItemStack stack, int tintIndex, int originalTint, int currentTint) {
      return ARGB32.lerp(this.getDurabilityPercentage(stack) * 0.6F + 0.2F, currentTint, this.getColor(stack));
   }
}
