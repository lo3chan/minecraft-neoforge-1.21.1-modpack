package io.wispforest.owo.mixin;

import io.wispforest.owo.util.Maldenhagen;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.BulkSectionAccess;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.levelgen.feature.OreFeature;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration.TargetBlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin({OreFeature.class})
public class Copenhagen {
   private final ThreadLocal<Map<BlockPos, BlockState>> OWO$COPING = ThreadLocal.withInitial(HashMap::new);

   @Inject(
      method = {"doPlace(Lnet/minecraft/world/level/WorldGenLevel;Lnet/minecraft/util/RandomSource;Lnet/minecraft/world/level/levelgen/feature/configurations/OreConfiguration;DDDDDDIIIII)Z"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/level/chunk/LevelChunkSection;setBlockState(IIILnet/minecraft/world/level/block/state/BlockState;Z)Lnet/minecraft/world/level/block/state/BlockState;"
      )},
      locals = LocalCapture.CAPTURE_FAILHARD
   )
   private void malding(
      WorldGenLevel world,
      RandomSource random,
      OreConfiguration config,
      double startX,
      double endX,
      double startZ,
      double endZ,
      double startY,
      double endY,
      int p_x,
      int p_y,
      int p_z,
      int p_horizontalSize,
      int p_verticalSize,
      CallbackInfoReturnable<Boolean> cir,
      int i,
      BitSet bitSet,
      MutableBlockPos mutable,
      int j,
      double[] ds,
      BulkSectionAccess chunkSectionCache,
      int m,
      double d,
      double e,
      double g,
      double h,
      int n,
      int o,
      int p,
      int q,
      int r,
      int s,
      int t,
      double u,
      int v,
      double w,
      int aa,
      double x,
      int ab,
      LevelChunkSection chunkSection,
      int ad,
      int ae,
      int af,
      BlockState blockState,
      Iterator<TargetBlockState> var57,
      TargetBlockState target
   ) {
      if (Maldenhagen.isOnCopium(target.state.getBlock())) {
         this.OWO$COPING.get().put(new BlockPos(t, v, aa), target.state);
      }
   }

   @Inject(
      method = {"doPlace(Lnet/minecraft/world/level/WorldGenLevel;Lnet/minecraft/util/RandomSource;Lnet/minecraft/world/level/levelgen/feature/configurations/OreConfiguration;DDDDDDIIIII)Z"},
      at = {@At("TAIL")}
   )
   private void coping(
      WorldGenLevel world,
      RandomSource random,
      OreConfiguration config,
      double startX,
      double endX,
      double startZ,
      double endZ,
      double startY,
      double endY,
      int x,
      int y,
      int z,
      int horizontalSize,
      int verticalSize,
      CallbackInfoReturnable<Boolean> cir
   ) {
      this.OWO$COPING.get().forEach((blockPos, state) -> world.setBlock(blockPos, state, 3));
      this.OWO$COPING.get().clear();
   }
}
