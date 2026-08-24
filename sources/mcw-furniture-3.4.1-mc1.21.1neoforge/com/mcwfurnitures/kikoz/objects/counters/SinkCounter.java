package com.mcwfurnitures.kikoz.objects.counters;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class SinkCounter extends Counter {
   public static final BooleanProperty WATER = BooleanProperty.create("water");
   protected static final VoxelShape BASE = Shapes.or(Block.box(0.0, 0.0, 0.0, 16.0, 16.0, 16.0), new VoxelShape[0]);

   public SinkCounter(Properties properties) {
      super(properties);
      this.registerDefaultState((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH)).setValue(WATER, false));
   }

   public ItemInteractionResult useItemOn(
      ItemStack itemstack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit
   ) {
      Item item = itemstack.getItem();
      boolean hasWater = (Boolean)state.getValue(WATER);
      Direction facing = (Direction)state.getValue(FACING);
      if (item == Items.WATER_BUCKET && !hasWater) {
         state = (BlockState)state.cycle(WATER);
         level.setBlock(pos, state, 2);
         level.playSound(null, pos, SoundEvents.BUCKET_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
         player.setItemInHand(hand, new ItemStack(Items.BUCKET));
         return ItemInteractionResult.SUCCESS;
      } else if (item == Items.BUCKET && hasWater) {
         state = (BlockState)state.setValue(WATER, false);
         level.setBlock(pos, state, 2);
         level.playSound(null, pos, SoundEvents.BUCKET_FILL, SoundSource.BLOCKS, 1.0F, 1.0F);
         ItemStack filledBucket = new ItemStack(Items.WATER_BUCKET);
         if (!player.getAbilities().instabuild) {
            itemstack.shrink(1);
            if (itemstack.isEmpty()) {
               player.setItemInHand(hand, filledBucket);
            } else if (!player.getInventory().add(filledBucket)) {
               player.drop(filledBucket, false);
            }
         } else if (!player.getInventory().add(filledBucket)) {
            player.drop(filledBucket, false);
         }

         return ItemInteractionResult.SUCCESS;
      } else if (item == Items.GLASS_BOTTLE && hasWater) {
         state = (BlockState)state.cycle(WATER);
         level.setBlock(pos, state, 2);
         level.playSound(null, pos, SoundEvents.BOTTLE_FILL, SoundSource.BLOCKS, 1.0F, 1.0F);
         ItemStack waterBottle = new ItemStack(Items.POTION);
         CompoundTag tag = new CompoundTag();
         tag.putString("Potion", "minecraft:water");
         waterBottle.set(DataComponents.POTION_CONTENTS, new PotionContents(Potions.WATER));
         if (!player.getAbilities().instabuild) {
            itemstack.shrink(1);
            if (itemstack.isEmpty()) {
               player.setItemInHand(hand, waterBottle);
            } else if (!player.getInventory().add(waterBottle)) {
               player.drop(waterBottle, false);
            }
         }

         return ItemInteractionResult.SUCCESS;
      } else if (!level.isClientSide) {
         BlockState newState = (BlockState)state.cycle(WATER);
         level.setBlock(pos, newState, 3);
         level.playSound(null, pos, SoundEvents.BUCKET_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
         return ItemInteractionResult.SUCCESS;
      } else {
         if (!hasWater) {
            double baseX = pos.getX() + 0.5;
            double baseZ = pos.getZ() + 0.5;
            double offset = 0.1;
            switch (facing) {
               case NORTH:
                  baseZ -= offset;
                  break;
               case SOUTH:
                  baseZ += offset;
                  break;
               case EAST:
                  baseX += offset;
                  break;
               case WEST:
                  baseX -= offset;
            }

            for (int i = 0; i < 20; i++) {
               double y = pos.getY() + 1 - i * 0.001;
               level.addParticle(ParticleTypes.FALLING_DRIPSTONE_WATER, baseX, y, baseZ, 0.0, -1.0E-4, 0.0);
            }
         }

         return ItemInteractionResult.SUCCESS;
      }
   }

   @Override
   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(new Property[]{FACING, WATER});
   }
}
