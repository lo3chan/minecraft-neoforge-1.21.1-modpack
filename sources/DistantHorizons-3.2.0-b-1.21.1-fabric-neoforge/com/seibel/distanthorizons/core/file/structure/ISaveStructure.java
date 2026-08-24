package com.seibel.distanthorizons.core.file.structure;

import com.seibel.distanthorizons.core.wrapperInterfaces.world.ILevelWrapper;
import java.io.File;

public interface ISaveStructure extends AutoCloseable {
   String DATABASE_NAME = "DistantHorizons.sqlite";

   File getSaveFolder(ILevelWrapper iLevelWrapper);

   File getPre23SaveFolder(ILevelWrapper iLevelWrapper);
}
