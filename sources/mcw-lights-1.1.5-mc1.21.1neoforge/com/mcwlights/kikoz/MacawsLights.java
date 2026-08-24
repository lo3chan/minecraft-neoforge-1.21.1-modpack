package com.mcwlights.kikoz;

import com.mcwlights.kikoz.init.BlockInit;
import com.mcwlights.kikoz.init.ItemInit;
import com.mcwlights.kikoz.init.SoundsInit;
import com.mcwlights.kikoz.init.TabInit;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod("mcwlights")
public class MacawsLights {
   public static final String MOD_ID = "mcwlights";

   public MacawsLights(IEventBus bus) {
      ItemInit.ITEMS.register(bus);
      BlockInit.BLOCKS.register(bus);
      SoundsInit.SOUNDS.register(bus);
      TabInit.CREATIVE_TABS.register(bus);
   }
}
