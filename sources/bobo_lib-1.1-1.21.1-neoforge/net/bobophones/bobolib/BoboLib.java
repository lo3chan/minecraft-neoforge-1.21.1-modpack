package net.bobophones.bobolib;

import net.bobophones.bobolib.registry.ModItems;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;

@Mod("bobo_lib")
public class BoboLib {
   public static final String id = "bobo_lib";

   public BoboLib(IEventBus bus, ModContainer container) {
      ModItems.Register(bus);
   }
}
