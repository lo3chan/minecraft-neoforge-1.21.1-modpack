package net.joefoxe.hexerei.block.custom;

import java.util.function.Supplier;
import net.joefoxe.hexerei.tileentity.ModSignBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.WallSignBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.properties.WoodType;

public class ModWallSign extends WallSignBlock {
   protected final Supplier<BlockEntityType<ModSignBlockEntity>> blockEntityType;

   public ModWallSign(Properties pProperties, Supplier<BlockEntityType<ModSignBlockEntity>> blockEntityType, WoodType pType) {
      super(pType, pProperties);
      this.blockEntityType = blockEntityType;
   }

   public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
      return new ModSignBlockEntity(pPos, pState);
   }
}
