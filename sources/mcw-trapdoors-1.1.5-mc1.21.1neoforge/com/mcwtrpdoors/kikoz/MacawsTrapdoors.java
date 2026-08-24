package com.mcwtrpdoors.kikoz;

import com.mcwtrpdoors.kikoz.init.BlockInit;
import com.mcwtrpdoors.kikoz.init.ItemInit;
import com.mcwtrpdoors.kikoz.init.TabInit;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod("mcwtrpdoors")
public class MacawsTrapdoors {
   public static final String MOD_ID = "mcwtrpdoors";

   public MacawsTrapdoors(IEventBus bus) {
      ItemInit.ITEMS.register(bus);
      BlockInit.BLOCKS.register(bus);
      TabInit.CREATIVE_TABS.register(bus);
   }
}
