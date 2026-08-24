package com.aetherteam.aether.block.construction;

import com.aetherteam.aether.blockentity.SkyrootSignBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.StandingSignBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.properties.WoodType;

public class SkyrootSignBlock extends StandingSignBlock {
   public SkyrootSignBlock(WoodType type, Properties properties) {
      super(type, properties);
   }

   public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
      return new SkyrootSignBlockEntity(pos, state);
   }
}
