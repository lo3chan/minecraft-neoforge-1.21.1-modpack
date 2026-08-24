package net.mehvahdjukaar.moonlight.core.mixins;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder.Reference;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.server.commands.PlaceCommand;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({PlaceCommand.class})
public abstract class PlaceStructureCommandMixin {
   @Inject(
      method = {"placeStructure"},
      at = {@At("TAIL")}
   )
   private static void ml$addDebugRenderers(
      CommandSourceStack source, Reference<Structure> structure, BlockPos pos, CallbackInfoReturnable<Integer> cir, @Local StructureStart start
   ) {
      if (start.isValid()) {
         DebugPackets.sendStructurePacket(source.getLevel(), start);
      }
   }
}
