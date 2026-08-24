package com.github.alexthe666.alexsmobs.misc;

import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;

public abstract class AbstractProjectileDispenseBehavior extends DefaultDispenseItemBehavior {
   public ItemStack execute(BlockSource source, ItemStack stack) {
      Level level = source.level();
      Position position = DispenserBlock.getDispensePosition(source);
      Direction direction = (Direction)source.state().getValue(DispenserBlock.FACING);
      Projectile projectile = this.getProjectile(level, position, stack);
      projectile.shoot(direction.getStepX(), direction.getStepY() + 0.1F, direction.getStepZ(), this.getPower(), this.getUncertainty());
      level.addFreshEntity(projectile);
      stack.shrink(1);
      return stack;
   }

   protected void playSound(BlockSource source) {
      source.level().levelEvent(1002, source.pos(), 0);
   }

   protected abstract Projectile getProjectile(Level var1, Position var2, ItemStack var3);

   protected float getUncertainty() {
      return 6.0F;
   }

   protected float getPower() {
      return 1.1F;
   }
}
