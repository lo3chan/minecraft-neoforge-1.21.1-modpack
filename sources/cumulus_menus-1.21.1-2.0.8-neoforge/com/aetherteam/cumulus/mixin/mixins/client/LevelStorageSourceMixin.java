package com.aetherteam.cumulus.mixin.mixins.client;

import com.aetherteam.cumulus.client.WorldDisplayHelper;
import com.aetherteam.cumulus.mixin.MixinHooks;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import java.nio.file.Path;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.LevelStorageSource.LevelDirectory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin({LevelStorageSource.class})
public class LevelStorageSourceMixin {
   @WrapOperation(
      method = {"lambda$loadLevelSummaries$3(Lnet/minecraft/world/level/storage/LevelStorageSource$LevelDirectory;)Lnet/minecraft/world/level/storage/LevelSummary;", "method_43418(Lnet/minecraft/world/level/storage/LevelStorageSource$LevelDirectory;)Lnet/minecraft/world/level/storage/LevelSummary;"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/util/DirectoryLock;isLocked(Ljava/nio/file/Path;)Z"
      )},
      require = 1,
      allow = 1
   )
   private boolean loadLevelSummaries(Path flag, Operation<Boolean> original, @Local(argsOnly = true) LevelDirectory levelDirectory) {
      return WorldDisplayHelper.isActive() && MixinHooks.canUnlockLevel(levelDirectory.path()) ? false : (Boolean)original.call(new Object[]{flag});
   }
}
