package net.mcreator.borninchaosv.block;

import net.mcreator.borninchaosv.block.entity.FelSoilDestructibleBlockEntity;
import net.mcreator.borninchaosv.procedures.FelSoilDestructibleObnovlieniieTikaProcedure;
import net.mcreator.borninchaosv.procedures.FelSoilDestructiblePriDobavlieniiBlokaProcedure;
import net.mcreator.borninchaosv.procedures.FelSoilKoghdaSushchnostKhoditPoBlokuProcedure;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;

public class FelSoilDestructibleBlock extends Block implements EntityBlock {
   public FelSoilDestructibleBlock() {
      super(
         Properties.of()
            .instrument(NoteBlockInstrument.BASEDRUM)
            .sound(SoundType.NETHERRACK)
            .strength(0.6F, 20.0F)
            .lightLevel(s -> 3)
            .requiresCorrectToolForDrops()
            .speedFactor(0.9F)
            .hasPostProcess((bs, br, bp) -> true)
            .emissiveRendering((bs, br, bp) -> true)
      );
   }

   public int getLightBlock(BlockState state, BlockGetter worldIn, BlockPos pos) {
      return 15;
   }

   public void onPlace(BlockState blockstate, Level world, BlockPos pos, BlockState oldState, boolean moving) {
      super.onPlace(blockstate, world, pos, oldState, moving);
      world.scheduleTick(pos, this, 1);
      FelSoilDestructiblePriDobavlieniiBlokaProcedure.execute(world, pos.getX(), pos.getY(), pos.getZ());
   }

   public void tick(BlockState blockstate, ServerLevel world, BlockPos pos, RandomSource random) {
      super.tick(blockstate, world, pos, random);
      FelSoilDestructibleObnovlieniieTikaProcedure.execute(world, pos.getX(), pos.getY(), pos.getZ());
      world.scheduleTick(pos, this, 1);
   }

   public void stepOn(Level world, BlockPos pos, BlockState blockstate, Entity entity) {
      super.stepOn(world, pos, blockstate, entity);
      FelSoilKoghdaSushchnostKhoditPoBlokuProcedure.execute(world, entity);
   }

   public MenuProvider getMenuProvider(BlockState state, Level worldIn, BlockPos pos) {
      return worldIn.getBlockEntity(pos) instanceof MenuProvider menuProvider ? menuProvider : null;
   }

   public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
      return new FelSoilDestructibleBlockEntity(pos, state);
   }

   public boolean triggerEvent(BlockState state, Level world, BlockPos pos, int eventID, int eventParam) {
      super.triggerEvent(state, world, pos, eventID, eventParam);
      BlockEntity blockEntity = world.getBlockEntity(pos);
      return blockEntity == null ? false : blockEntity.triggerEvent(eventID, eventParam);
   }
}
