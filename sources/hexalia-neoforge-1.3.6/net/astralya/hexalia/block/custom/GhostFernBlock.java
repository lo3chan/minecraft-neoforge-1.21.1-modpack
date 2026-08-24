package net.astralya.hexalia.block.custom;

import net.astralya.hexalia.particle.custom.ColoredSporeParticleOptions;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import org.joml.Vector3f;

public class GhostFernBlock extends HerbBlock {
   public GhostFernBlock(Holder<MobEffect> effect, float seconds, Properties properties) {
      super(effect, seconds, properties);
   }

   public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
      super.animateTick(state, level, pos, random);
      if (!(random.nextFloat() > 0.25F)) {
         double x = pos.getX() + 0.1 + random.nextDouble() * 0.8;
         double y = pos.getY() + random.nextDouble() * 0.7;
         double z = pos.getZ() + 0.1 + random.nextDouble() * 0.8;
         level.addParticle(new ColoredSporeParticleOptions(new Vector3f(0.72F, 0.82F, 0.76F)), x, y, z, 0.0, 0.0, 0.0);
      }
   }
}
