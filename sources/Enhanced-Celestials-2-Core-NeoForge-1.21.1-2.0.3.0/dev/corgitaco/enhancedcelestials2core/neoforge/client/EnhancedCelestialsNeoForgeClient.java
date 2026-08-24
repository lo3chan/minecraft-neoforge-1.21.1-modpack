package dev.corgitaco.enhancedcelestials2core.neoforge.client;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import dev.corgitaco.enhancedcelestials2core.client.EnhancedCelestialsDebugOverlay;
import net.minecraft.commands.Commands;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent;
import net.neoforged.neoforge.client.event.RenderGuiEvent.Post;
import net.neoforged.neoforge.common.NeoForge;

public class EnhancedCelestialsNeoForgeClient {
   public static void clientSetup(FMLClientSetupEvent event) {
      NeoForge.EVENT_BUS.addListener(EnhancedCelestialsNeoForgeClient::registerClientCommands);
      NeoForge.EVENT_BUS.addListener(EnhancedCelestialsNeoForgeClient::renderDebugOverlay);
   }

   private static void registerClientCommands(RegisterClientCommandsEvent event) {
      event.getDispatcher().register((LiteralArgumentBuilder)Commands.literal("ec").then(Commands.literal("debug").executes(context -> {
         EnhancedCelestialsDebugOverlay.toggle();
         return 1;
      })));
   }

   private static void renderDebugOverlay(Post event) {
      EnhancedCelestialsDebugOverlay.render(event.getGuiGraphics(), event.getPartialTick());
   }
}
