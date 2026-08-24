package com.yungnickyoung.minecraft.betterfortresses.mixin;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.yungnickyoung.minecraft.betterfortresses.BetterFortressesCommon;
import java.util.Optional;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.ResourceOrTagKeyArgument.Result;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.commands.LocateCommand;
import net.minecraft.world.level.levelgen.structure.Structure;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({LocateCommand.class})
public class LocateVanillaFortressCommandMixin {
   @Unique
   private static final SimpleCommandExceptionType OLD_FORTRESS_EXCEPTION = new SimpleCommandExceptionType(
      Component.translatable("Use /locate structure betterfortresses:fortress instead!")
   );

   @Inject(
      method = {"locateStructure"},
      at = {@At("HEAD")}
   )
   private static void betterfortresses_overrideLocateVanillaFortress(
      CommandSourceStack cmdSource, Result<Structure> result, CallbackInfoReturnable<Integer> ci
   ) throws CommandSyntaxException {
      Optional<ResourceKey<Structure>> optional = result.unwrap().left();
      if (BetterFortressesCommon.CONFIG.general.disableVanillaFortresses
         && optional.isPresent()
         && optional.get().location().equals(ResourceLocation.withDefaultNamespace("fortress"))) {
         throw OLD_FORTRESS_EXCEPTION.create();
      }
   }
}
