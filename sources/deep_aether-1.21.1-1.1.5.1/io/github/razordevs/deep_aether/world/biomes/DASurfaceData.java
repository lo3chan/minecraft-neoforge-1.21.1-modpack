package io.github.razordevs.deep_aether.world.biomes;

import com.aetherteam.aether.block.AetherBlockStateProperties;
import com.aetherteam.aether.data.resources.AetherFeatureStates;
import io.github.razordevs.deep_aether.init.DABlocks;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Noises;
import net.minecraft.world.level.levelgen.SurfaceRules;
import net.minecraft.world.level.levelgen.SurfaceRules.RuleSource;
import net.minecraft.world.level.levelgen.placement.CaveSurface;

public class DASurfaceData {
   public static RuleSource makeRules() {
      return SurfaceRules.sequence(
         new RuleSource[]{
            SurfaceRules.ifTrue(
               SurfaceRules.isBiome(new ResourceKey[]{DABiomes.GOLDEN_HEIGHTS}),
               SurfaceRules.ifTrue(
                  SurfaceRules.ON_FLOOR, SurfaceRules.ifTrue(SurfaceRules.noiseCondition(Noises.SWAMP, 0.2), SurfaceRules.state(AetherFeatureStates.QUICKSOIL))
               )
            ),
            SurfaceRules.ifTrue(
               SurfaceRules.isBiome(new ResourceKey[]{DABiomes.GOLDEN_HEIGHTS}),
               SurfaceRules.ifTrue(
                  SurfaceRules.ON_FLOOR,
                  SurfaceRules.state(
                     (BlockState)((Block)DABlocks.GOLDEN_GRASS_BLOCK.get()).defaultBlockState().setValue(AetherBlockStateProperties.DOUBLE_DROPS, true)
                  )
               )
            ),
            SurfaceRules.ifTrue(
               SurfaceRules.isBiome(new ResourceKey[]{DABiomes.GOLDEN_GROVE}),
               SurfaceRules.ifTrue(
                  SurfaceRules.ON_FLOOR,
                  SurfaceRules.state(
                     (BlockState)((Block)DABlocks.GOLDEN_GRASS_BLOCK.get()).defaultBlockState().setValue(AetherBlockStateProperties.DOUBLE_DROPS, true)
                  )
               )
            ),
            SurfaceRules.ifTrue(
               SurfaceRules.isBiome(new ResourceKey[]{DABiomes.YAGROOT_SWAMP}),
               SurfaceRules.ifTrue(
                  SurfaceRules.ON_FLOOR,
                  SurfaceRules.ifTrue(
                     SurfaceRules.noiseCondition(Noises.SWAMP, 0.7), SurfaceRules.state(((Block)DABlocks.VIRULENT_QUICKSAND.get()).defaultBlockState())
                  )
               )
            ),
            SurfaceRules.ifTrue(
               SurfaceRules.isBiome(new ResourceKey[]{DABiomes.YAGROOT_SWAMP}),
               SurfaceRules.ifTrue(
                  SurfaceRules.ON_FLOOR,
                  SurfaceRules.state((BlockState)((Block)DABlocks.AETHER_MUD.get()).defaultBlockState().setValue(AetherBlockStateProperties.DOUBLE_DROPS, true))
               )
            ),
            SurfaceRules.ifTrue(
               SurfaceRules.isBiome(new ResourceKey[]{DABiomes.YAGROOT_SWAMP}),
               SurfaceRules.ifTrue(
                  SurfaceRules.stoneDepthCheck(0, true, 0, CaveSurface.FLOOR),
                  SurfaceRules.state((BlockState)((Block)DABlocks.AETHER_MUD.get()).defaultBlockState().setValue(AetherBlockStateProperties.DOUBLE_DROPS, true))
               )
            )
         }
      );
   }
}
