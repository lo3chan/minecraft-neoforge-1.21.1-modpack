package net.astralya.hexalia.block.custom;

import net.astralya.hexalia.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.GameEvent.Context;
import net.minecraft.world.phys.BlockHitResult;

public class SunfireTomatoCropBlock extends CropBlock {
   public static final int MAX_AGE = 3;
   public static final IntegerProperty AGE = IntegerProperty.create("age", 0, 3);

   public SunfireTomatoCropBlock(Properties properties) {
      super(properties);
      this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(AGE, 0));
   }

   protected ItemLike getBaseSeedId() {
      return (ItemLike)ModItems.SUNFIRE_TOMATO_SEEDS.get();
   }

   public IntegerProperty getAgeProperty() {
      return AGE;
   }

   public int getMaxAge() {
      return 3;
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(new Property[]{AGE});
   }

   protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
      int age = (Integer)state.getValue(AGE);
      boolean mature = age == 3;
      if (age > 1) {
         int count = 1 + level.random.nextInt(2) + (mature ? 1 : 0);
         popResource(level, pos, new ItemStack((ItemLike)ModItems.SUNFIRE_TOMATO.get(), count));
         level.playSound(null, pos, SoundEvents.SWEET_BERRY_BUSH_PICK_BERRIES, SoundSource.BLOCKS, 1.0F, 0.8F + level.random.nextFloat() * 0.4F);
         BlockState harvested = (BlockState)state.setValue(AGE, 1);
         level.setBlock(pos, harvested, 2);
         level.gameEvent(GameEvent.BLOCK_CHANGE, pos, Context.of(player, harvested));
         return InteractionResult.sidedSuccess(level.isClientSide());
      } else {
         return super.useWithoutItem(state, level, pos, player, hitResult);
      }
   }

   protected ItemInteractionResult useItemOn(
      ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult
   ) {
      return state.getValue(AGE) != 3 && stack.is(Items.BONE_MEAL)
         ? ItemInteractionResult.SKIP_DEFAULT_BLOCK_INTERACTION
         : super.useItemOn(stack, state, level, pos, player, hand, hitResult);
   }
}
