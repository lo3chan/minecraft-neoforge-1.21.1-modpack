package net.mehvahdjukaar.amendments.mixins;

import net.mehvahdjukaar.amendments.common.block.BoilingWaterCauldronBlock;
import net.minecraft.core.cauldron.CauldronInteraction.InteractionMap;
import net.minecraft.world.level.biome.Biome.Precipitation;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin({Blocks.class})
public class BlocksMixin {
   @Redirect(
      method = {"<clinit>"},
      at = @At(
         value = "NEW",
         target = "(Lnet/minecraft/world/level/biome/Biome$Precipitation;Lnet/minecraft/core/cauldron/CauldronInteraction$InteractionMap;Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;)Lnet/minecraft/world/level/block/LayeredCauldronBlock;",
         ordinal = 0
      ),
      slice = @Slice(
         from = @At(
            value = "CONSTANT",
            args = {"stringValue=water_cauldron"}
         )
      )
   )
   private static LayeredCauldronBlock amendments$overrideCauldron(Precipitation precipitation, InteractionMap interactions, Properties properties) {
      return new BoilingWaterCauldronBlock(precipitation, interactions, properties);
   }
}
