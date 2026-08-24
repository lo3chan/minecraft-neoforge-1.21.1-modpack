package com.aetherteam.aether.data;

import com.aetherteam.aether.block.FreezingBlock;
import com.aetherteam.aether.item.AetherItems;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import java.util.Map;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.neoforged.neoforge.event.AddReloadListenerEvent;

public class ReloadListeners {
   public static void reloadListenerSetup(AddReloadListenerEvent event) {
      event.addListener(new ReloadListeners.RecipeReloadListener());
      event.addListener(new ReloadListeners.BannerReloadListener());
   }

   public static class BannerReloadListener extends SimpleJsonResourceReloadListener {
      public static final Gson GSON_INSTANCE = new GsonBuilder().create();

      public BannerReloadListener() {
         super(GSON_INSTANCE, Registries.elementsDirPath(Registries.BANNER_PATTERN));
      }

      protected void apply(Map<ResourceLocation, JsonElement> object, ResourceManager resourceManager, ProfilerFiller profiler) {
         AetherItems.SWET_BANNER = null;
      }
   }

   public static class RecipeReloadListener extends SimpleJsonResourceReloadListener {
      public static final Gson GSON_INSTANCE = new GsonBuilder().create();

      public RecipeReloadListener() {
         super(GSON_INSTANCE, Registries.elementsDirPath(Registries.RECIPE));
      }

      protected void apply(Map<ResourceLocation, JsonElement> object, ResourceManager resourceManager, ProfilerFiller profiler) {
         FreezingBlock.cachedBlocks.clear();
         FreezingBlock.cachedResults.clear();
      }
   }
}
