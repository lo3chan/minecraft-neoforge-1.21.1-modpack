package com.aetherteam.aether.block.natural;

import com.aetherteam.aether.block.AetherBlockStateProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HalfTransparentBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class AercloudBlock extends HalfTransparentBlock {
   protected static final VoxelShape COLLISION_SHAPE = Block.box(0.0, 0.0, 0.0, 16.0, 0.01, 16.0);
   protected static final VoxelShape FALLING_COLLISION_SHAPE = Shapes.box(0.0, 0.0, 0.0, 1.0, 0.9, 1.0);

   public AercloudBlock(Properties properties) {
      super(properties);
      this.registerDefaultState((BlockState)this.defaultBlockState().setValue(AetherBlockStateProperties.DOUBLE_DROPS, false));
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(new Property[]{AetherBlockStateProperties.DOUBLE_DROPS});
   }

   public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
      entity.resetFallDistance();
      if (entity.getDeltaMovement().y < 0.0 && !(entity instanceof Projectile)) {
         entity.setDeltaMovement(entity.getDeltaMovement().multiply(1.0, 0.005, 1.0));
      }

      entity.setOnGround(entity instanceof LivingEntity livingEntity && (!(livingEntity instanceof Player player) || !player.getAbilities().flying));
   }

   public void fallOn(Level level, BlockState state, BlockPos pos, Entity entity, float fallDistance) {
   }

   public boolean propagatesSkylightDown(BlockState state, BlockGetter level, BlockPos pos) {
      return false;
   }

   public float getShadeBrightness(BlockState state, BlockGetter level, BlockPos pos) {
      return 0.25F;
   }

   public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
      if (!this.getDefaultCollisionShape(state, level, pos, context).isEmpty() && level.getBlockState(pos.above()).getBlock() instanceof AercloudBlock) {
         return Shapes.block();
      } else {
         if (context instanceof EntityCollisionContext entityCollisionContext) {
            Entity entity = entityCollisionContext.getEntity();
            if (entity != null && entity.fallDistance > 2.5F && (!(entity instanceof LivingEntity livingEntity) || !livingEntity.isFallFlying())) {
               return FALLING_COLLISION_SHAPE;
            }
         }

         return this.getDefaultCollisionShape(state, level, pos, context);
      }
   }

   protected VoxelShape getDefaultCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
      return COLLISION_SHAPE;
   }

   public VoxelShape getVisualShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
      return Shapes.empty();
   }
}
