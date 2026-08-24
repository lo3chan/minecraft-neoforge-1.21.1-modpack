package net.mehvahdjukaar.amendments.common.block;

import com.mojang.serialization.MapCodec;
import net.mehvahdjukaar.amendments.common.item.DyeBottleItem;
import net.mehvahdjukaar.amendments.common.tile.LiquidCauldronBlockTile;
import net.mehvahdjukaar.amendments.reg.ModRegistry;
import net.mehvahdjukaar.moonlight.api.fluids.SoftFluidStack;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.GameEvent.Context;
import net.minecraft.world.level.material.Fluid;

public class DyeCauldronBlock extends ModCauldronBlock {
   public static final MapCodec<DyeCauldronBlock> CODEC = simpleCodec(DyeCauldronBlock::new);
   public static final IntegerProperty LEVEL = BlockStateProperties.LEVEL_CAULDRON;

   public DyeCauldronBlock(Properties properties) {
      super(properties);
      this.registerDefaultState((BlockState)((BlockState)this.defaultBlockState().setValue(BOILING, false)).setValue(LEVEL, 1));
   }

   protected MapCodec<? extends DyeCauldronBlock> codec() {
      return CODEC;
   }

   @Override
   public IntegerProperty getLevelProperty() {
      return LEVEL;
   }

   @Override
   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      super.createBlockStateDefinition(builder);
      builder.add(new Property[]{LEVEL});
   }

   protected boolean canReceiveStalactiteDrip(Fluid fluid) {
      return false;
   }

   protected void receiveStalactiteDrip(BlockState state, Level level, BlockPos pos, Fluid fluid) {
   }

   @Override
   protected void handleEntityInsideFluidSpecial(BlockState state, Level level, BlockPos pos, Entity entity) {
      if (entity instanceof Sheep sheep && level.getBlockEntity(pos) instanceof LiquidCauldronBlockTile te) {
         SoftFluidStack fluid = te.getSoftFluidTank().getFluid();
         if (fluid.is(ModRegistry.DYE_SOFT_FLUID)) {
            DyeColor dye = DyeBottleItem.getClosestDye(fluid);
            if (sheep.getColor() != dye) {
               sheep.setColor(dye);
               te.consumeOneLayer();
               level.gameEvent(entity, GameEvent.BLOCK_CHANGE, pos);
            }
         }
      }
   }

   public static void playDyeSoundAndConsume(BlockState state, BlockPos pos, Level level, Player player, ItemStack stack) {
      if (player instanceof ServerPlayer serverPlayer) {
         level.gameEvent(GameEvent.BLOCK_CHANGE, pos, Context.of(player, state));
         player.awardStat(Stats.ITEM_USED.get(stack.getItem()));
         CriteriaTriggers.ITEM_USED_ON_BLOCK.trigger(serverPlayer, pos, stack);
      }

      level.playSound(player, pos, SoundEvents.DYE_USE, SoundSource.BLOCKS, 1.0F, 1.0F);
      level.playSound(player, pos, SoundEvents.BUCKET_EMPTY, SoundSource.BLOCKS, 1.0F, 1.3F);
      stack.consume(1, player);
   }
}
