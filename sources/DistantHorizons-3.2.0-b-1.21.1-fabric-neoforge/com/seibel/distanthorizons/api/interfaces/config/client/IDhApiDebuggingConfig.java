package com.seibel.distanthorizons.api.interfaces.config.client;

import com.seibel.distanthorizons.api.enums.rendering.EDhApiDebugRendering;
import com.seibel.distanthorizons.api.interfaces.config.IDhApiConfigGroup;
import com.seibel.distanthorizons.api.interfaces.config.IDhApiConfigValue;

public interface IDhApiDebuggingConfig extends IDhApiConfigGroup {
   IDhApiConfigValue<EDhApiDebugRendering> debugRendering();

   IDhApiConfigValue<Boolean> debugKeybindings();

   IDhApiConfigValue<Boolean> renderWireframe();

   IDhApiConfigValue<Boolean> lodOnlyMode();

   IDhApiConfigValue<Boolean> debugWireframeRendering();
}
