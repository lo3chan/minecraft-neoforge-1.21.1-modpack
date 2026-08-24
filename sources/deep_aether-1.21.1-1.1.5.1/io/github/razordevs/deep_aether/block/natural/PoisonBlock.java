package io.github.razordevs.deep_aether.block.natural;

import com.aetherteam.aether.effect.AetherEffects;
import io.github.razordevs.deep_aether.entity.PoisonItem;
import io.github.razordevs.deep_aether.init.DAParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.FluidState;

public class PoisonBlock extends LiquidBlock {
   public PoisonBlock(FlowingFluid deferredHolder, Properties properties) {
      super(deferredHolder, properties);
   }

   public void stepOn(Level level, BlockPos blockPos, BlockState blockState, Entity entity) {
      if (entity instanceof LivingEntity && !(entity instanceof ArmorStand)) {
         ((LivingEntity)entity).addEffect(new MobEffectInstance(AetherEffects.INEBRIATION, 80, 0, false, false));
      }
   }

   public boolean shouldDisplayFluidOverlay(BlockState state, BlockAndTintGetter level, BlockPos pos, FluidState fluidState) {
      return true;
   }

   public void animateTick(BlockState blockState, Level level, BlockPos blockPos, RandomSource randomSource) {
      double d0 = blockPos.getX();
      double d1 = blockPos.getY();
      double d2 = blockPos.getZ();
      level.addAlwaysVisibleParticle(
         (ParticleOptions)DAParticles.POISON_BUBBLES.get(),
         d0 + randomSource.nextFloat(),
         d1 + randomSource.nextFloat(),
         d2 + randomSource.nextFloat(),
         0.0,
         0.04,
         0.0
      );
      if (randomSource.nextInt(10) == 0) {
         level.playLocalSound(
            d0,
            d1,
            d2,
            SoundEvents.BUBBLE_COLUMN_BUBBLE_POP,
            SoundSource.BLOCKS,
            0.2F + randomSource.nextFloat() * 0.2F,
            0.9F + randomSource.nextFloat() * 0.15F,
            false
         );
      }

      super.animateTick(blockState, level, blockPos, randomSource);
   }

   public void entityInside(BlockState blockState, Level level, BlockPos pos, Entity entity) {
      if (entity instanceof LivingEntity livingEntity && !(livingEntity instanceof ArmorStand)) {
         livingEntity.addEffect(new MobEffectInstance(AetherEffects.INEBRIATION, 100, 0, false, false));
         this.playHissingSound(level, livingEntity, pos);
      } else if (entity instanceof PoisonItem poisonItem) {
         poisonItem.deep_Aether$increaseTime();
         if (poisonItem.deep_Aether$canConvert()) {
            ItemEntity itemEntity = (ItemEntity)poisonItem;
            this.playHissingSound(level, itemEntity, pos);
            this.spawnBubbleParticles(level, itemEntity, pos);
         }
      }
   }

   private void spawnBubbleParticles(Level level, Entity entity, BlockPos pos) {
      if (!level.isClientSide && entity.isAlive()) {
         BlockPos itemPos = entity.getOnPos();
         ServerLevel serverlevel = (ServerLevel)level;
         serverlevel.sendParticles(
            (SimpleParticleType)DAParticles.POISON_BUBBLES.get(),
            itemPos.getX() + level.random.nextDouble(),
            pos.getY() + 1,
            itemPos.getZ() + level.random.nextDouble(),
            1,
            0.0,
            0.0,
            0.2,
            0.3
         );
      }
   }

   private void playHissingSound(Level level, Entity entity, BlockPos pos) {
      if (level.random.nextInt(25) == 0) {
         level.playSound(
            entity, pos, SoundEvents.LAVA_EXTINGUISH, SoundSource.BLOCKS, 0.2F + level.random.nextFloat() * 0.2F, 0.9F + level.random.nextFloat() * 0.15F
         );
      }
   }
}
