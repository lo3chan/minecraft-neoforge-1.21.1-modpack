package net.mehvahdjukaar.moonlight.core.mixins;

import java.util.List;
import net.mehvahdjukaar.moonlight.core.worldgen.SpawnBoxStructurePiece;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.structure.PoolElementStructurePiece;
import net.minecraft.world.level.levelgen.structure.pools.JigsawPlacement.Placer;
import net.minecraft.world.level.levelgen.structure.pools.alias.PoolAliasLookup;
import net.minecraft.world.level.levelgen.structure.templatesystem.LiquidSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.apache.commons.lang3.mutable.MutableObject;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({Placer.class})
public abstract class JigsawPlacementMixin {
   @Shadow
   @Final
   private StructureTemplateManager structureTemplateManager;
   @Shadow
   @Final
   private List<? super PoolElementStructurePiece> pieces;

   @Inject(
      method = {"tryPlacingChildren"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/level/levelgen/structure/pools/StructurePoolElement;getShuffledJigsawBlocks(Lnet/minecraft/world/level/levelgen/structure/templatesystem/StructureTemplateManager;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/Rotation;Lnet/minecraft/util/RandomSource;)Ljava/util/List;",
         ordinal = 0,
         shift = Shift.BEFORE
      )}
   )
   public void ml$AddSpawnBoxPieces(
      PoolElementStructurePiece piece,
      MutableObject<VoxelShape> free,
      int depth,
      boolean useExpansionHack,
      LevelHeightAccessor level,
      RandomState random,
      PoolAliasLookup poolAliasLookup,
      LiquidSettings liquidSettings,
      CallbackInfo ci
   ) {
      List<? extends PoolElementStructurePiece> extraPieces = SpawnBoxStructurePiece.getSpawnBoxPieces(piece, this.structureTemplateManager, liquidSettings);
      this.pieces.addAll(extraPieces);
   }
}
