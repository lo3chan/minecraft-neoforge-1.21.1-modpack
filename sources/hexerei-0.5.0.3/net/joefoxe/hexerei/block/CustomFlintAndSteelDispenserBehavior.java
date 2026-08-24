package net.joefoxe.hexerei.block;

import net.joefoxe.hexerei.block.custom.Candle;
import net.joefoxe.hexerei.block.custom.SageBurningPlate;
import net.joefoxe.hexerei.data.candle.CandleData;
import net.joefoxe.hexerei.item.ModItems;
import net.joefoxe.hexerei.tileentity.CandleTile;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.gameevent.GameEvent;

public class CustomFlintAndSteelDispenserBehavior extends CustomVanillaItemDispenseBehavior {
   public CustomFlintAndSteelDispenserBehavior(DispenseItemBehavior behavior) {
      super(behavior);
   }

   @Override
   protected ItemStack execute(BlockSource source, ItemStack stack) {
      Level level = source.level();
      this.setSuccess(false);
      Direction direction = (Direction)source.state().getValue(DispenserBlock.FACING);
      BlockPos blockpos = source.pos().relative(direction);
      BlockState blockstate = level.getBlockState(blockpos);
      if (Candle.canBeLit(blockstate, blockpos, level)) {
         CandleTile tile = (CandleTile)level.getBlockEntity(blockpos);
         boolean flag = false;
         if (blockstate.getBlock() instanceof Candle && tile != null) {
            if (((CandleData)tile.candles.get(0)).hasCandle && !((CandleData)tile.candles.get(0)).lit) {
               ((CandleData)tile.candles.get(0)).lit = true;
            } else if (((CandleData)tile.candles.get(1)).hasCandle && !((CandleData)tile.candles.get(1)).lit) {
               ((CandleData)tile.candles.get(1)).lit = true;
            } else if (((CandleData)tile.candles.get(2)).hasCandle && !((CandleData)tile.candles.get(2)).lit) {
               ((CandleData)tile.candles.get(2)).lit = true;
            } else if (((CandleData)tile.candles.get(3)).hasCandle && !((CandleData)tile.candles.get(3)).lit) {
               ((CandleData)tile.candles.get(3)).lit = true;
            } else {
               flag = true;
            }
         }

         if (!flag) {
            level.playSound(null, blockpos, SoundEvents.FLINTANDSTEEL_USE, SoundSource.BLOCKS, 1.0F, RandomSource.create().nextFloat() * 0.4F + 1.0F);
            if (blockstate.hasProperty(BlockStateProperties.LIT)) {
               level.setBlockAndUpdate(blockpos, (BlockState)blockstate.setValue(BlockStateProperties.LIT, true));
            }

            level.gameEvent(null, GameEvent.BLOCK_CHANGE, blockpos);
            if (this.isSuccess()) {
               stack.setDamageValue(stack.getDamageValue() + 1);
               if (stack.getDamageValue() >= stack.getMaxDamage()) {
                  stack.setCount(0);
               }
            }

            this.setSuccess(true);
         }
      }

      if (blockstate.getBlock() instanceof SageBurningPlate sageBurningPlate) {
         sageBurningPlate.withTileEntityDo(level, blockpos, te -> {
            if (((ItemStack)te.getItems().getFirst()).is((Item)ModItems.DRIED_SAGE_BUNDLE.get()) && !(Boolean)blockstate.getValue(BlockStateProperties.LIT)) {
               if (blockstate.hasProperty(BlockStateProperties.LIT)) {
                  level.setBlockAndUpdate(blockpos, (BlockState)blockstate.setValue(BlockStateProperties.LIT, true));
               }

               level.gameEvent(null, GameEvent.BLOCK_CHANGE, blockpos);
               if (this.isSuccess()) {
                  stack.setDamageValue(stack.getDamageValue() + 1);
                  if (stack.getDamageValue() >= stack.getMaxDamage()) {
                     stack.setCount(0);
                  }
               }

               this.setSuccess(true);
            }
         });
      }

      return super.execute(source, stack);
   }
}
