package dev.shadowsoffire.fastsuite.mixin;

import dev.shadowsoffire.fastsuite.AuxRecipeManager;
import net.minecraft.commands.Commands.CommandSelection;
import net.minecraft.core.RegistryAccess.Frozen;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.crafting.RecipeManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(
   value = {ReloadableServerResources.class},
   remap = false,
   priority = 500
)
public class ServerResourcesMixin {
   @Shadow
   @Mutable
   private RecipeManager recipes;

   @Inject(
      method = {"<init>"},
      at = {@At("TAIL")}
   )
   private void onInit(Frozen registryAccess, FeatureFlagSet enabledFeatures, CommandSelection commandSelection, int functionCompilationLevel, CallbackInfo ci) {
      this.recipes = new AuxRecipeManager(registryAccess);
   }
}
