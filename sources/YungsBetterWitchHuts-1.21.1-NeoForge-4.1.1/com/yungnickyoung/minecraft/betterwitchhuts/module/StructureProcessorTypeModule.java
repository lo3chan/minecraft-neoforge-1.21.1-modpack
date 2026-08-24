package com.yungnickyoung.minecraft.betterwitchhuts.module;

import com.yungnickyoung.minecraft.betterwitchhuts.world.processor.BrewingStandProcessor;
import com.yungnickyoung.minecraft.betterwitchhuts.world.processor.FenceLegProcessor;
import com.yungnickyoung.minecraft.betterwitchhuts.world.processor.LegProcessor;
import com.yungnickyoung.minecraft.betterwitchhuts.world.processor.PottedMushroomProcessor;
import com.yungnickyoung.minecraft.betterwitchhuts.world.processor.WitchCircleProcessor;
import com.yungnickyoung.minecraft.yungsapi.api.autoregister.AutoRegister;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;

@AutoRegister("betterwitchhuts")
public class StructureProcessorTypeModule {
   @AutoRegister("leg_processor")
   public static StructureProcessorType<LegProcessor> LEG_PROCESSOR = () -> LegProcessor.CODEC;
   @AutoRegister("fence_leg_processor")
   public static StructureProcessorType<FenceLegProcessor> FENCE_LEG_PROCESSOR = () -> FenceLegProcessor.CODEC;
   @AutoRegister("witch_circle_processor")
   public static StructureProcessorType<WitchCircleProcessor> WITCH_CIRCLE_PROCESSOR = () -> WitchCircleProcessor.CODEC;
   @AutoRegister("brewing_stand_processor")
   public static StructureProcessorType<BrewingStandProcessor> BREWING_STAND_PROCESSOR = () -> BrewingStandProcessor.CODEC;
   @AutoRegister("potted_mushroom_processor")
   public static StructureProcessorType<PottedMushroomProcessor> POTTED_MUSHROOM_PROCESSOR = () -> PottedMushroomProcessor.CODEC;
}
