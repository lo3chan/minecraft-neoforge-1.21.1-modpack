package com.mcwwindows.kikoz;

import com.mcwwindows.kikoz.init.BlockInit;
import com.mcwwindows.kikoz.init.ItemInit;
import com.mcwwindows.kikoz.init.SoundsInit;
import com.mcwwindows.kikoz.init.TabInit;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod("mcwwindows")
public class MacawsWindows {
   public static final String MOD_ID = "mcwwindows";

   public MacawsWindows(IEventBus bus) {
      ItemInit.ITEMS.register(bus);
      BlockInit.BLOCKS.register(bus);
      SoundsInit.SOUNDS.register(bus);
      TabInit.CREATIVE_TABS.register(bus);
   }
}
