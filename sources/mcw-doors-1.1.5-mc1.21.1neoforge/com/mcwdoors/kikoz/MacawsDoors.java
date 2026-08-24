package com.mcwdoors.kikoz;

import com.mcwdoors.kikoz.init.BlockInit;
import com.mcwdoors.kikoz.init.ItemInit;
import com.mcwdoors.kikoz.init.SoundsInit;
import com.mcwdoors.kikoz.init.TabInit;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod("mcwdoors")
public class MacawsDoors {
   public static final String MOD_ID = "mcwdoors";

   public MacawsDoors(IEventBus bus) {
      ItemInit.ITEMS.register(bus);
      BlockInit.BLOCKS.register(bus);
      SoundsInit.SOUNDS.register(bus);
      TabInit.CREATIVE_TABS.register(bus);
   }
}
