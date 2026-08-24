package com.aetherteam.cumulus.mixin;

import com.aetherteam.cumulus.mixin.mixins.common.accessor.MinecraftServerAccessor;
import java.nio.file.Path;
import net.minecraft.client.Minecraft;

public class MixinHooks {
   public static boolean canUnlockLevel(Path basePath) {
      return Minecraft.getInstance().getSingleplayerServer() != null
         ? basePath.getFileName()
            .toString()
            .equals(((MinecraftServerAccessor)Minecraft.getInstance().getSingleplayerServer()).cumulus$getStorageSource().getLevelId())
         : false;
   }
}
