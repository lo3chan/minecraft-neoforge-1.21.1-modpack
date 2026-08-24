package com.seibel.distanthorizons.core.wrapperInterfaces.minecraft;

import com.seibel.distanthorizons.core.wrapperInterfaces.world.IServerLevelWrapper;
import com.seibel.distanthorizons.coreapi.interfaces.dependencyInjection.IBindable;
import java.io.File;
import org.jetbrains.annotations.Nullable;

public interface IMinecraftSharedWrapper extends IBindable {
   boolean isDedicatedServer();

   File getInstallationDirectory();

   int getPlayerCount();

   @Nullable
   IServerLevelWrapper getLevelWrapper(String string);
}
