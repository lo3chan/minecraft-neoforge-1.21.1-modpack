package com.seibel.distanthorizons.api.interfaces.override.levelHandling;

import com.seibel.distanthorizons.api.interfaces.override.IDhApiOverrideable;
import com.seibel.distanthorizons.api.interfaces.world.IDhApiLevelWrapper;
import java.io.File;

public interface IDhApiSaveStructure extends IDhApiOverrideable {
   File overrideFilePath(File file, IDhApiLevelWrapper iDhApiLevelWrapper);
}
