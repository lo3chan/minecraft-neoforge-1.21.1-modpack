package io.wispforest.owo.client;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import io.wispforest.owo.Owo;
import io.wispforest.owo.client.screens.ScreenInternals;
import io.wispforest.owo.command.debug.OwoDebugCommands;
import io.wispforest.owo.config.OwoConfigCommand;
import io.wispforest.owo.config.ui.ConfigScreenProviders;
import io.wispforest.owo.itemgroup.json.OwoItemGroupLoader;
import io.wispforest.owo.moddata.ModDataLoader;
import io.wispforest.owo.shader.BlurProgram;
import io.wispforest.owo.shader.GlProgram;
import io.wispforest.owo.ui.parsing.UIModelLoader;
import io.wispforest.owo.ui.util.NinePatchTexture;
import net.minecraft.Util;
import net.minecraft.Util.OS;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForge;
import org.jetbrains.annotations.ApiStatus.Internal;

@OnlyIn(Dist.CLIENT)
@Mod(
   value = "owo",
   dist = {Dist.CLIENT}
)
@Internal
public class OwoClient {
   private static final String LINUX_RENDERDOC_WARNING = "\n========================================\nIgnored 'owo.renderdocPath' property as this Minecraft instance is not running on Windows.\nPlease populate the LD_PRELOAD environment variable instead\n========================================";
   private static final String MAC_RENDERDOC_WARNING = "\n========================================\nIgnored 'owo.renderdocPath' property as this Minecraft instance is not running on Windows.\nRenderDoc is not supported on macOS\n========================================";
   private static final String GENERIC_RENDERDOC_WARNING = "\n========================================\nIgnored 'owo.renderdocPath' property as this Minecraft instance is not running on Windows.\n========================================";
   public static final GlProgram HSV_PROGRAM = new GlProgram(ResourceLocation.fromNamespaceAndPath("owo", "spectrum"), DefaultVertexFormat.POSITION_COLOR);
   public static final BlurProgram BLUR_PROGRAM = new BlurProgram();

   public OwoClient(IEventBus modBus) {
      ModDataLoader.load(OwoItemGroupLoader.INSTANCE);
      OwoItemGroupLoader.initItemGroupCallback();
      modBus.addListener(event -> {
         event.registerReloadListener(new UIModelLoader());
         event.registerReloadListener(new NinePatchTexture.MetadataLoader());
      });
      if (Owo.DEBUG) {
         String renderdocPath = System.getProperty("owo.renderdocPath");
         if (renderdocPath != null) {
            if (Util.getPlatform() == OS.WINDOWS) {
               System.load(renderdocPath);
            } else {
               Owo.LOGGER
                  .warn(
                     switch (Util.getPlatform()) {
                        case LINUX -> "\n========================================\nIgnored 'owo.renderdocPath' property as this Minecraft instance is not running on Windows.\nPlease populate the LD_PRELOAD environment variable instead\n========================================";
                        case OSX -> "\n========================================\nIgnored 'owo.renderdocPath' property as this Minecraft instance is not running on Windows.\nRenderDoc is not supported on macOS\n========================================";
                        default -> "\n========================================\nIgnored 'owo.renderdocPath' property as this Minecraft instance is not running on Windows.\n========================================";
                     }
                  );
            }
         }
      }

      ScreenInternals.Client.init();
      NeoForge.EVENT_BUS.addListener(event -> OwoConfigCommand.register(event.getDispatcher(), event.getBuildContext()));
      if (Owo.DEBUG) {
         OwoDebugCommands.Client.register();
      }

      modBus.addListener(
         FMLClientSetupEvent.class,
         event -> ConfigScreenProviders.forEach(
            (modId, screenFactory) -> ModList.get()
               .getModContainerById(modId)
               .ifPresent(
                  mod -> mod.registerExtensionPoint(
                     IConfigScreenFactory.class, (IConfigScreenFactory)(modContainer, modsScreen) -> (Screen)screenFactory.apply(modsScreen)
                  )
               )
         )
      );
   }
}
