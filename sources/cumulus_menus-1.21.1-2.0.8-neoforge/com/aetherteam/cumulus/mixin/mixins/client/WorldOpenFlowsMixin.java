package com.aetherteam.cumulus.mixin.mixins.client;

import com.aetherteam.cumulus.client.WorldDisplayHelper;
import com.aetherteam.cumulus.mixin.mixins.common.accessor.MinecraftServerAccessor;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.serialization.Dynamic;
import java.io.IOException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.GenericMessageScreen;
import net.minecraft.client.gui.screens.worldselection.WorldOpenFlows;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.storage.LevelStorageSource.LevelStorageAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({WorldOpenFlows.class})
public class WorldOpenFlowsMixin {
   @Inject(
      method = {"openWorld"},
      at = {@At("RETURN")}
   )
   private void aetherFabric$onFailRun(String worldName, Runnable onFail, CallbackInfo ci, @Local LevelStorageAccess levelStorageAccess) {
      if (levelStorageAccess == null && WorldDisplayHelper.FAIL_RUN.equals(onFail)) {
         onFail.run();
      }
   }

   @Inject(
      method = {"openWorld(Ljava/lang/String;Ljava/lang/Runnable;)V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void openWorld(String worldName, Runnable onFail, CallbackInfo ci) {
      if (WorldDisplayHelper.isActive()
         && Minecraft.getInstance().hasSingleplayerServer()
         && ((MinecraftServerAccessor)Minecraft.getInstance().getSingleplayerServer()).cumulus$getStorageSource().getLevelId().equals(worldName)) {
         WorldDisplayHelper.enterLoadedLevel();
         ci.cancel();
      }
   }

   @ModifyVariable(
      method = {"openWorldCheckWorldStemCompatibility(Lnet/minecraft/world/level/storage/LevelStorageSource$LevelStorageAccess;Lnet/minecraft/server/WorldStem;Lnet/minecraft/server/packs/repository/PackRepository;Ljava/lang/Runnable;)V"},
      at = @At("STORE"),
      ordinal = 1
   )
   private boolean confirmExperimentalWarning(boolean confirmExperimentalWarning) {
      return WorldDisplayHelper.isActive() ? false : confirmExperimentalWarning;
   }

   @Inject(
      method = {"openWorldLoadLevelStem(Lnet/minecraft/world/level/storage/LevelStorageSource$LevelStorageAccess;Lcom/mojang/serialization/Dynamic;ZLjava/lang/Runnable;)V"},
      at = {@At("HEAD")}
   )
   private void closeActiveWorld(LevelStorageAccess levelStorage, Dynamic<?> levelData, boolean safeMode, Runnable onFail, CallbackInfo ci) throws IOException {
      if (WorldDisplayHelper.isActive() && !WorldDisplayHelper.sameSummaries(levelStorage.getSummary(levelStorage.getDataTag()))) {
         WorldDisplayHelper.stopLevel(new GenericMessageScreen(Component.translatable("menu.savingLevel")));
         WorldDisplayHelper.resetSummary();
      }
   }
}
