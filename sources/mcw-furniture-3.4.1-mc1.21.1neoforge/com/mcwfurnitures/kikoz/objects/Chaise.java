package com.mcwfurnitures.kikoz.objects;

import com.mcwfurnitures.kikoz.storage.CouchEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class Chaise extends FurnitureObjectNonFaceable {
   private static final VoxelShape BASE = Shapes.or(
      Block.box(0.0, 0.0, 14.0, 2.0, 2.0, 16.0),
      new VoxelShape[]{
         Block.box(14.0, 0.0, 14.0, 16.0, 2.0, 16.0),
         Block.box(14.0, 0.0, 0.0, 16.0, 2.0, 2.0),
         Block.box(0.0, 2.0, 0.0, 16.0, 7.0, 16.0),
         Block.box(0.0, 0.0, 0.0, 2.0, 2.0, 2.0),
         Block.box(1.0, 1.0, 1.0, 15.0, 2.0, 15.0)
      }
   );

   public Chaise(Properties properties) {
      super(properties);
      this.registerDefaultState((BlockState)this.stateDefinition.any());
   }

   public ItemInteractionResult useItemOn(
      ItemStack itemstack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit
   ) {
      return CouchEntity.create(level, pos, 0.3, player);
   }

   @Override
   public VoxelShape getShape(BlockState state, BlockGetter blockReader, BlockPos pos, CollisionContext selectionContext) {
      return BASE;
   }
}
