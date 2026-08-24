package com.github.alexthe666.alexsmobs.item;

import com.github.alexthe666.alexsmobs.entity.util.RainbowUtil;
import com.github.alexthe666.alexsmobs.misc.AMCompat;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;

public class ItemRainbowJelly extends Item {
   public ItemRainbowJelly(Properties tab) {
      super(tab);
   }

   public InteractionResult interactLivingEntity(ItemStack stack, Player playerIn, LivingEntity target, InteractionHand hand) {
      int i = RainbowUtil.getRainbowTypeFromStack(stack);
      if (RainbowUtil.getRainbowType(target) == i) {
         return InteractionResult.PASS;
      } else {
         RainbowUtil.setRainbowType(target, i);
         RandomSource random = playerIn.getRandom();

         for (int j = 0; j < 6 + random.nextInt(3); j++) {
            double d2 = random.nextGaussian() * 0.02;
            double d0 = random.nextGaussian() * 0.02;
            double d1 = random.nextGaussian() * 0.02;
            playerIn.level()
               .addParticle(
                  new ItemParticleOption(ParticleTypes.ITEM, stack),
                  target.getX() + random.nextFloat() * target.getBbWidth() - target.getBbWidth() * 0.5,
                  target.getY() + target.getBbHeight() * 0.5F + random.nextFloat() * target.getBbHeight() * 0.5F,
                  target.getZ() + random.nextFloat() * target.getBbWidth() - target.getBbWidth() * 0.5,
                  d0,
                  d1,
                  d2
               );
         }

         target.gameEvent(GameEvent.ITEM_INTERACT_START);
         target.playSound(SoundEvents.SLIME_SQUISH_SMALL, 1.0F, target.getVoicePitch());
         if (!playerIn.isCreative()) {
            stack.shrink(1);
         }

         return InteractionResult.SUCCESS;
      }
   }

   public ItemStack finishUsingItem(ItemStack st, Level level, LivingEntity e) {
      RainbowUtil.setRainbowType(e, RainbowUtil.getRainbowTypeFromStack(st));
      return AMCompat.isEdible(this) ? e.eat(level, st) : st;
   }

   public int getUseDuration(ItemStack stack, LivingEntity user) {
      return this.getUseDuration(stack);
   }

   public int getUseDuration(ItemStack stack) {
      return AMCompat.isEdible(stack.getItem()) ? 64 : 0;
   }

   public boolean isFoil(ItemStack stack) {
      return super.isFoil(stack) || RainbowUtil.getRainbowTypeFromStack(stack) > 1;
   }

   public static enum RainbowType {
      RAINBOW,
      TRANS,
      NONBI,
      BI,
      ACE,
      WEEZER,
      BRAZIL;

      public static ItemRainbowJelly.RainbowType getFromString(String name) {
         if (name.contains("nonbi") || name.contains("non-bi")) {
            return NONBI;
         } else if (name.contains("trans")) {
            return TRANS;
         } else if (name.contains("bi")) {
            return BI;
         } else if (name.contains("asexual") || name.contains("ace")) {
            return ACE;
         } else if (name.contains("weezer")) {
            return WEEZER;
         } else {
            return name.contains("brazil") ? BRAZIL : RAINBOW;
         }
      }
   }
}
