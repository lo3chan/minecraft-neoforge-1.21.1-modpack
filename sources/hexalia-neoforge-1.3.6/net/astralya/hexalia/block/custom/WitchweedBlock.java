package net.astralya.hexalia.block.custom;

import net.astralya.hexalia.entity.custom.SilkMothEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.Difficulty;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.entity.animal.frog.Frog;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class WitchweedBlock extends HerbBlock {
   private static final VoxelShape SHAPE = Shapes.or(Block.box(2.0, 0.0, 1.0, 15.0, 7.0, 15.0), new VoxelShape[0]);

   public WitchweedBlock(Holder<MobEffect> effect, float seconds, Properties properties) {
      super(effect, seconds, properties);
   }

   protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
      Vec3 offset = state.getOffset(level, pos);
      return SHAPE.move(offset.x, offset.y, offset.z);
   }

   protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
      if (entity instanceof LivingEntity living
         && !living.isSteppingCarefully()
         && !(living instanceof Frog)
         && !(living instanceof SilkMothEntity)
         && !(living instanceof Bee)
         && !(living instanceof Player player && player.getAbilities().instabuild)) {
         living.makeStuckInBlock(state, new Vec3(0.8, 0.75, 0.8));
         if (!level.isClientSide() && level.getDifficulty() != Difficulty.PEACEFUL) {
            living.addEffect(new MobEffectInstance(MobEffects.POISON, 100));
         }
      }
   }
}
