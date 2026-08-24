package dev.latvian.mods.kubejs.core.mixin;

import dev.latvian.mods.kubejs.core.ReloadableServerResourcesKJS;
import dev.latvian.mods.kubejs.server.ServerScriptManager;
import dev.latvian.mods.kubejs.util.RegistryAccessContainer;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.minecraft.commands.Commands.CommandSelection;
import net.minecraft.core.LayeredRegistryAccess;
import net.minecraft.core.RegistryAccess.Frozen;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.RegistryLayer;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.tags.TagManager;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.crafting.RecipeManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({ReloadableServerResources.class})
public abstract class ReloadableServerResourcesMixin implements ReloadableServerResourcesKJS {
   @Unique
   private ServerScriptManager kjs$serverScriptManager;
   @Shadow
   @Final
   private TagManager tagManager;
   @Shadow
   @Final
   private RecipeManager recipes;

   @Inject(
      method = {"<init>"},
      at = {@At("RETURN")}
   )
   private void init(Frozen registryAccess, FeatureFlagSet featureFlagSet, CommandSelection commandSelection, int functionCompilationLevel, CallbackInfo ci) {
      this.kjs$serverScriptManager = ServerScriptManager.release();
      this.tagManager.kjs$setResources(this);
      this.recipes.kjs$setResources(this);
   }

   @Inject(
      method = {"loadResources"},
      at = {@At("HEAD")}
   )
   private static void injectKubeJSPacks(
      ResourceManager resourceManager,
      LayeredRegistryAccess<RegistryLayer> registries,
      FeatureFlagSet enabledFeatures,
      CommandSelection commandSelection,
      int functionCompilationLevel,
      Executor backgroundExecutor,
      Executor gameExecutor,
      CallbackInfoReturnable<CompletableFuture<ReloadableServerResources>> cir
   ) {
      RegistryAccessContainer.current = new RegistryAccessContainer(registries.compositeAccess());
      if (gameExecutor instanceof MinecraftServer s && s.getServerResources() != null && s.getServerResources().managers().kjs$getServerScriptManager() != null
         )
       {
         s.getServerResources().managers().kjs$getServerScriptManager().reloadAndCapture();
      }
   }

   @Override
   public ServerScriptManager kjs$getServerScriptManager() {
      return this.kjs$serverScriptManager;
   }

   @Override
   public TagManager kjs$getTagManager() {
      return this.tagManager;
   }
}
