package com.mcwdoors.kikoz.objects;

import com.mcwdoors.kikoz.init.SoundsInit;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoorHingeSide;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class JapaneseDoors extends DoorBlock {
   public static final EnumProperty<DoorHingeSide> HINGE = BlockStateProperties.DOOR_HINGE;
   protected static final VoxelShape EAST = Block.box(0.0, 0.0, 7.0, 16.0, 16.0, 9.0);
   protected static final VoxelShape NORTH = Block.box(7.0, 0.0, 0.0, 9.0, 16.0, 16.0);

   public VoxelShape getShape(BlockState state, BlockGetter reader, BlockPos pos, CollisionContext context) {
      switch ((Direction)state.getValue(FACING)) {
         case NORTH:
            return EAST;
         case SOUTH:
            return EAST;
         case EAST:
            return NORTH;
         case WEST:
         default:
            return NORTH;
      }
   }

   public VoxelShape getCollisionShape(BlockState state, BlockGetter reader, BlockPos pos, CollisionContext context) {
      if ((Boolean)state.getValue(OPEN)) {
         return Shapes.empty();
      } else {
         switch ((Direction)state.getValue(FACING)) {
            case NORTH:
            case SOUTH:
               return EAST;
            case EAST:
            case WEST:
               return NORTH;
            default:
               return null;
         }
      }
   }

   public JapaneseDoors(Properties properties, BlockSetType type) {
      super(type, properties);
      this.registerDefaultState(
         (BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH))
                     .setValue(OPEN, false))
                  .setValue(HINGE, DoorHingeSide.LEFT))
               .setValue(POWERED, false))
            .setValue(HALF, DoubleBlockHalf.LOWER)
      );
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(new Property[]{HALF, FACING, OPEN, HINGE, POWERED});
   }

   private void playSound(Level level, BlockPos pos, boolean open) {
      level.playSound(
         null,
         pos,
         open ? (SoundEvent)SoundsInit.SHOJI.get() : (SoundEvent)SoundsInit.SHOJI.get(),
         SoundSource.BLOCKS,
         1.0F,
         level.random.nextFloat() * 0.1F + 0.9F
      );
   }

   public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos postwo, boolean bool) {
      boolean powered = level.hasNeighborSignal(pos)
         || level.hasNeighborSignal(pos.relative(state.getValue(HALF) == DoubleBlockHalf.LOWER ? Direction.UP : Direction.DOWN));
      if (!this.defaultBlockState().is(block) && powered != (Boolean)state.getValue(POWERED)) {
         if (powered != (Boolean)state.getValue(OPEN)) {
            this.playSound(level, pos, powered);
            level.gameEvent(null, powered ? GameEvent.BLOCK_OPEN : GameEvent.BLOCK_CLOSE, pos);
         }

         level.setBlock(pos, (BlockState)((BlockState)state.setValue(POWERED, powered)).setValue(OPEN, powered), 2);
      }
   }

   public void setOpen(@Nullable Entity entity, Level level, BlockState state, BlockPos pos, boolean open) {
      if (state.is(this) && (Boolean)state.getValue(OPEN) != open) {
         level.setBlock(pos, (BlockState)state.setValue(OPEN, open), 10);
         this.playSound(level, pos, open);
         level.gameEvent(entity, open ? GameEvent.BLOCK_OPEN : GameEvent.BLOCK_CLOSE, pos);
      }
   }

   public InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
      if (this.type() == BlockSetType.IRON) {
         return InteractionResult.PASS;
      } else {
         state = (BlockState)state.cycle(OPEN);
         level.setBlock(pos, state, 2);
         this.playSound(level, pos, (Boolean)state.getValue(OPEN));
         level.gameEvent(player, state.getValue(OPEN) ? GameEvent.BLOCK_OPEN : GameEvent.BLOCK_CLOSE, pos);
         return InteractionResult.SUCCESS;
      }
   }
}
