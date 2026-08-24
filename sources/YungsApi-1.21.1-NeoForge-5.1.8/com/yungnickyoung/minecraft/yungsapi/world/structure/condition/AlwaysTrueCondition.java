package com.yungnickyoung.minecraft.yungsapi.world.structure.condition;

import com.mojang.serialization.MapCodec;
import com.yungnickyoung.minecraft.yungsapi.world.structure.context.StructureContext;

public class AlwaysTrueCondition extends StructureCondition {
   private static final AlwaysTrueCondition INSTANCE = new AlwaysTrueCondition();
   public static final MapCodec<AlwaysTrueCondition> CODEC = MapCodec.unit(() -> INSTANCE);

   @Override
   public StructureConditionType<?> type() {
      return StructureConditionType.ALWAYS_TRUE;
   }

   @Override
   public boolean passes(StructureContext ctx) {
      return true;
   }
}
