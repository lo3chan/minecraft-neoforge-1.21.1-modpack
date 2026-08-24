package com.finndog.moogs_structures.mixins.structures;

import com.finndog.moogs_structures.config.MslConfig;
import com.finndog.moogs_structures.config.ReplaceVanillaManager;
import com.finndog.moogs_structures.modinit.MoogsStructuresTags;
import com.google.common.base.Stopwatch;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.datafixers.util.Pair;
import net.minecraft.Util;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.ResourceOrTagKeyArgument.Result;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.commands.LocateCommand;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.levelgen.structure.Structure;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin({LocateCommand.class})
public class LocateCommandMixin {
   @Final
   @Shadow
   private static DynamicCommandExceptionType ERROR_STRUCTURE_NOT_FOUND;

   @Inject(
      method = {"locateStructure(Lnet/minecraft/commands/CommandSourceStack;Lnet/minecraft/commands/arguments/ResourceOrTagKeyArgument$Result;)I"},
      at = {@At("HEAD")},
      cancellable = true,
      require = 0
   )
   private static void moogs_structures_interceptReplacedLocate(CommandSourceStack source, Result<Structure> result, CallbackInfoReturnable<Integer> cir) {
      result.unwrap()
         .ifLeft(
            key -> {
               ResourceLocation id = key.location();
               if (MslConfig.get().isStructureDisabled(id)) {
                  source.sendSuccess(() -> Component.literal(id + " is disabled in the Moogs Structures config (config/moogs_structures.json)."), false);
                  cir.setReturnValue(1);
               } else {
                  ReplaceVanillaManager.getActiveReplacement(id)
                     .ifPresent(
                        replacement -> {
                           ResourceLocation replacementId = replacement.replacementStructure();
                           String replacementText = replacementId != null ? replacementId.toString() : "a Moogs structure";
                           source.sendSuccess(
                              () -> Component.literal(
                                 id
                                    + " has been replaced with "
                                    + replacementText
                                    + ". You can change this in the Moogs Structures config (config/moogs_structures.json)."
                              ),
                              false
                           );
                           cir.setReturnValue(1);
                        }
                     );
               }
            }
         );
   }

   @Inject(
      method = {"locateStructure(Lnet/minecraft/commands/CommandSourceStack;Lnet/minecraft/commands/arguments/ResourceOrTagKeyArgument$Result;)I"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/level/chunk/ChunkGenerator;findNearestMapStructure(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/core/HolderSet;Lnet/minecraft/core/BlockPos;IZ)Lcom/mojang/datafixers/util/Pair;",
         ordinal = 0
      )},
      locals = LocalCapture.CAPTURE_FAILSOFT,
      cancellable = true,
      require = 0
   )
   private static void moogs_structures_increaseLocateRadius(
      CommandSourceStack commandSourceStack,
      Result<Structure> result,
      CallbackInfoReturnable<Integer> cir,
      Registry<Structure> registry,
      HolderSet<Structure> holderSet,
      BlockPos blockPos,
      ServerLevel serverLevel
   ) throws CommandSyntaxException {
      if (holderSet.stream().anyMatch(configuredStructureFeatureHolder -> configuredStructureFeatureHolder.is(MoogsStructuresTags.LARGER_LOCATE_SEARCH))) {
         int increasedSearchRadius = 2000;
         Stopwatch stopwatch = Stopwatch.createStarted(Util.TICKER);
         Pair<BlockPos, Holder<Structure>> pair = serverLevel.getChunkSource()
            .getGenerator()
            .findNearestMapStructure(serverLevel, holderSet, blockPos, increasedSearchRadius, false);
         stopwatch.stop();
         if (pair == null) {
            throw ERROR_STRUCTURE_NOT_FOUND.create(result.asPrintable());
         }

         cir.setReturnValue(
            LocateCommand.showLocateResult(commandSourceStack, result, blockPos, pair, "commands.locate.structure.success", false, stopwatch.elapsed())
         );
      }
   }
}
