package net.diebuddies.physics;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;

@Mod("physicsmod")
public class ModExecutor {
   public ModExecutor(IEventBus modEventBus) {
      if (FMLEnvironment.dist.isClient()) {
         StarterClient.onInitializeClient(modEventBus);
      }

      if (FMLEnvironment.dist.isDedicatedServer()) {
         StarterServer.onInitializeServer();
      }
   }
}
