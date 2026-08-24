package dev.worldgen.lithostitched.mixin.common.template;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import dev.worldgen.lithostitched.Lithostitched;
import dev.worldgen.lithostitched.worldgen.modifier.template.TemplateLists;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.StructurePieceAccessor;
import net.minecraft.world.level.levelgen.structure.Structure.GenerationContext;
import net.minecraft.world.level.levelgen.structure.structures.ShipwreckStructure;
import net.minecraft.world.level.levelgen.structure.structures.ShipwreckPieces.ShipwreckPiece;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin({ShipwreckStructure.class})
public class ShipwreckStructureMixin {
   @WrapOperation(
      method = {"generatePieces"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/level/levelgen/structure/structures/ShipwreckPieces;addRandomPiece(Lnet/minecraft/world/level/levelgen/structure/templatesystem/StructureTemplateManager;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/Rotation;Lnet/minecraft/world/level/levelgen/structure/StructurePieceAccessor;Lnet/minecraft/util/RandomSource;Z)Lnet/minecraft/world/level/levelgen/structure/structures/ShipwreckPieces$ShipwreckPiece;"
      )}
   )
   private ShipwreckPiece useTemplateList(
      StructureTemplateManager templateManager,
      BlockPos pos,
      Rotation rotation,
      StructurePieceAccessor pieceAccessor,
      RandomSource random,
      boolean beached,
      Operation<ShipwreckPiece> operation,
      @Local(ordinal = 0,argsOnly = true) GenerationContext context
   ) {
      if (!Lithostitched.breaksSeedParity()) {
         return (ShipwreckPiece)operation.call(new Object[]{templateManager, pos, rotation, pieceAccessor, random, beached});
      } else {
         ResourceLocation id = TemplateLists.getRandom(
            context.registryAccess(), beached ? TemplateLists.SHIPWRECK_BEACHED : TemplateLists.SHIPWRECK_OCEAN, random
         );
         ShipwreckPiece piece = new ShipwreckPiece(templateManager, id, pos, rotation, beached);
         pieceAccessor.addPiece(piece);
         return piece;
      }
   }
}
