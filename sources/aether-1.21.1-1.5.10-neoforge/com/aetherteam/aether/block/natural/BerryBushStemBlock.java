package com.aetherteam.aether.block.natural;

import com.aetherteam.aether.AetherConfig;
import com.aetherteam.aether.block.AetherBlockStateProperties;
import com.aetherteam.aether.block.AetherBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.common.CommonHooks;

public class BerryBushStemBlock extends AetherBushBlock implements BonemealableBlock {
   protected static final VoxelShape SHAPE = Block.box(2.0, 0.0, 2.0, 14.0, 13.0, 14.0);

   public BerryBushStemBlock(Properties properties) {
      super(properties);
      this.registerDefaultState((BlockState)this.defaultBlockState().setValue(AetherBlockStateProperties.DOUBLE_DROPS, false));
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(new Property[]{AetherBlockStateProperties.DOUBLE_DROPS});
   }

   public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
      if ((Boolean)AetherConfig.SERVER.berry_bush_consistency.get()
         && entity instanceof LivingEntity
         && entity.getType() != EntityType.FOX
         && entity.getType() != EntityType.BEE) {
         entity.makeStuckInBlock(state, new Vec3(0.800000011920929, 0.75, 0.800000011920929));
      }
   }

   public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
      if (level.getRawBrightness(pos.above(), 0) >= 9 && CommonHooks.canCropGrow(level, pos, state, random.nextInt(60) == 0)) {
         level.setBlockAndUpdate(
            pos,
            (BlockState)((Block)AetherBlocks.BERRY_BUSH.get())
               .defaultBlockState()
               .setValue(AetherBlockStateProperties.DOUBLE_DROPS, (Boolean)state.getValue(AetherBlockStateProperties.DOUBLE_DROPS))
         );
         CommonHooks.fireCropGrowPost(level, pos, state);
      }
   }

   public boolean isRandomlyTicking(BlockState blockState) {
      return true;
   }

   public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state) {
      level.setBlockAndUpdate(
         pos,
         (BlockState)((Block)AetherBlocks.BERRY_BUSH.get())
            .defaultBlockState()
            .setValue(AetherBlockStateProperties.DOUBLE_DROPS, (Boolean)state.getValue(AetherBlockStateProperties.DOUBLE_DROPS))
      );
   }

   public boolean isBonemealSuccess(Level level, RandomSource random, BlockPos pos, BlockState state) {
      return random.nextFloat() <= 0.45;
   }

   public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state) {
      return true;
   }

   public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
      return SHAPE;
   }
}
