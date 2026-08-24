package net.cibernet.alchemancy.properties.special;

import net.cibernet.alchemancy.properties.Property;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.FireBlock;
import net.minecraft.world.level.block.state.BlockState;

public class FlameWakerProperty extends Property {
   @Override
   public void onEquippedTick(LivingEntity user, EquipmentSlot slot, ItemStack stack) {
      if (!user.level().isClientSide() && (slot == EquipmentSlot.FEET || slot == EquipmentSlot.BODY)) {
         Level level = user.level();
         BlockPos pos = user.blockPosition();
         BlockState state = level.getBlockState(pos);
         if (user.getKnownMovement().length() <= 0.004999999888241291 && state.is(BlockTags.FIRE) && state.hasProperty(FireBlock.AGE)) {
            level.setBlock(pos, (BlockState)state.setValue(FireBlock.AGE, Math.min((Integer)state.getValue(FireBlock.AGE), 2)), 11);
         }

         if (user.isSprinting() && (BaseFireBlock.canBePlacedAt(level, pos, Direction.UP) || level.getBlockState(pos).canBeReplaced())) {
            BlockState fireBlock = BaseFireBlock.getState(level, pos);
            if (fireBlock.hasProperty(FireBlock.AGE)) {
               fireBlock = (BlockState)fireBlock.setValue(FireBlock.AGE, 8);
            }

            level.setBlock(pos, fireBlock, 11);
            if (user.tickCount % 40 == 0) {
               this.damageItem(user, stack, slot, 1);
            }
         }
      }
   }

   @Override
   public int getColor(ItemStack stack) {
      return 16740386;
   }
}
