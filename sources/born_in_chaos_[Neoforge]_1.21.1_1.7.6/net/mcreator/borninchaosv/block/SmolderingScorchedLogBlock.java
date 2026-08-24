package net.mcreator.borninchaosv.block;

import net.mcreator.borninchaosv.procedures.ScorchedLogKoghdaIghrokNachinaietUnichtozhatProcedure;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.block.state.properties.Property;

public class SmolderingScorchedLogBlock extends Block {
   public static final EnumProperty<Axis> AXIS = BlockStateProperties.AXIS;

   public SmolderingScorchedLogBlock() {
      super(Properties.of().ignitedByLava().instrument(NoteBlockInstrument.BASS).sound(SoundType.WOOD).strength(2.0F));
      this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(AXIS, Axis.Y));
   }

   public int getLightBlock(BlockState state, BlockGetter worldIn, BlockPos pos) {
      return 15;
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      super.createBlockStateDefinition(builder);
      builder.add(new Property[]{AXIS});
   }

   public BlockState getStateForPlacement(BlockPlaceContext context) {
      return (BlockState)super.getStateForPlacement(context).setValue(AXIS, context.getClickedFace().getAxis());
   }

   public BlockState rotate(BlockState state, Rotation rot) {
      if (rot == Rotation.CLOCKWISE_90 || rot == Rotation.COUNTERCLOCKWISE_90) {
         if (state.getValue(AXIS) == Axis.X) {
            return (BlockState)state.setValue(AXIS, Axis.Z);
         }

         if (state.getValue(AXIS) == Axis.Z) {
            return (BlockState)state.setValue(AXIS, Axis.X);
         }
      }

      return state;
   }

   public void attack(BlockState blockstate, Level world, BlockPos pos, Player entity) {
      super.attack(blockstate, world, pos, entity);
      ScorchedLogKoghdaIghrokNachinaietUnichtozhatProcedure.execute(world, pos.getX(), pos.getY(), pos.getZ());
   }
}
