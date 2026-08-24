package com.mcwroofs.kikoz;

import com.mcwroofs.kikoz.init.BlockInit;
import com.mcwroofs.kikoz.init.ItemInit;
import com.mcwroofs.kikoz.init.TabInit;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod("mcwroofs")
public class MacawsRoofs {
   public static final String MOD_ID = "mcwroofs";

   public MacawsRoofs(IEventBus bus) {
      ItemInit.ITEMS.register(bus);
      BlockInit.BLOCKS.register(bus);
      TabInit.CREATIVE_TABS.register(bus);
   }
}
