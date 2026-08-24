package dev.isxander.yacl3.platform;

import dev.isxander.yacl3.gui.image.YACLImageReloadListener;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;

@Mod("yet_another_config_lib_v3")
public class PlatformEntrypoint {
   public PlatformEntrypoint(IEventBus modEventBus) {
      YACLConfig.HANDLER.load();
      modEventBus.addListener(RegisterClientReloadListenersEvent.class, event -> event.registerReloadListener(new YACLImageReloadListener()));
   }
}
