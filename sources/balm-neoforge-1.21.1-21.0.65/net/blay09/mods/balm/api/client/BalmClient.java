package net.blay09.mods.balm.api.client;

import java.util.function.Consumer;
import net.blay09.mods.balm.api.BalmRuntimeLoadContext;
import net.blay09.mods.balm.api.client.commands.BalmClientCommands;
import net.blay09.mods.balm.api.client.keymappings.BalmKeyMappings;
import net.blay09.mods.balm.api.client.module.BalmClientModule;
import net.blay09.mods.balm.api.client.rendering.BalmModels;
import net.blay09.mods.balm.api.client.rendering.BalmRenderers;
import net.blay09.mods.balm.api.client.rendering.BalmTextures;
import net.blay09.mods.balm.api.client.screen.BalmScreens;
import net.blay09.mods.balm.client.BalmClientRegistrars;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;

public class BalmClient {
   private static final BalmClientRuntime<BalmRuntimeLoadContext> runtime = BalmClientRuntimeSpi.create();

   @Deprecated
   public static void registerModule(BalmClientModule module) {
      runtime.registerModule(module);
   }

   @Deprecated
   public static void onRuntimeAvailable(Runnable callback) {
      runtime.onRuntimeAvailable(callback);
   }

   @Deprecated(
      since = "1.22"
   )
   public static <T extends BalmRuntimeLoadContext> void initialize(String modId, T context, Runnable initializer) {
      initializeMod(modId, context, initializer);
   }

   @Deprecated
   public static <T extends BalmRuntimeLoadContext> void initializeMod(String modId, T context, Runnable initializer) {
      runtime.initializeMod(modId, context, initializer);
   }

   public static <T extends BalmRuntimeLoadContext> void initializeMod(String modId, T context, BalmClientModule module) {
      runtime.initializeMod(modId, context, registrars -> registrars.registerModule(module));
   }

   public static <T extends BalmRuntimeLoadContext> void initializeMod(String modId, T context, BalmClientModule... modules) {
      runtime.initializeMod(modId, context, registrars -> {
         for (BalmClientModule module : modules) {
            registrars.registerModule(module);
         }
      });
   }

   public static void initializeMod(String modId, BalmRuntimeLoadContext context, Consumer<BalmClientRegistrars> initializer) {
      runtime.initializeMod(modId, context, initializer);
   }

   public static BalmRenderers getRenderers() {
      return runtime.getRenderers();
   }

   @Deprecated
   public static BalmKeyMappings getKeyMappings() {
      return runtime.getKeyMappings();
   }

   @Deprecated
   public static BalmScreens getScreens() {
      return runtime.getScreens();
   }

   public static BalmModels getModels() {
      return runtime.getModels();
   }

   public static BalmClientRuntime<? extends BalmRuntimeLoadContext> getRuntime() {
      return runtime;
   }

   @Deprecated
   public static void addResourceReloadListener(ResourceLocation identifier, PreparableReloadListener reloadListener) {
      runtime.addResourceReloadListener(identifier, reloadListener);
   }

   @Deprecated
   public static BalmTextures getTextures() {
      return runtime.getTextures();
   }

   public static BalmClientCommands clientCommands() {
      return runtime.clientCommands();
   }
}
