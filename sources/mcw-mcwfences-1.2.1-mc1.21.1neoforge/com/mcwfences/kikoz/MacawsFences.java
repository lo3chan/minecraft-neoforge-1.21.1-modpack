package com.mcwfences.kikoz;

import com.mcwfences.kikoz.init.BlockInit;
import com.mcwfences.kikoz.init.ItemInit;
import com.mcwfences.kikoz.init.TabInit;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod("mcwfences")
public class MacawsFences {
   public static final String MOD_ID = "mcwfences";

   public MacawsFences(IEventBus bus) {
      ItemInit.ITEMS.register(bus);
      BlockInit.BLOCKS.register(bus);
      TabInit.CREATIVE_TABS.register(bus);
   }
}
