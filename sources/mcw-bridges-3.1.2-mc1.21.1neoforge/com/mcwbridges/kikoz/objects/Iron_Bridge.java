package com.mcwbridges.kikoz.objects;

import com.mcwbridges.kikoz.init.ItemInit;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class Iron_Bridge extends Bridge_Block {
   protected static final VoxelShape SIDE_0 = Shapes.or(BASE, Block.box(0.0, 1.0, 15.0, 16.0, 16.0, 16.0));
   protected static final VoxelShape SIDE_90 = Shapes.or(BASE, Block.box(0.0, 1.0, 0.0, 1.0, 16.0, 16.0));
   protected static final VoxelShape SIDE_180 = Shapes.or(BASE, Block.box(0.0, 1.0, 0.0, 16.0, 16.0, 1.0));
   protected static final VoxelShape SIDE_270 = Shapes.or(BASE, Block.box(15.0, 1.0, 0.0, 16.0, 16.0, 16.0));
   protected static final VoxelShape CORNER_0 = Shapes.or(SIDE_180, SIDE_90);
   protected static final VoxelShape CORNER_90 = Shapes.or(SIDE_180, SIDE_270);
   protected static final VoxelShape CORNER_180 = Shapes.or(SIDE_270, SIDE_0);
   protected static final VoxelShape CORNER_270 = Shapes.or(SIDE_0, SIDE_90);
   protected static final VoxelShape MIDDLE_90 = Shapes.or(SIDE_0, SIDE_180);
   protected static final VoxelShape MIDDLE_0 = Shapes.or(SIDE_90, SIDE_270);

   public Iron_Bridge(Properties prop) {
      super(prop);
      this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(CONNECTION, Bridge_Block.ConnectionStatus.BASE));
   }

   @Override
   public VoxelShape getShape(BlockState state, BlockGetter reader, BlockPos pos, CollisionContext contx) {
      switch ((Bridge_Block.ConnectionStatus)state.getValue(CONNECTION)) {
         case BASE:
            return BASE;
         case CORNER_NE:
            return CORNER_270;
         case CORNER_NW:
            return CORNER_180;
         case CORNER_SE:
            return CORNER_0;
         case CORNER_SW:
            return CORNER_90;
         case MIDDLE_EW:
            return MIDDLE_0;
         case MIDDLE_NS:
            return MIDDLE_90;
         case SIDE_E:
            return SIDE_270;
         case SIDE_N:
            return SIDE_180;
         case SIDE_S:
            return SIDE_0;
         case SIDE_W:
            return SIDE_90;
         default:
            return BASE;
      }
   }

   @Override
   public ItemInteractionResult useItemOn(
      ItemStack itemstack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit
   ) {
      Item item = itemstack.getItem();
      if (item != ItemInit.PLIERS.get() && item != Items.SHEARS) {
         if (itemstack.getItem() instanceof BlockItem) {
            BlockItem blockItem = (BlockItem)itemstack.getItem();
            if (blockItem.getBlock() == this) {
               Direction facing = player.getDirection();
               BlockPos placePos = pos.relative(facing);
               if (level.getBlockState(placePos).isAir()) {
                  level.setBlock(placePos, this.defaultBlockState(), 3);
                  if (!player.getAbilities().instabuild) {
                     itemstack.shrink(1);
                  }

                  level.playSound(null, pos, SoundEvents.METAL_PLACE, SoundSource.BLOCKS, 1.0F, 1.0F);
                  return ItemInteractionResult.SUCCESS;
               }
            }
         }

         return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
      } else {
         BlockState newState = (BlockState)state.cycle(CONNECTION);
         level.setBlock(pos, newState, 18);
         return ItemInteractionResult.SUCCESS;
      }
   }
}
