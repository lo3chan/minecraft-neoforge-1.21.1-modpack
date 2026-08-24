package com.seibel.distanthorizons.api.interfaces.override.worldGenerator;

import com.seibel.distanthorizons.api.interfaces.world.IDhApiLevelWrapper;
import com.seibel.distanthorizons.api.objects.DhApiResult;

public interface IDhApiWorldGeneratorOverrideRegister {
   DhApiResult<Void> registerWorldGeneratorOverride(IDhApiLevelWrapper iDhApiLevelWrapper, IDhApiWorldGenerator iDhApiWorldGenerator);
}
