package com.yungnickyoung.minecraft.bettermineshafts.mixin;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.yungnickyoung.minecraft.bettermineshafts.BetterMineshaftsCommon;
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
public class LocateVanillaMineshaftCommandMixin {
   @Unique
   private static final SimpleCommandExceptionType VANILLA_MINESHAFT_EXCEPTION = new SimpleCommandExceptionType(
      Component.translatable("Use /locate structure #bettermineshafts:better_mineshafts instead!")
   );

   @Inject(
      method = {"locateStructure"},
      at = {@At("HEAD")}
   )
   private static void overrideLocateVanillaPyramid(CommandSourceStack cmdSource, Result<Structure> result, CallbackInfoReturnable<Integer> ci) throws CommandSyntaxException {
      Optional<ResourceKey<Structure>> optional = result.unwrap().left();
      if (BetterMineshaftsCommon.CONFIG.disableVanillaMineshafts
         && optional.isPresent()
         && optional.get().location().equals(ResourceLocation.fromNamespaceAndPath("minecraft", "mineshaft"))) {
         throw VANILLA_MINESHAFT_EXCEPTION.create();
      }
   }
}
