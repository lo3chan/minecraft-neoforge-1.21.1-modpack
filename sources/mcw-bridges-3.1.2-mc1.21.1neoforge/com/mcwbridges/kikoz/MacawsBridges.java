package com.mcwbridges.kikoz;

import com.mcwbridges.kikoz.init.BlockInit;
import com.mcwbridges.kikoz.init.ItemInit;
import com.mcwbridges.kikoz.init.TabInit;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod("mcwbridges")
public class MacawsBridges {
   public static final String MOD_ID = "mcwbridges";

   public MacawsBridges(IEventBus bus) {
      ItemInit.ITEMS.register(bus);
      BlockInit.BLOCKS.register(bus);
      TabInit.CREATIVE_TABS.register(bus);
   }
}
