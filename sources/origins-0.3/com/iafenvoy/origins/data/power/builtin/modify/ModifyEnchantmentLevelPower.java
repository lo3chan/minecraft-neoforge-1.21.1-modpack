package com.iafenvoy.origins.data.power.builtin.modify;

import com.iafenvoy.origins.accessor.EntityLinkedItemStack;
import com.iafenvoy.origins.attachment.PowerHelper;
import com.iafenvoy.origins.data._common.helper.ModifierPowerHelper;
import com.iafenvoy.origins.data.condition.ItemCondition;
import com.iafenvoy.origins.data.power.Power;
import com.iafenvoy.origins.util.codec.CombinedCodecs;
import com.iafenvoy.origins.util.math.Modifier;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments.Mutable;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.enchanting.GetEnchantmentLevelEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@EventBusSubscriber
public class ModifyEnchantmentLevelPower extends Power implements ModifierPowerHelper {
   public static final MapCodec<ModifyEnchantmentLevelPower> CODEC = RecordCodecBuilder.mapCodec(
      i -> i.group(
            Power.BaseSettings.CODEC.forGetter(Power::getSettings),
            Enchantment.CODEC.fieldOf("enchantment").forGetter(ModifyEnchantmentLevelPower::getEnchantment),
            ItemCondition.optionalCodec("item_condition").forGetter(ModifyEnchantmentLevelPower::getItemCondition),
            CombinedCodecs.MODIFIER.fieldOf("modifier").forGetter(ModifyEnchantmentLevelPower::getModifier)
         )
         .apply(i, ModifyEnchantmentLevelPower::new)
   );
   private final Holder<Enchantment> enchantment;
   private final ItemCondition itemCondition;
   private final List<Modifier> modifier;

   public ModifyEnchantmentLevelPower(Power.BaseSettings settings, Holder<Enchantment> enchantment, ItemCondition itemCondition, List<Modifier> modifier) {
      super(settings);
      this.enchantment = enchantment;
      this.itemCondition = itemCondition;
      this.modifier = modifier;
   }

   public Holder<Enchantment> getEnchantment() {
      return this.enchantment;
   }

   public ItemCondition getItemCondition() {
      return this.itemCondition;
   }

   @Override
   public List<Modifier> getModifier() {
      return this.modifier;
   }

   @NotNull
   @Override
   public MapCodec<? extends Power> codec() {
      return CODEC;
   }

   public boolean doesApply(Level level, ItemStack stack) {
      return this.itemCondition.test(level, stack);
   }

   public static int modifyLevel(@Nullable LivingEntity entity, ItemStack stack, Holder<Enchantment> enchantment, int level) {
      return entity == null
         ? level
         : PowerHelper.get(entity)
            .modify(
               ModifyEnchantmentLevelPower.class, power -> power.enchantment.value() == enchantment.value() && power.doesApply(entity.level(), stack), level
            );
   }

   @SubscribeEvent
   public static void modifyEnchantmentLevel(GetEnchantmentLevelEvent event) {
      ItemStack stack = event.getStack();
      Holder<Enchantment> targetEnchant = event.getTargetEnchant();
      Mutable enchantments = event.getEnchantments();
      LivingEntity entity = getEntityFromStack(stack);
      if (targetEnchant != null && entity != null) {
         int level = enchantments.getLevel(targetEnchant);
         int modifiedLevel = modifyLevel(entity, stack, targetEnchant, level);
         if (modifiedLevel != level) {
            enchantments.set(targetEnchant, modifiedLevel);
         }
      }
   }

   @Nullable
   private static LivingEntity getEntityFromStack(ItemStack stack) {
      return EntityLinkedItemStack.getEntity(stack) instanceof LivingEntity le ? le : null;
   }
}
