package io.github.razordevs.deep_aether.item.misc;

import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.alchemy.PotionContents;

public class AntidoteItem extends Item {
   private final boolean enchanted;
   private final MobEffectInstance instance;

   public AntidoteItem(boolean enchanted, Properties properties, MobEffectInstance instance) {
      super(properties);
      this.enchanted = enchanted;
      this.instance = instance;
   }

   public boolean isFoil(ItemStack itemStack) {
      return this.enchanted;
   }

   public int getUseDuration(ItemStack stack, LivingEntity livingEntity) {
      return 40;
   }

   public UseAnim getUseAnimation(ItemStack p_41358_) {
      return UseAnim.DRINK;
   }

   public SoundEvent getDrinkingSound() {
      return SoundEvents.HONEY_DRINK;
   }

   public SoundEvent getEatingSound() {
      return SoundEvents.HONEY_DRINK;
   }

   public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
      PotionContents.addPotionTooltip(List.of(this.instance), tooltipComponents::add, 1.0F, context.level() == null ? 20.0F : context.tickRate());
   }
}
