package com.seibel.distanthorizons.core.world;

import com.seibel.distanthorizons.core.level.IDhLevel;
import com.seibel.distanthorizons.core.wrapperInterfaces.world.ILevelWrapper;
import java.io.Closeable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface IDhWorld extends Closeable {
   @Nullable
   IDhLevel getOrLoadLevel(@NotNull ILevelWrapper iLevelWrapper);

   @Nullable
   IDhLevel getLevel(@NotNull ILevelWrapper iLevelWrapper);

   Iterable<? extends IDhLevel> getAllLoadedLevels();

   int getLoadedLevelCount();

   boolean unloadLevel(@NotNull ILevelWrapper iLevelWrapper);
}
