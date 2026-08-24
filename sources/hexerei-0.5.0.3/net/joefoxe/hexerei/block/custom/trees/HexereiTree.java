package net.joefoxe.hexerei.block.custom.trees;

import java.util.Optional;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.grower.TreeGrower;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;

public class HexereiTree {
   public static TreeGrower getGrower(String name, ResourceKey<ConfiguredFeature<?, ?>> configConfiguredFeature) {
      return new TreeGrower(name, Optional.empty(), Optional.of(configConfiguredFeature), Optional.empty());
   }
}
