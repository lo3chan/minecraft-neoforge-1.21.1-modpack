package com.mcwpaths.kikoz.objects;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class EngravedBlock extends Block {
   public static final BooleanProperty ENGRAVED = BooleanProperty.create("engraved");
   protected static final VoxelShape CUBE = Block.box(0.0, 0.0, 0.0, 16.0, 16.0, 16.0);
   protected static final VoxelShape ENGRAVE = Block.box(0.0, 0.0, 0.0, 16.0, 15.0, 16.0);

   public EngravedBlock(Properties properties) {
      super(properties);
      this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(ENGRAVED, false));
   }

   public VoxelShape getShape(BlockState state, BlockGetter reader, BlockPos pos, CollisionContext context) {
      boolean engraved = (Boolean)state.getValue(ENGRAVED);
      return engraved ? ENGRAVE : CUBE;
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(new Property[]{ENGRAVED});
   }

   public ItemInteractionResult useItemOn(
      ItemStack pStack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit
   ) {
      ItemStack heldItem = player.getItemInHand(hand);
      Item item = heldItem.getItem();
      if (heldItem.is(ItemTags.PICKAXES)) {
         if (level.isClientSide()) {
            return ItemInteractionResult.SUCCESS;
         } else {
            level.playSound(null, pos, SoundEvents.UI_STONECUTTER_TAKE_RESULT, SoundSource.BLOCKS, 1.0F, level.getRandom().nextFloat() * 0.1F + 0.9F);
            BlockState newState = (BlockState)state.cycle(ENGRAVED);
            level.setBlock(pos, newState, 3);
            if (!player.isCreative()) {
               heldItem.hurtAndBreak(1, player, LivingEntity.getSlotForHand(hand));
            }

            return ItemInteractionResult.SUCCESS;
         }
      } else {
         return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
      }
   }
}
