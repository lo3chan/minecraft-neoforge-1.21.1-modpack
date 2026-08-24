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

public class GuardianFossilBlockEntity extends BlockEntity {
   public GuardianFossilBlockEntity(BlockPos pos, BlockState state) {
      super((BlockEntityType)ModBlockEntities.GUARDIAN_FOSSIL.get(), pos, state);
   }

   public static void tick(Level world, BlockPos pos, BlockState state, GuardianFossilBlockEntity blockEntity) {
      if (BAConfig.fossilEffectsEnabled && BAConfig.guardianFossilEffectsEnabled) {
         for (Player player : world.getEntitiesOfClass(Player.class, AABB.ofSize(pos.getCenter(), 10.0, 10.0, 10.0))) {
            player.addEffect(new MobEffectInstance(MobEffects.WATER_BREATHING, 200, 0, false, false));
         }
      }
   }
}
