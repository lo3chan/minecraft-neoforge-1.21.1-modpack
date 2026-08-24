package vazkii.akashictome;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig.Type;
import vazkii.akashictome.network.NetworkHandler;
import vazkii.akashictome.proxy.ClientProxy;
import vazkii.akashictome.proxy.CommonProxy;

@Mod("akashictome")
public class AkashicTome {
   public static final String MOD_ID = "akashictome";
   public static CommonProxy proxy;

   public AkashicTome(IEventBus bus, ModContainer modContainer, Dist dist) {
      bus.addListener(NetworkHandler::registerPayloadHandler);
      Registries.ITEMS.register(bus);
      Registries.DATA_COMPONENTS.register(bus);
      Registries.SERIALIZERS.register(bus);
      modContainer.registerConfig(Type.COMMON, ConfigHandler.CONFIG_SPEC);
      proxy = (CommonProxy)(dist.isClient() ? new ClientProxy() : new CommonProxy());
      proxy.preInit();
   }
}
