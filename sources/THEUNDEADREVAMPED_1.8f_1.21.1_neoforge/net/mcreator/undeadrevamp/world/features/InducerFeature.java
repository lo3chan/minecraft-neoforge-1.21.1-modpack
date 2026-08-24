package net.mcreator.undeadrevamp.world.features;

import net.mcreator.undeadrevamp.procedures.InducerAdditionalGenerationConditionProcedure;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.OreFeature;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;

public class InducerFeature extends OreFeature {
   public InducerFeature() {
      super(OreConfiguration.CODEC);
   }

   public boolean place(FeaturePlaceContext<OreConfiguration> context) {
      WorldGenLevel world = context.level();
      int x = context.origin().getX();
      int y = context.origin().getY();
      int z = context.origin().getZ();
      return !InducerAdditionalGenerationConditionProcedure.execute() ? false : super.place(context);
   }
}
