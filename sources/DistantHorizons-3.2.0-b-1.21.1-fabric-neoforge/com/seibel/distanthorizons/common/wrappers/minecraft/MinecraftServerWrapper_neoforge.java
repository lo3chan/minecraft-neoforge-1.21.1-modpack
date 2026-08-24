package com.seibel.distanthorizons.common.wrappers.minecraft;

import com.seibel.distanthorizons.common.wrappers.world.ServerLevelWrapper_neoforge;
import com.seibel.distanthorizons.core.wrapperInterfaces.world.IServerLevelWrapper;
import java.io.File;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class MinecraftServerWrapper_neoforge extends AbstractMinecraftSharedWrapper_neoforge {
   public static final MinecraftServerWrapper_neoforge INSTANCE = new MinecraftServerWrapper_neoforge();
   @Nullable
   public DedicatedServer dedicatedServer = null;

   private MinecraftServerWrapper_neoforge() {
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
         return this.dedicatedServer.getServerDirectory().toFile();
      }
   }

   @Override
   public int getPlayerCount() {
      if (this.dedicatedServer == null) {
         throw new IllegalStateException("Trying to get player count before dedicated server completed initialization!");
      } else {
         return this.dedicatedServer.getPlayerCount();
      }
   }

   @Nullable
   @Override
   public IServerLevelWrapper getLevelWrapper(String dimensionResourceLocation) {
      if (this.dedicatedServer == null) {
         throw new IllegalStateException("Trying to get the server mcLevel before dedicated server completed initialization!");
      } else {
         ResourceKey<Level> dimensionKey = this.deserializeDimensionResourceKey(dimensionResourceLocation);
         ServerLevel mcLevel = this.dedicatedServer.getLevel(dimensionKey);
         return ServerLevelWrapper_neoforge.getWrapper(mcLevel);
      }
   }
}
