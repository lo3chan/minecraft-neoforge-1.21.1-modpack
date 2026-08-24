package dev.worldgen.lithostitched.mixin.common.processor;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import dev.worldgen.lithostitched.Lithostitched;
import dev.worldgen.lithostitched.impl.worldgen.processor.UnboundReferenceProcessor;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.structures.OceanRuinPieces.OceanRuinPiece;
import net.minecraft.world.level.levelgen.structure.structures.OceanRuinStructure.Type;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin({OceanRuinPiece.class})
public class OceanRuinPieceMixin {
   @ModifyReturnValue(
      method = {"makeSettings"},
      at = {@At("RETURN")}
   )
   private static StructurePlaceSettings addShipwreckProcessor(StructurePlaceSettings settings, Rotation rotation, float f, Type type) {
      return Lithostitched.breaksSeedParity() ? settings.addProcessor(UnboundReferenceProcessor.of("ocean_ruin_" + type.getName())) : settings;
   }
}
