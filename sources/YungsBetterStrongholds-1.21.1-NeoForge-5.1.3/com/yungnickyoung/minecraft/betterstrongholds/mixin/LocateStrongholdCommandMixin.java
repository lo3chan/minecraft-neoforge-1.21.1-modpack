package com.yungnickyoung.minecraft.betterstrongholds.mixin;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Optional;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.ResourceOrTagKeyArgument.Result;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.commands.LocateCommand;
import net.minecraft.world.level.levelgen.structure.Structure;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({LocateCommand.class})
public class LocateStrongholdCommandMixin {
   private static final SimpleCommandExceptionType OLD_STRONGHOLD_EXCEPTION = new SimpleCommandExceptionType(
      Component.translatable("Use /locate structure betterstrongholds:stronghold instead!")
   );

   @Inject(
      method = {"locateStructure"},
      at = {@At("HEAD")}
   )
   private static void overrideLocateVanillaStronghold(CommandSourceStack cmdSource, Result<Structure> result, CallbackInfoReturnable<Integer> ci) throws CommandSyntaxException {
      Optional<ResourceKey<Structure>> optional = result.unwrap().left();
      if (optional.isPresent() && optional.get().location().equals(ResourceLocation.withDefaultNamespace("stronghold"))) {
         throw OLD_STRONGHOLD_EXCEPTION.create();
      }
   }
}
