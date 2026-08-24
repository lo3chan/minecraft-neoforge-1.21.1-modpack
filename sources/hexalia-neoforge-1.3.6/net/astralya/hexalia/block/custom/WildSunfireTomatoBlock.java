package net.astralya.hexalia.block.custom;

import com.mojang.serialization.MapCodec;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class WildSunfireTomatoBlock extends BushBlock {
   public static final MapCodec<WildSunfireTomatoBlock> CODEC = simpleCodec(WildSunfireTomatoBlock::new);
   protected static final VoxelShape SHAPE = Shapes.or(Block.box(5.0, 0.0, 5.0, 11.0, 10.0, 11.0), new VoxelShape[0]);

   public WildSunfireTomatoBlock(Properties properties) {
      super(properties);
   }

   protected MapCodec<? extends BushBlock> codec() {
      return CODEC;
   }

   public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
      BlockState result = super.playerWillDestroy(level, pos, state, player);
      spawnFireParticles(level, pos);
      return result;
   }

   protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
      return SHAPE;
   }

   protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
      if (!level.isClientSide() && level.getDifficulty() != Difficulty.PEACEFUL) {
         if (entity instanceof LivingEntity livingEntity && !livingEntity.isSteppingCarefully() && !livingEntity.fireImmune()) {
            if (livingEntity instanceof Player player && player.isCreative()) {
               return;
            }

            livingEntity.igniteForSeconds(5.0F);
         }
      }
   }

   private static void spawnFireParticles(Level level, BlockPos pos) {
      ThreadLocalRandom random = ThreadLocalRandom.current();

      for (int i = 0; i < 8; i++) {
         double x = pos.getX() + 0.5 + random.nextDouble(-0.5, 0.5);
         double y = pos.getY() + random.nextDouble(0.33);
         double z = pos.getZ() + 0.5 + random.nextDouble(-0.5, 0.5);
         level.addParticle(ParticleTypes.FLAME, x, y, z, 0.0, 0.05, 0.0);
      }
   }
}
