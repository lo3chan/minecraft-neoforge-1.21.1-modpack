package net.mehvahdjukaar.moonlight.core.mixins;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.mehvahdjukaar.moonlight.api.MoonlightRegistry;
import net.mehvahdjukaar.moonlight.core.Moonlight;
import net.minecraft.commands.arguments.blocks.BlockStateParser;
import net.minecraft.commands.arguments.blocks.BlockStateParser.BlockResult;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.JigsawReplacementProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin({JigsawReplacementProcessor.class})
public abstract class JigsawReplacementProcessorMixin {
   @ModifyReturnValue(
      method = {"processBlock"},
      at = {@At("RETURN")}
   )
   private StructureBlockInfo ml$processSpawnBoxes(
      @Nullable StructureBlockInfo original,
      LevelReader level,
      BlockPos offset,
      BlockPos pos,
      StructureBlockInfo blockInfo,
      StructureBlockInfo relativeBlockInfo,
      StructurePlaceSettings settings
   ) {
      if (original == null || !original.state().is(MoonlightRegistry.SPAWN_BOX_BLOCK.get())) {
         return original;
      } else if (original.nbt() == null) {
         Moonlight.LOGGER.warn("Spawn Box block at {} is missing nbt, will not replace", offset);
         return original;
      } else {
         String string = original.nbt().getString("final_state");

         BlockState blockState2;
         try {
            BlockResult blockResult = BlockStateParser.parseForBlock(level.holderLookup(Registries.BLOCK), string, true);
            blockState2 = blockResult.blockState();
         } catch (CommandSyntaxException var111) {
            Moonlight.LOGGER.error("Failed to parse spawn box replacement state '{}' at {}: {}", string, offset, var111.getMessage());
            return null;
         }

         return blockState2.is(Blocks.STRUCTURE_VOID) ? null : new StructureBlockInfo(original.pos(), blockState2, null);
      }
   }
}
