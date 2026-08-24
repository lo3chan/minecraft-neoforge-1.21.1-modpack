package net.cibernet.alchemancy.properties;

import java.util.List;
import net.cibernet.alchemancy.crafting.ForgeRecipeGrid;
import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.cibernet.alchemancy.properties.data.IDataHolder;
import net.cibernet.alchemancy.registries.AlchemancyProperties;
import net.cibernet.alchemancy.registries.AlchemancySoundEvents;
import net.cibernet.alchemancy.registries.AlchemancyTags;
import net.cibernet.alchemancy.util.ColorUtils;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class UndyingProperty extends Property implements IDataHolder<Integer> {
   final int[] colors = new int[]{4318847, 16645102, 15391620, 16645102};
   private static final int MAX = 5;

   @Override
   public int modifyDurabilityConsumed(
      ItemStack stack, ServerLevel level, @Nullable LivingEntity user, int originalAmount, int resultingAmount, RandomSource random
   ) {
      if (stack.getMaxDamage() <= stack.getDamageValue() + resultingAmount) {
         repairItem(stack, (int)(stack.getMaxDamage() * 0.4F));
         int uses = this.getData(stack);
         if (uses <= 1) {
            InfusedPropertiesHelper.removeProperty(stack, AlchemancyProperties.UNDYING);
         } else {
            this.setData(stack, uses - 1);
         }

         if (user != null) {
            level.playSound(null, user.getX(), user.getY(), user.getZ(), AlchemancySoundEvents.UNDYING, SoundSource.PLAYERS, 1.0F, 1.0F);
            level.sendParticles(
               SparklingProperty.getParticles(stack).orElse(ParticleTypes.TOTEM_OF_UNDYING),
               user.getX(),
               user.getY(0.5),
               user.getZ(),
               10,
               0.15000000596046448,
               0.0,
               0.15000000596046448,
               0.2
            );
         }

         if (user != null) {
            level.playSound(
               user instanceof Player player ? player : null,
               user.position().x,
               user.position().y,
               user.position().z,
               SoundEvents.TOTEM_USE,
               SoundSource.PLAYERS,
               0.65F,
               1.0F
            );
         }

         return 0;
      } else {
         return resultingAmount;
      }
   }

   @Override
   public boolean onInfusedByDormantProperty(
      ItemStack stack, ItemStack propertySource, ForgeRecipeGrid grid, List<Holder<Property>> propertiesToAdd, boolean consumeItem
   ) {
      int amount = Math.max(1, this.getData(stack));
      if (amount >= 5) {
         return false;
      } else {
         if (consumeItem) {
            this.setData(stack, Math.min(5, (InfusedPropertiesHelper.hasProperty(stack, this.asHolder()) ? amount : 0) + getSourceAmount(propertySource)));
         }

         return true;
      }
   }

   private static int getSourceAmount(ItemStack stack) {
      return stack.is(AlchemancyTags.Items.VALUABLE_UNDYING_SOURCE) ? 3 : 1;
   }

   @Override
   public Component getDisplayText(ItemStack stack) {
      int amount = this.getData(stack);
      return (Component)(amount <= 1
         ? super.getDisplayText(stack)
         : Component.translatable("property.detail.item_count", new Object[]{super.getDisplayText(stack), amount}).withColor(this.getColor(stack)));
   }

   @Override
   public Component getName(ItemStack stack) {
      int amount = getSourceAmount(stack);
      return (Component)(amount <= 1
         ? super.getName(stack)
         : Component.translatable("property.detail.item_count", new Object[]{super.getName(stack), amount}).withColor(this.getColor(stack)));
   }

   @Override
   public int getColor(ItemStack stack) {
      return ColorUtils.interpolateColorsOverTime(0.5F, this.colors);
   }

   public Integer readData(CompoundTag tag) {
      return tag.contains("uses") ? tag.getInt("uses") : this.getDefaultData();
   }

   public CompoundTag writeData(final Integer data) {
      return new CompoundTag() {
         {
            this.putInt("uses", data);
         }
      };
   }

   public Integer getDefaultData() {
      return 1;
   }
}
