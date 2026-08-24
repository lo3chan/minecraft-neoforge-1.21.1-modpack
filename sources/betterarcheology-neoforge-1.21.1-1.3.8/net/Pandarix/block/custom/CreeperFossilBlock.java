package net.Pandarix.block.custom;

import com.google.common.collect.ImmutableMap;
import java.util.List;
import java.util.Map;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

public class CreeperFossilBlock extends FossilBaseBlock {
   private static final Map<Direction, VoxelShape> CREEPER_SHAPES_FOR_DIRECTION = ImmutableMap.of(
      Direction.NORTH,
      Shapes.or(
         Block.box(3.5, 17.25, 3.5, 12.5, 26.25, 12.5),
         new VoxelShape[]{Block.box(3.5, 5.25, 5.5, 12.5, 17.25, 10.5), Block.box(3.0, 0.0, 9.5, 13.0, 6.5, 14.5), Block.box(3.0, 0.0, 1.5, 13.0, 6.5, 6.5)}
      ),
      Direction.SOUTH,
      Shapes.or(
         Block.box(3.5, 17.25, 3.5, 12.5, 26.25, 12.5),
         new VoxelShape[]{Block.box(3.5, 5.25, 5.5, 12.5, 17.25, 10.5), Block.box(3.0, 0.0, 9.5, 13.0, 6.5, 14.5), Block.box(3.0, 0.0, 1.5, 13.0, 6.5, 6.5)}
      ),
      Direction.WEST,
      Shapes.or(
         Block.box(3.5, 17.25, 3.5, 12.5, 26.25, 12.5),
         new VoxelShape[]{Block.box(5.5, 5.25, 3.5, 10.5, 17.25, 12.5), Block.box(1.5, 0.0, 3.0, 6.5, 6.5, 13.0), Block.box(9.5, 0.0, 3.0, 14.5, 6.5, 13.0)}
      ),
      Direction.EAST,
      Shapes.or(
         Block.box(3.5, 17.25, 3.5, 12.5, 26.25, 12.5),
         new VoxelShape[]{Block.box(5.5, 5.25, 3.5, 10.5, 17.25, 12.5), Block.box(1.5, 0.0, 3.0, 6.5, 6.5, 13.0), Block.box(9.5, 0.0, 3.0, 14.5, 6.5, 13.0)}
      )
   );

   public CreeperFossilBlock(Properties settings) {
      super(settings);
   }

   public void animateTick(BlockState blockState, Level level, BlockPos pos, RandomSource random) {
      super.animateTick(blockState, level, pos, random);
      ParticleOptions particle = random.nextBoolean() ? ParticleTypes.SMALL_FLAME : ParticleTypes.SMOKE;
      Vec3 center = pos.getCenter();
      if (level.isClientSide()) {
         level.addParticle(
            particle,
            center.x() + random.nextFloat() * getRandomSign(random),
            center.y() + random.nextFloat() * getRandomSign(random),
            center.z() + random.nextFloat() * getRandomSign(random),
            random.nextFloat() / 50.0F * getRandomSign(random),
            random.nextFloat() / 30.0F,
            random.nextFloat() / 50.0F * getRandomSign(random)
         );
      }
   }

   private static int getRandomSign(RandomSource rand) {
      return rand.nextBoolean() ? 1 : -1;
   }

   @NotNull
   public VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
      return CREEPER_SHAPES_FOR_DIRECTION.get(blockState.getValue(FACING));
   }

   public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> component, TooltipFlag flag) {
      component.add(Component.translatable("block.betterarcheology.creeper_fossil_tooltip").withStyle(ChatFormatting.GRAY));
      super.appendHoverText(stack, context, component, flag);
   }
}
