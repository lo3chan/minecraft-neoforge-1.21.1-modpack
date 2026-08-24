package net.mcreator.borninchaosv.block;

import net.mcreator.borninchaosv.block.entity.DarkIceBlockEntity;
import net.mcreator.borninchaosv.procedures.DarkIceKoghdaSushchnostKhoditPoBlokuProcedure;
import net.mcreator.borninchaosv.procedures.DarkIcePriDobavlieniiBlokaProcedure;
import net.mcreator.borninchaosv.procedures.DarkIcePriObnovlieniiTikaProcedure;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.util.FastColor.ARGB32;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class DarkIceBlock extends Block implements EntityBlock {
   public DarkIceBlock() {
      super(
         Properties.of()
            .instrument(NoteBlockInstrument.HAT)
            .sound(SoundType.GLASS)
            .strength(4.0F, 10.0F)
            .friction(0.7F)
            .noOcclusion()
            .isRedstoneConductor((bs, br, bp) -> false)
      );
   }

   public boolean shouldDisplayFluidOverlay(BlockState state, BlockAndTintGetter world, BlockPos pos, FluidState fluidstate) {
      return true;
   }

   public Integer getBeaconColorMultiplier(BlockState state, LevelReader world, BlockPos pos, BlockPos beaconPos) {
      return ARGB32.opaque(-13551037);
   }

   public boolean skipRendering(BlockState state, BlockState adjacentBlockState, Direction side) {
      return adjacentBlockState.getBlock() == this ? true : super.skipRendering(state, adjacentBlockState, side);
   }

   public int getLightBlock(BlockState state, BlockGetter worldIn, BlockPos pos) {
      return 7;
   }

   public VoxelShape getVisualShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
      return Shapes.empty();
   }

   public void onPlace(BlockState blockstate, Level world, BlockPos pos, BlockState oldState, boolean moving) {
      super.onPlace(blockstate, world, pos, oldState, moving);
      world.scheduleTick(pos, this, 1);
      DarkIcePriDobavlieniiBlokaProcedure.execute(world, pos.getX(), pos.getY(), pos.getZ());
   }

   public void tick(BlockState blockstate, ServerLevel world, BlockPos pos, RandomSource random) {
      super.tick(blockstate, world, pos, random);
      DarkIcePriObnovlieniiTikaProcedure.execute(world, pos.getX(), pos.getY(), pos.getZ());
      world.scheduleTick(pos, this, 1);
   }

   public void stepOn(Level world, BlockPos pos, BlockState blockstate, Entity entity) {
      super.stepOn(world, pos, blockstate, entity);
      DarkIceKoghdaSushchnostKhoditPoBlokuProcedure.execute(world, pos.getX(), pos.getY(), pos.getZ(), entity);
   }

   public MenuProvider getMenuProvider(BlockState state, Level worldIn, BlockPos pos) {
      return worldIn.getBlockEntity(pos) instanceof MenuProvider menuProvider ? menuProvider : null;
   }

   public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
      return new DarkIceBlockEntity(pos, state);
   }

   public boolean triggerEvent(BlockState state, Level world, BlockPos pos, int eventID, int eventParam) {
      super.triggerEvent(state, world, pos, eventID, eventParam);
      BlockEntity blockEntity = world.getBlockEntity(pos);
      return blockEntity == null ? false : blockEntity.triggerEvent(eventID, eventParam);
   }

   public boolean hasAnalogOutputSignal(BlockState state) {
      return true;
   }

   public int getAnalogOutputSignal(BlockState blockState, Level world, BlockPos pos) {
      return world.getBlockEntity(pos) instanceof DarkIceBlockEntity be ? AbstractContainerMenu.getRedstoneSignalFromContainer(be) : 0;
   }
}
