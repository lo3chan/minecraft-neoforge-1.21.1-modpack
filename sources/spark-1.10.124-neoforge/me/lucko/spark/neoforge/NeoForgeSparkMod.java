package me.lucko.spark.neoforge;

import java.nio.file.Path;
import me.lucko.spark.neoforge.plugin.NeoForgeClientSparkPlugin;
import me.lucko.spark.neoforge.plugin.NeoForgeServerSparkPlugin;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;

@Mod("spark")
public class NeoForgeSparkMod {
   private final ModContainer container;
   private final Path configDirectory;

   public NeoForgeSparkMod(ModContainer container, IEventBus eventBus) {
      this.container = container;
      this.configDirectory = FMLPaths.CONFIGDIR.get().resolve(this.container.getModId());
      eventBus.addListener(this::clientInit);
      NeoForge.EVENT_BUS.register(this);
   }

   public String getVersion() {
      return this.container.getModInfo().getVersion().toString();
   }

   public void clientInit(FMLClientSetupEvent e) {
      NeoForgeClientSparkPlugin.register(this, e);
   }

   @SubscribeEvent
   public void serverInit(ServerAboutToStartEvent e) {
      NeoForgeServerSparkPlugin.register(this, e);
   }

   public Path getConfigDirectory() {
      return this.configDirectory;
   }
}
