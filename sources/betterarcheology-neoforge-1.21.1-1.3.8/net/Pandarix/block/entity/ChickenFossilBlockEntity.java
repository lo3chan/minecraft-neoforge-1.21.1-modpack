package net.Pandarix.block.entity;

import net.Pandarix.config.BAConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public class ChickenFossilBlockEntity extends BlockEntity {
   public ChickenFossilBlockEntity(BlockPos pos, BlockState state) {
      super((BlockEntityType)ModBlockEntities.CHICKEN_FOSSIL.get(), pos, state);
   }

   public static void tick(Level world, BlockPos pos, BlockState state, ChickenFossilBlockEntity blockEntity) {
      if (BAConfig.fossilEffectsEnabled && BAConfig.chickenFossilEffectsEnabled) {
         for (Player player : world.getEntitiesOfClass(Player.class, AABB.ofSize(pos.getCenter(), 10.0, 10.0, 10.0))) {
            player.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 200, 0, false, false));
         }
      }
   }
}
