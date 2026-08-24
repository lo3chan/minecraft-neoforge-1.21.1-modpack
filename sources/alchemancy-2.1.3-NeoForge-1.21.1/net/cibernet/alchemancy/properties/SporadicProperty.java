package net.cibernet.alchemancy.properties;

import java.util.List;
import net.cibernet.alchemancy.blocks.blockentities.RootedItemBlockEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class SporadicProperty extends Property {
   @Override
   public void onEquippedTick(LivingEntity user, EquipmentSlot slot, ItemStack stack) {
      if (!user.level().isClientSide() && user.getRandom().nextFloat() < 0.005F) {
         activateByEntity(user, user, stack);
      }
   }

   @Override
   public void onRootedTick(RootedItemBlockEntity root, List<LivingEntity> entitiesInBounds) {
      if (!root.getLevel().isClientSide() && root.getLevel().getRandom().nextFloat() < 0.005F) {
         for (LivingEntity entity : entitiesInBounds) {
            activateByBlock(root, entity);
         }
      }
   }

   @Override
   public void onRootedAnimateTick(RootedItemBlockEntity root, RandomSource randomSource) {
      playRootedParticles(root, randomSource, ParticleTypes.MYCELIUM);
   }

   @Override
   public int getColor(ItemStack stack) {
      return 10120654;
   }

   @Override
   public Component getDisplayText(ItemStack stack) {
      return super.getDisplayText(stack).copy().withStyle(ChatFormatting.ITALIC);
   }
}
