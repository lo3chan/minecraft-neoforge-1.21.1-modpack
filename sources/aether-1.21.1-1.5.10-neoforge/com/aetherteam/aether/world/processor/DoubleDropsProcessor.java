package com.aetherteam.aether.world.processor;

import com.aetherteam.aether.block.AetherBlockStateProperties;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;
import org.jetbrains.annotations.Nullable;

public class DoubleDropsProcessor extends StructureProcessor {
   public static final DoubleDropsProcessor INSTANCE = new DoubleDropsProcessor();
   public static final MapCodec<DoubleDropsProcessor> CODEC = MapCodec.unit(INSTANCE);

   @Nullable
   public StructureBlockInfo process(
      LevelReader level,
      BlockPos origin,
      BlockPos centerBottom,
      StructureBlockInfo originalBlockInfo,
      StructureBlockInfo modifiedBlockInfo,
      StructurePlaceSettings settings,
      @Nullable StructureTemplate template
   ) {
      return modifiedBlockInfo.state().hasProperty(AetherBlockStateProperties.DOUBLE_DROPS)
         ? new StructureBlockInfo(
            modifiedBlockInfo.pos(), (BlockState)modifiedBlockInfo.state().setValue(AetherBlockStateProperties.DOUBLE_DROPS, true), modifiedBlockInfo.nbt()
         )
         : super.process(level, origin, centerBottom, originalBlockInfo, modifiedBlockInfo, settings, template);
   }

   protected StructureProcessorType<?> getType() {
      return (StructureProcessorType<?>)AetherStructureProcessors.DOUBLE_DROPS.get();
   }
}
