package io.github.razordevs.aeroblender.aether;

import com.aetherteam.aether.block.AetherBlockStateProperties;
import com.aetherteam.aether.block.AetherBlocks;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.SurfaceRules;
import net.minecraft.world.level.levelgen.SurfaceRules.RuleSource;

public class AetherSurfaceRuleData {
   private static final RuleSource AETHER_GRASS_BLOCK = makeStateRule((Block)AetherBlocks.AETHER_GRASS_BLOCK.get());
   private static final RuleSource AETHER_DIRT = makeStateRule((Block)AetherBlocks.AETHER_DIRT.get());

   private static RuleSource makeStateRule(Block block) {
      return SurfaceRules.state((BlockState)block.defaultBlockState().setValue(AetherBlockStateProperties.DOUBLE_DROPS, true));
   }

   public static RuleSource aether() {
      RuleSource surface = SurfaceRules.sequence(new RuleSource[]{SurfaceRules.ifTrue(SurfaceRules.waterBlockCheck(-1, 0), AETHER_GRASS_BLOCK), AETHER_DIRT});
      return SurfaceRules.sequence(
         new RuleSource[]{SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR, surface), SurfaceRules.ifTrue(SurfaceRules.UNDER_FLOOR, AETHER_DIRT)}
      );
   }
}
