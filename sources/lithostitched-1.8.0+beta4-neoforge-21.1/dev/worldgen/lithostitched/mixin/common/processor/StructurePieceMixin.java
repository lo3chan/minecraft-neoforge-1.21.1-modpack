package dev.worldgen.lithostitched.mixin.common.processor;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import dev.worldgen.lithostitched.Lithostitched;
import dev.worldgen.lithostitched.api.worldgen.processor.LithostitchedProcessorLists;
import dev.worldgen.lithostitched.duck.ReferencePosDuck;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin({StructurePiece.class})
public class StructurePieceMixin implements ReferencePosDuck {
   @Shadow
   protected BoundingBox boundingBox;
   @Unique
   private BlockPos lithostitched$referencePos;

   @WrapOperation(
      method = {"placeBlock", "fillColumnDown"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/level/WorldGenLevel;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z"
      )}
   )
   private boolean applyProcessor(
      WorldGenLevel level,
      BlockPos pos,
      BlockState state,
      int flags,
      Operation<Boolean> operation,
      @Local(ordinal = 0) int x,
      @Local(ordinal = 1) int y,
      @Local(ordinal = 2) int z
   ) {
      ResourceKey<StructureProcessorList> processorKey = LithostitchedProcessorLists.pick((StructurePiece)this);
      if (processorKey == null) {
         return (Boolean)operation.call(new Object[]{level, pos, state, flags});
      } else {
         Optional<StructureProcessorList> processorList = Lithostitched.registry(level.registryAccess(), Registries.PROCESSOR_LIST).getOptional(processorKey);
         if (!processorList.isEmpty() && !processorList.get().list().isEmpty()) {
            StructureBlockInfo processedInfo = new StructureBlockInfo(pos, state, null);
            BlockPos piecePos = new BlockPos(this.boundingBox.minX(), this.boundingBox.minY(), this.boundingBox.minZ());

            for (StructureProcessor processor : processorList.get().list()) {
               BlockPos referencePos = this.lithostitched$referencePos == null ? piecePos : this.lithostitched$referencePos;
               processedInfo = processor.processBlock(
                  level,
                  piecePos,
                  referencePos,
                  new StructureBlockInfo(new BlockPos(x, y, z), processedInfo.state(), processedInfo.nbt()),
                  processedInfo,
                  new StructurePlaceSettings()
               );
               if (processedInfo == null) {
                  return false;
               }
            }

            return (Boolean)operation.call(new Object[]{level, pos, processedInfo.state(), flags});
         } else {
            return (Boolean)operation.call(new Object[]{level, pos, state, flags});
         }
      }
   }

   @Override
   public void setReferencePos(BlockPos referencePos) {
      this.lithostitched$referencePos = referencePos;
   }
}
