package dev.worldgen.lithostitched.mixin.common.template;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import dev.worldgen.lithostitched.Lithostitched;
import dev.worldgen.lithostitched.worldgen.modifier.template.TemplateLists;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.Structure.GenerationContext;
import net.minecraft.world.level.levelgen.structure.Structure.GenerationStub;
import net.minecraft.world.level.levelgen.structure.structures.NetherFossilStructure;
import net.minecraft.world.level.levelgen.structure.structures.NetherFossilPieces.NetherFossilPiece;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin({NetherFossilStructure.class})
public class NetherFossilStructureMixin {
   @WrapOperation(
      method = {"findGenerationPoint"},
      at = {@At(
         value = "INVOKE",
         target = "Ljava/util/Optional;of(Ljava/lang/Object;)Ljava/util/Optional;"
      )}
   )
   private Optional<GenerationStub> useTemplateList(
      Object stub, Operation<Optional<GenerationStub>> operation, GenerationContext context, @Local(ordinal = 0) BlockPos pos
   ) {
      return !Lithostitched.breaksSeedParity() ? (Optional)operation.call(new Object[]{stub}) : Optional.of(new GenerationStub(pos, builder -> {
         Rotation rotation = Rotation.getRandom(context.random());
         ResourceLocation template = TemplateLists.getRandom(context.registryAccess(), TemplateLists.NETHER_FOSSIL, context.random());
         builder.addPiece(new NetherFossilPiece(context.structureTemplateManager(), template, pos, rotation));
      }));
   }
}
