package net.mcreator.undeadrevamp.world.features;

import net.mcreator.undeadrevamp.procedures.SpawnpillarAdditionalGenerationConditionProcedure;
import net.mcreator.undeadrevamp.world.features.configurations.StructureFeatureConfiguration;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;

public class SpawnpillarFeature extends StructureFeature {
   public SpawnpillarFeature() {
      super(StructureFeatureConfiguration.CODEC);
   }

   @Override
   public boolean place(FeaturePlaceContext<StructureFeatureConfiguration> context) {
      WorldGenLevel world = context.level();
      int x = context.origin().getX();
      int y = context.origin().getY();
      int z = context.origin().getZ();
      return !SpawnpillarAdditionalGenerationConditionProcedure.execute() ? false : super.place(context);
   }
}
