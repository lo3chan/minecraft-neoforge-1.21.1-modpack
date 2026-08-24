package com.seibel.distanthorizons.common.wrappers.minecraft;

import com.seibel.distanthorizons.common.wrappers.world.ServerLevelWrapper_fabric;
import com.seibel.distanthorizons.core.wrapperInterfaces.world.IServerLevelWrapper;
import java.io.File;
import net.minecraft.class_1937;
import net.minecraft.class_3176;
import net.minecraft.class_3218;
import net.minecraft.class_5321;
import org.jetbrains.annotations.Nullable;

public class MinecraftServerWrapper_fabric extends AbstractMinecraftSharedWrapper_fabric {
   public static final MinecraftServerWrapper_fabric INSTANCE = new MinecraftServerWrapper_fabric();
   @Nullable
   public class_3176 dedicatedServer = null;

   private MinecraftServerWrapper_fabric() {
   }

   @Override
   public boolean isDedicatedServer() {
      return true;
   }

   @Override
   public File getInstallationDirectory() {
      if (this.dedicatedServer == null) {
         throw new IllegalStateException("Trying to get Installation Direction before dedicated server completed initialization!");
      } else {
         return this.dedicatedServer.method_3831().toFile();
      }
   }

   @Override
   public int getPlayerCount() {
      if (this.dedicatedServer == null) {
         throw new IllegalStateException("Trying to get player count before dedicated server completed initialization!");
      } else {
         return this.dedicatedServer.method_3788();
      }
   }

   @Nullable
   @Override
   public IServerLevelWrapper getLevelWrapper(String dimensionResourceLocation) {
      if (this.dedicatedServer == null) {
         throw new IllegalStateException("Trying to get the server mcLevel before dedicated server completed initialization!");
      } else {
         class_5321<class_1937> dimensionKey = this.deserializeDimensionResourceKey(dimensionResourceLocation);
         class_3218 mcLevel = this.dedicatedServer.method_3847(dimensionKey);
         return ServerLevelWrapper_fabric.getWrapper(mcLevel);
      }
   }
}
