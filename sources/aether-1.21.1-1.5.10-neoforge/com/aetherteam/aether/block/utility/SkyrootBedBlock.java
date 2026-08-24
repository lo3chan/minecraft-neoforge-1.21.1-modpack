package com.aetherteam.aether.block.utility;

import com.aetherteam.aether.blockentity.SkyrootBedBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.properties.BedPart;

public class SkyrootBedBlock extends BedBlock {
   public SkyrootBedBlock(Properties properties) {
      super(DyeColor.CYAN, properties);
      this.registerDefaultState((BlockState)((BlockState)((BlockState)this.getStateDefinition().any()).setValue(PART, BedPart.FOOT)).setValue(OCCUPIED, false));
   }

   public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
      return new SkyrootBedBlockEntity(pos, state);
   }
}
