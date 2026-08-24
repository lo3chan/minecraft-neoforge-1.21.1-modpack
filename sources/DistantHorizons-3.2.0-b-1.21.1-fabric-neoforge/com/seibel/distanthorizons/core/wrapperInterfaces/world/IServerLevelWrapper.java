package com.seibel.distanthorizons.core.wrapperInterfaces.world;

import java.io.File;

public interface IServerLevelWrapper extends ILevelWrapper {
   File getMcSaveFolder();

   String getKeyedLevelDimensionName();
}
