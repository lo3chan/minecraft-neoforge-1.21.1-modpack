package com.seibel.distanthorizons.common.wrappers;

import com.seibel.distanthorizons.api.enums.config.EDhApiRenderingApi;
import com.seibel.distanthorizons.api.enums.config.EDhApiRenderingEngine;
import com.seibel.distanthorizons.api.interfaces.render.IDhApiCustomRenderObjectFactory;
import com.seibel.distanthorizons.common.render.openGl.GlDhRenderApiDefinition_neoforge;
import com.seibel.distanthorizons.common.wrappers.block.BlockStateTextureProvider_neoforge;
import com.seibel.distanthorizons.common.wrappers.gui.LangWrapper_neoforge;
import com.seibel.distanthorizons.common.wrappers.gui.classicConfig.ClassicConfigGUI_neoforge;
import com.seibel.distanthorizons.common.wrappers.level.KeyedClientLevelManager_neoforge;
import com.seibel.distanthorizons.common.wrappers.minecraft.MinecraftClientWrapper_neoforge;
import com.seibel.distanthorizons.common.wrappers.minecraft.MinecraftRenderWrapper_neoforge;
import com.seibel.distanthorizons.common.wrappers.minecraft.MinecraftServerWrapper_neoforge;
import com.seibel.distanthorizons.core.config.Config;
import com.seibel.distanthorizons.core.dependencyInjection.SingletonInjector;
import com.seibel.distanthorizons.core.level.IKeyedClientLevelManager;
import com.seibel.distanthorizons.core.logging.DhLogger;
import com.seibel.distanthorizons.core.logging.DhLoggerBuilder;
import com.seibel.distanthorizons.core.render.renderer.GenericRenderObjectFactory;
import com.seibel.distanthorizons.core.wrapperInterfaces.IVersionConstants;
import com.seibel.distanthorizons.core.wrapperInterfaces.IWrapperFactory;
import com.seibel.distanthorizons.core.wrapperInterfaces.block.IBlockStateFaceTextureProvider;
import com.seibel.distanthorizons.core.wrapperInterfaces.config.IConfigGui;
import com.seibel.distanthorizons.core.wrapperInterfaces.config.ILangWrapper;
import com.seibel.distanthorizons.core.wrapperInterfaces.minecraft.IMinecraftClientWrapper;
import com.seibel.distanthorizons.core.wrapperInterfaces.minecraft.IMinecraftRenderWrapper;
import com.seibel.distanthorizons.core.wrapperInterfaces.minecraft.IMinecraftSharedWrapper;
import com.seibel.distanthorizons.core.wrapperInterfaces.render.AbstractDhRenderApiDefinition;

public class DependencySetup_neoforge {
   protected static final DhLogger LOGGER = new DhLoggerBuilder().build();
   private static boolean renderingApiBindingsSet = false;

   public static void createSharedBindings() {
      SingletonInjector.INSTANCE.bind(ILangWrapper.class, LangWrapper_neoforge.INSTANCE);
      SingletonInjector.INSTANCE.bind(IVersionConstants.class, VersionConstants.INSTANCE);
      SingletonInjector.INSTANCE.bind(IWrapperFactory.class, WrapperFactory_neoforge.INSTANCE);
      SingletonInjector.INSTANCE.bind(IKeyedClientLevelManager.class, new KeyedClientLevelManager_neoforge());
      SingletonInjector.INSTANCE.bind(IDhApiCustomRenderObjectFactory.class, GenericRenderObjectFactory.INSTANCE);
   }

   public static void createServerBindings() {
      SingletonInjector.INSTANCE.bind(IMinecraftSharedWrapper.class, MinecraftServerWrapper_neoforge.INSTANCE);
   }

   public static void createClientBindings() {
      SingletonInjector.INSTANCE.bind(IMinecraftClientWrapper.class, MinecraftClientWrapper_neoforge.INSTANCE);
      SingletonInjector.INSTANCE.bind(IMinecraftSharedWrapper.class, MinecraftClientWrapper_neoforge.INSTANCE);
      SingletonInjector.INSTANCE.bind(IMinecraftRenderWrapper.class, MinecraftRenderWrapper_neoforge.INSTANCE);
      SingletonInjector.INSTANCE.bind(IConfigGui.class, ClassicConfigGUI_neoforge.CONFIG_CORE_INTERFACE);
      SingletonInjector.INSTANCE.bind(IBlockStateFaceTextureProvider.class, BlockStateTextureProvider_neoforge.INSTANCE);
   }

   public static synchronized void setRenderingApiBindings() {
      if (renderingApiBindingsSet) {
         LOGGER.warn("Rendering bindings already set, skipping. How did this happen?");
      } else {
         renderingApiBindingsSet = true;
         EDhApiRenderingEngine renderingApiEnum = Config.Client.Advanced.Graphics.Experimental.renderingEngine.get();
         if (renderingApiEnum == EDhApiRenderingEngine.AUTO) {
            IVersionConstants versionConstants = SingletonInjector.INSTANCE.get(IVersionConstants.class);
            renderingApiEnum = versionConstants.getDefaultRenderingEngine();
         }

         LOGGER.info("Setting DH Rendering API to: [" + renderingApiEnum + "]...");
         AbstractDhRenderApiDefinition renderDefinition;
         boolean validApi;
         if (renderingApiEnum == EDhApiRenderingEngine.OPEN_GL) {
            validApi = true;
            renderDefinition = new GlDhRenderApiDefinition_neoforge();
         } else {
            if (renderingApiEnum != EDhApiRenderingEngine.BLAZE_3D) {
               String message = "No ["
                  + AbstractDhRenderApiDefinition.class.getSimpleName()
                  + "] concrete implementation found for the value: ["
                  + renderingApiEnum
                  + "].";
               LOGGER.fatal(message);
               throw new IllegalStateException(message);
            }

            validApi = false;
            renderDefinition = null;
         }

         if (!validApi) {
            String message = "The Distant Horizons rendering engine ["
               + renderDefinition.getEngineName()
               + "]-["
               + renderingApiEnum
               + "] is not supported with this Minecraft config, reverting to ["
               + EDhApiRenderingEngine.AUTO
               + "].";
            LOGGER.fatal(message);
            Config.Client.Advanced.Graphics.Experimental.renderingEngine.set(EDhApiRenderingEngine.AUTO);
            throw new IllegalStateException(message);
         } else {
            EDhApiRenderingApi mcRenderApi = MinecraftRenderWrapper_neoforge.INSTANCE.getMcRenderingApi();
            if (mcRenderApi != renderDefinition.getRenderApi()) {
               String message = "The Distant Horizons rendering engine ["
                  + renderDefinition.getEngineName()
                  + "]-["
                  + renderDefinition.getRenderApi().name()
                  + "] cannot be used since it's API doesn't match what Minecraft is currently set to use ["
                  + mcRenderApi.name()
                  + "]. Please either change Minecraft's rendering API or Distant Horizons'.";
               LOGGER.fatal(message);
               throw new IllegalStateException(message);
            } else {
               renderDefinition.bindRenderers();
               LOGGER.info("DH Rendering successfully bound to: [" + renderDefinition.getEngineName() + "]...");
            }
         }
      }
   }
}
